package io.awesome.gagtube.util.text;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import io.awesome.gagtube.util.NavigationHelper;
import io.awesome.gagtube.util.SharedUtils;

final class HashtagLongPressClickableSpan extends LongPressClickableSpan {

    @NonNull
    private final Context context;
    @NonNull
    private final String parsedHashtag;
    private final int relatedInfoServiceId;

    HashtagLongPressClickableSpan(@NonNull final Context context,
                                  @NonNull final String parsedHashtag,
                                  final int relatedInfoServiceId) {
        this.context = context;
        this.parsedHashtag = parsedHashtag;
        this.relatedInfoServiceId = relatedInfoServiceId;
    }

    @Override
    public void onClick(@NonNull final View view) {
        NavigationHelper.openSearch(context, relatedInfoServiceId, parsedHashtag);
    }

    @Override
    public void onLongClick(@NonNull final View view) {
        SharedUtils.copyToClipboard(context, parsedHashtag);
    }
}
