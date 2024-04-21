package io.awesome.gagtube.util;

import static io.awesome.gagtube.util.InternalUrlsHandler.playOnPopup;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;

import org.schabi.newpipe.extractor.Info;
import org.schabi.newpipe.extractor.stream.StreamInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.noties.markwon.Markwon;
import io.noties.markwon.linkify.LinkifyPlugin;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public final class TextLinkUtils {

    public static final String TAG = TextLinkUtils.class.getSimpleName();

    // Looks for hashtags with characters from any language (\p{L}), numbers, or underscores
    private static final Pattern HASHTAGS_PATTERN = Pattern.compile("(#[\\p{L}0-9_]+)");

    private TextLinkUtils() {
    }

    public static void createLinksFromHtmlBlock(@NonNull final TextView textView, final String htmlBlock, final int htmlCompatFlag, @Nullable final Info relatedInfo, final CompositeDisposable disposables) {
        changeIntentsOfDescriptionLinks(textView, HtmlCompat.fromHtml(htmlBlock, htmlCompatFlag), relatedInfo, disposables);
    }

    public static void createLinksFromPlainText(@NonNull final TextView textView, final String plainTextBlock, @Nullable final Info relatedInfo, final CompositeDisposable disposables) {
        textView.setAutoLinkMask(Linkify.WEB_URLS);
        textView.setText(plainTextBlock, TextView.BufferType.SPANNABLE);
        changeIntentsOfDescriptionLinks(textView, textView.getText(), relatedInfo, disposables);
    }

    public static void createLinksFromMarkdownText(@NonNull final TextView textView, final String markdownBlock, @Nullable final Info relatedInfo, final CompositeDisposable disposables) {
        final Markwon markwon = Markwon.builder(textView.getContext()).usePlugin(LinkifyPlugin.create()).build();
        changeIntentsOfDescriptionLinks(textView, markwon.toMarkdown(markdownBlock), relatedInfo, disposables);
    }

    private static void addClickListenersOnHashtags(final Context context, @NonNull final SpannableStringBuilder spannableDescription, final Info relatedInfo) {
        final String descriptionText = spannableDescription.toString();
        final Matcher hashtagsMatches = HASHTAGS_PATTERN.matcher(descriptionText);

        while (hashtagsMatches.find()) {
            final int hashtagStart = hashtagsMatches.start(1);
            final int hashtagEnd = hashtagsMatches.end(1);
            final String parsedHashtag = descriptionText.substring(hashtagStart, hashtagEnd);

            // don't add a ClickableSpan if there is already one, which should be a part of an URL,
            // already parsed before
            if (spannableDescription.getSpans(hashtagStart, hashtagEnd, ClickableSpan.class).length == 0) {
                spannableDescription.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull final View view) {
                        NavigationHelper.openSearch(context, relatedInfo.getServiceId(), parsedHashtag);
                    }
                }, hashtagStart, hashtagEnd, 0);
            }
        }
    }

    /**
     * Add click listeners which opens the popup player on timestamps in a plain text.
     * <p>
     * This method finds all timestamps in the {@link SpannableStringBuilder} of the description
     * using a regular expression, adds for each a {@link ClickableSpan} which opens the popup
     * player at the time indicated in the timestamps.
     *
     * @param context              the context to use
     * @param spannableDescription the SpannableStringBuilder with the text of the
     *                             content description
     * @param relatedInfo          what to open in the popup player when timestamps are clicked
     * @param disposables          disposables created by the method are added here and their
     *                             lifecycle should be handled by the calling class
     */
    private static void addClickListenersOnTimestamps(final Context context, @NonNull final SpannableStringBuilder spannableDescription, final Info relatedInfo, final CompositeDisposable disposables) {
        final String descriptionText = spannableDescription.toString();
        final Matcher timestampsMatches = TimestampExtractor.TIMESTAMPS_PATTERN.matcher(descriptionText);

        while (timestampsMatches.find()) {
            final TimestampExtractor.TimestampMatchDTO timestampMatchDTO = TimestampExtractor.getTimestampFromMatcher(timestampsMatches, descriptionText);

            if (timestampMatchDTO == null) {
                continue;
            }

            spannableDescription.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull final View view) {
                    playOnPopup(context, relatedInfo.getUrl(), relatedInfo.getService(), timestampMatchDTO.seconds(), disposables);
                }
            }, timestampMatchDTO.timestampStart(), timestampMatchDTO.timestampEnd(), 0);
        }
    }

    private static void changeIntentsOfDescriptionLinks(final TextView textView, final CharSequence chars, @Nullable final Info relatedInfo, final CompositeDisposable disposables) {
        disposables.add(Single.fromCallable(() -> {
                    final Context context = textView.getContext();

                    // add custom click actions on web links
                    final SpannableStringBuilder textBlockLinked = new SpannableStringBuilder(chars);
                    final URLSpan[] urls = textBlockLinked.getSpans(0, chars.length(), URLSpan.class);

                    for (final URLSpan span : urls) {
                        final String url = span.getURL();
                        final ClickableSpan clickableSpan = new ClickableSpan() {
                            public void onClick(@NonNull final View view) {
                                if (!InternalUrlsHandler.handleUrlDescriptionTimestamp(new CompositeDisposable(), context, url)) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    context.startActivity(intent);
                                }
                            }
                        };

                        textBlockLinked.setSpan(clickableSpan, textBlockLinked.getSpanStart(span), textBlockLinked.getSpanEnd(span), textBlockLinked.getSpanFlags(span));
                        textBlockLinked.removeSpan(span);
                    }

                    // add click actions on plain text timestamps only for description of contents,
                    // unneeded for meta-info or other TextViews
                    if (relatedInfo != null) {
                        if (relatedInfo instanceof StreamInfo) {
                            addClickListenersOnTimestamps(context, textBlockLinked, relatedInfo, disposables);
                        }
                        addClickListenersOnHashtags(context, textBlockLinked, relatedInfo);
                    }

                    return textBlockLinked;
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(textBlockLinked -> setTextViewCharSequence(textView, textBlockLinked), throwable -> {
                    Log.e(TAG, "Unable to linkify text", throwable);
                    // this should never happen, but if it does, just fallback to it
                    setTextViewCharSequence(textView, chars);
                }));
    }

    private static void setTextViewCharSequence(@NonNull final TextView textView, final CharSequence charSequence) {
        textView.setText(charSequence);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setVisibility(View.VISIBLE);
    }
}