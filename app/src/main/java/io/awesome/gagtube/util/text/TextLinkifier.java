package io.awesome.gagtube.util.text;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;

import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.stream.Description;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.noties.markwon.Markwon;
import io.noties.markwon.linkify.LinkifyPlugin;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public final class TextLinkifier {

    // Looks for hashtags with characters from any language (\p{L}), numbers, or underscores
    private static final Pattern HASHTAGS_PATTERN = Pattern.compile("(#[\\p{L}0-9_]+)");

    public static final Consumer<TextView> SET_LINK_MOVEMENT_METHOD =
            v -> v.setMovementMethod(LongPressLinkMovementMethod.getInstance());

    private TextLinkifier() {
    }

    public static void fromDescription(@NonNull final TextView textView,
                                       final Description description,
                                       final int htmlCompatFlag,
                                       @Nullable final StreamingService relatedInfoService,
                                       @Nullable final String relatedStreamUrl,
                                       @NonNull final CompositeDisposable disposables,
                                       @Nullable final Consumer<TextView> onCompletion) {
        if (description == null) return;
        switch (description.getType()) {
            case Description.HTML:
                TextLinkifier.fromHtml(textView, description.getContent(), htmlCompatFlag,
                        relatedInfoService, relatedStreamUrl, disposables, onCompletion);
                break;
            case Description.MARKDOWN:
                TextLinkifier.fromMarkdown(textView, description.getContent(),
                        relatedInfoService, relatedStreamUrl, disposables, onCompletion);
                break;
            case Description.PLAIN_TEXT:
            default:
                TextLinkifier.fromPlainText(textView, description.getContent(),
                        relatedInfoService, relatedStreamUrl, disposables, onCompletion);
                break;
        }
    }

    public static void fromHtml(@NonNull final TextView textView,
                                @NonNull final String htmlBlock,
                                final int htmlCompatFlag,
                                @Nullable final StreamingService relatedInfoService,
                                @Nullable final String relatedStreamUrl,
                                @NonNull final CompositeDisposable disposables,
                                @Nullable final Consumer<TextView> onCompletion) {
        changeLinkIntents(
                textView, HtmlCompat.fromHtml(htmlBlock, htmlCompatFlag), relatedInfoService,
                relatedStreamUrl, disposables, onCompletion);
    }

    public static void fromPlainText(@NonNull final TextView textView,
                                     @NonNull final String plainTextBlock,
                                     @Nullable final StreamingService relatedInfoService,
                                     @Nullable final String relatedStreamUrl,
                                     @NonNull final CompositeDisposable disposables,
                                     @Nullable final Consumer<TextView> onCompletion) {
        textView.setAutoLinkMask(Linkify.WEB_URLS);
        textView.setText(plainTextBlock, TextView.BufferType.SPANNABLE);
        changeLinkIntents(textView, textView.getText(), relatedInfoService,
                relatedStreamUrl, disposables, onCompletion);
    }

    public static void fromMarkdown(@NonNull final TextView textView,
                                    @NonNull final String markdownBlock,
                                    @Nullable final StreamingService relatedInfoService,
                                    @Nullable final String relatedStreamUrl,
                                    @NonNull final CompositeDisposable disposables,
                                    @Nullable final Consumer<TextView> onCompletion) {
        final Markwon markwon = Markwon.builder(textView.getContext())
                .usePlugin(LinkifyPlugin.create()).build();
        changeLinkIntents(textView, markwon.toMarkdown(markdownBlock),
                relatedInfoService, relatedStreamUrl, disposables, onCompletion);
    }

    private static void changeLinkIntents(@NonNull final TextView textView,
                                          @NonNull final CharSequence chars,
                                          @Nullable final StreamingService relatedInfoService,
                                          @Nullable final String relatedStreamUrl,
                                          @NonNull final CompositeDisposable disposables,
                                          @Nullable final Consumer<TextView> onCompletion) {
        disposables.add(Single.fromCallable(() -> {
                    final Context context = textView.getContext();

                    // add custom click actions on web links
                    final SpannableStringBuilder textBlockLinked =
                            new SpannableStringBuilder(chars);
                    final URLSpan[] urls = textBlockLinked.getSpans(0, chars.length(),
                            URLSpan.class);

                    for (final URLSpan span : urls) {
                        final String url = span.getURL();
                        final LongPressClickableSpan longPressClickableSpan = new UrlLongPressClickableSpan(context, disposables, url);

                        textBlockLinked.setSpan(longPressClickableSpan,
                                textBlockLinked.getSpanStart(span),
                                textBlockLinked.getSpanEnd(span),
                                textBlockLinked.getSpanFlags(span));
                        textBlockLinked.removeSpan(span);
                    }

                    // add click actions on plain text timestamps only for description of contents,
                    // unneeded for meta-info or other TextViews
                    if (relatedInfoService != null) {
                        if (relatedStreamUrl != null) {
                            addClickListenersOnTimestamps(context, textBlockLinked,
                                    relatedInfoService, relatedStreamUrl, disposables);
                        }
                        addClickListenersOnHashtags(context, textBlockLinked, relatedInfoService);
                    }

                    return textBlockLinked;
                }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(textBlockLinked -> setTextViewCharSequence(textView, textBlockLinked, onCompletion),
                        throwable -> {
                            // this should never happen, but if it does, just fallback to it
                            setTextViewCharSequence(textView, chars, onCompletion);
                        }));
    }

    private static void addClickListenersOnHashtags(
            @NonNull final Context context,
            @NonNull final SpannableStringBuilder spannableDescription,
            @NonNull final StreamingService relatedInfoService) {
        final String descriptionText = spannableDescription.toString();
        final Matcher hashtagsMatches = HASHTAGS_PATTERN.matcher(descriptionText);

        while (hashtagsMatches.find()) {
            final int hashtagStart = hashtagsMatches.start(1);
            final int hashtagEnd = hashtagsMatches.end(1);
            final String parsedHashtag = descriptionText.substring(hashtagStart, hashtagEnd);

            // Don't add a LongPressClickableSpan if there is already one, which should be a part
            // of an URL, already parsed before
            if (spannableDescription.getSpans(hashtagStart, hashtagEnd, LongPressClickableSpan.class).length == 0) {
                final int serviceId = relatedInfoService.getServiceId();
                spannableDescription.setSpan(new HashtagLongPressClickableSpan(context, parsedHashtag, serviceId), hashtagStart, hashtagEnd, 0);
            }
        }
    }

    private static void addClickListenersOnTimestamps(
            @NonNull final Context context,
            @NonNull final SpannableStringBuilder spannableDescription,
            @NonNull final StreamingService relatedInfoService,
            @NonNull final String relatedStreamUrl,
            @NonNull final CompositeDisposable disposables) {
        final String descriptionText = spannableDescription.toString();
        final Matcher timestampsMatches = TimestampExtractor.TIMESTAMPS_PATTERN.matcher(descriptionText);

        while (timestampsMatches.find()) {
            final TimestampExtractor.TimestampMatchDTO timestampMatchDTO = TimestampExtractor.getTimestampFromMatcher(timestampsMatches, descriptionText);

            if (timestampMatchDTO == null) {
                continue;
            }

            spannableDescription.setSpan(
                    new TimestampLongPressClickableSpan(context, descriptionText, disposables, relatedInfoService, relatedStreamUrl, timestampMatchDTO),
                    timestampMatchDTO.timestampStart(),
                    timestampMatchDTO.timestampEnd(),
                    0);
        }
    }

    private static void setTextViewCharSequence(@NonNull final TextView textView,
                                                @Nullable final CharSequence charSequence,
                                                @Nullable final Consumer<TextView> onCompletion) {
        textView.setText(charSequence);
        textView.setVisibility(View.VISIBLE);
        if (onCompletion != null) {
            onCompletion.accept(textView);
        }
    }
}
