package io.awesome.gagtube.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.annimon.stream.Optional;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.Locale;

public class AppUtils {

    public static boolean isOnline(@NonNull Context context) {

        // true if online
        return Optional.ofNullable(((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)))
                .map(ConnectivityManager::getActiveNetworkInfo)
                .map(NetworkInfo::isConnected)
                .orElse(false);
    }

    public static String getDeviceCountryIso(Context context) {

        // get device country by sim card (most accurate)
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceCountry = null;
        if (tm != null) {
            deviceCountry = tm.getSimCountryIso().toUpperCase();
        }

        // if no deviceCountry by sim card, try locale
        if (TextUtils.isEmpty(deviceCountry)) {
            deviceCountry = Locale.getDefault().getCountry();
        }

        return deviceCountry;
    }

    public static int dpToPx(Context context, float dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }

    public static String getCountryCode(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(Constants.COUNTRY_CODE, Locale.getDefault().getCountry());
    }

    public static boolean isDownloadVisible(Context context) {
        return SharedPrefsHelper.getBooleanPrefs(context, SharedPrefsHelper.Key.SHOW_DOWNLOAD.name(), true);
    }

    public static boolean isShowInterstitialDownload(Context context) {
        return SharedPrefsHelper.getBooleanPrefs(context, SharedPrefsHelper.Key.SHOW_INTERSTITIAL_DOWNLOAD.name(), true);
    }
}