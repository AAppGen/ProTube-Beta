package io.awesome.gagtube.util.text;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import io.awesome.gagtube.util.SharedUtils;
import io.reactivex.disposables.CompositeDisposable;

final class UrlLongPressClickableSpan extends LongPressClickableSpan {

    @NonNull
    private final Context context;
    @NonNull
    private final CompositeDisposable disposables;
    @NonNull
    private final String url;

    UrlLongPressClickableSpan(@NonNull final Context context,
                              @NonNull final CompositeDisposable disposables,
                              @NonNull final String url) {
        this.context = context;
        this.disposables = disposables;
        this.url = url;
    }

    @Override
    public void onClick(@NonNull final View view) {
        if (!InternalUrlsHandler.handleUrlDescriptionTimestamp(disposables, context, url)) {
            SharedUtils.openUrlInApp(context, url);
        }
    }

    @Override
    public void onLongClick(@NonNull final View view) {
        SharedUtils.copyToClipboard(context, url);
    }
}
