package io.awesome.gagtube.util.text;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.LinkHandlerFactory;
import org.schabi.newpipe.extractor.stream.StreamInfo;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.awesome.gagtube.player.playqueue.PlayQueue;
import io.awesome.gagtube.player.playqueue.SinglePlayQueue;
import io.awesome.gagtube.util.ExtractorHelper;
import io.awesome.gagtube.util.NavigationHelper;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public final class InternalUrlsHandler {
    private static final Pattern AMPERSAND_TIMESTAMP_PATTERN = Pattern.compile("(.*)&t=(\\d+)");
    private static final Pattern HASHTAG_TIMESTAMP_PATTERN =
            Pattern.compile("(.*)#timestamp=(\\d+)");

    private InternalUrlsHandler() {
    }

    public static boolean handleUrlCommentsTimestamp(@NonNull final CompositeDisposable disposables,
                                                     final Context context,
                                                     @NonNull final String url) {
        return handleUrl(context, url, HASHTAG_TIMESTAMP_PATTERN, disposables);
    }

    public static boolean handleUrlDescriptionTimestamp(@NonNull final CompositeDisposable disposables,
                                                        final Context context,
                                                        @NonNull final String url) {
        return handleUrl(context, url, AMPERSAND_TIMESTAMP_PATTERN, disposables);
    }

    private static boolean handleUrl(final Context context,
                                     @NonNull final String url,
                                     @NonNull final Pattern pattern,
                                     @NonNull final CompositeDisposable disposables) {
        final Matcher matcher = pattern.matcher(url);
        if (!matcher.matches()) {
            return false;
        }
        final String matchedUrl = matcher.group(1);
        final int seconds;
        if (matcher.group(2) == null) {
            seconds = -1;
        } else {
            seconds = Integer.parseInt(Objects.requireNonNull(matcher.group(2)));
        }

        final StreamingService service;
        final StreamingService.LinkType linkType;
        try {
            service = NewPipe.getServiceByUrl(matchedUrl);
            linkType = service.getLinkTypeByUrl(matchedUrl);
            if (linkType == StreamingService.LinkType.NONE) {
                return false;
            }
        } catch (final ExtractionException e) {
            return false;
        }

        if (linkType == StreamingService.LinkType.STREAM && seconds != -1) {
            return playOnPopup(context, matchedUrl, service, seconds, disposables);
        } else {
            return true;
        }
    }

    public static boolean playOnPopup(final Context context,
                                      final String url,
                                      @NonNull final StreamingService service,
                                      final int seconds,
                                      @NonNull final CompositeDisposable disposables) {
        final LinkHandlerFactory factory = service.getStreamLHFactory();
        final String cleanUrl;

        try {
            cleanUrl = factory.getUrl(factory.getId(url));
        } catch (final ParsingException e) {
            return false;
        }

        final Single<StreamInfo> single = ExtractorHelper.getStreamInfo(service.getServiceId(), cleanUrl, false);
        disposables.add(single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(info -> {
                    final PlayQueue playQueue = new SinglePlayQueue(info, seconds * 1000L);
                    NavigationHelper.playOnMainPlayer((AppCompatActivity) context, playQueue, true);
                }, throwable -> {
                }));
        return true;
    }
}
