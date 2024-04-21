package io.awesome.gagtube.util;

import android.content.Context;
import android.content.res.Configuration;
import android.util.TypedValue;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;

public class DeviceUtils {

    public static boolean isTablet(@NonNull final Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static boolean isLandscape(@NonNull final Context context) {
        return context.getResources().getDisplayMetrics().heightPixels < context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int dpToPx(@Dimension(unit = Dimension.DP) final int dp, @NonNull final Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int spToPx(@Dimension(unit = Dimension.SP) final int sp, @NonNull final Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
}
