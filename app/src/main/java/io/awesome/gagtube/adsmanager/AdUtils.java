package io.awesome.gagtube.adsmanager;

import android.app.Activity;
import android.content.Context;

import io.awesome.gagtube.App;
import io.awesome.gagtube.adsmanager.admob.AdMobInterstitialAd;
import io.awesome.gagtube.adsmanager.applovin.ApplovinInterstitialAd;
import io.awesome.gagtube.adsmanager.applovin.ApplovinInterstitialAdDownload;
import io.awesome.gagtube.adsmanager.facebook.FbInterstitialAd;
import io.awesome.gagtube.util.SharedPrefsHelper;

public class AdUtils {

    public static boolean ENABLE_ADMOB() {
        return SharedPrefsHelper.getBooleanPrefs(App.getAppContext(), SharedPrefsHelper.Key.SHOW_ADMOB_MAIN.name(), false);
    }

    public static boolean ENABLE_FACEBOOK() {
        return SharedPrefsHelper.getBooleanPrefs(App.getAppContext(), SharedPrefsHelper.Key.SHOW_FACEBOOK_MAIN.name(), false);
    }

    public static boolean ENABLE_APPLOVIN() {
        return SharedPrefsHelper.getBooleanPrefs(App.getAppContext(), SharedPrefsHelper.Key.SHOW_APPLOVIN_MAIN.name(), true);
    }

    public static void initInterstitialAd(Activity activity) {
        if (ENABLE_ADMOB()) {
            AdMobInterstitialAd.getInstance().init(activity);
        } else if (ENABLE_FACEBOOK()) {
            FbInterstitialAd.getInstance().init(activity);
        } else if (ENABLE_APPLOVIN()) {
            ApplovinInterstitialAd.getInstance().init(activity);
            ApplovinInterstitialAdDownload.getInstance().init(activity);
        }
    }

    public static void showInterstitialAd(Activity activity, Runnable callback) {
        if (ENABLE_ADMOB()) {
            AdMobInterstitialAd.getInstance().showInterstitialAd(activity, callback::run);
        } else if (ENABLE_FACEBOOK()) {
            FbInterstitialAd.getInstance().showInterstitialAd(activity, callback::run);
        } else if (ENABLE_APPLOVIN()) {
            ApplovinInterstitialAd.getInstance().showInterstitialAd(activity, callback::run);
        } else {
            callback.run();
        }
    }

    public static void showInterstitialAdDownload(Activity activity, Runnable callback) {
        if (ENABLE_ADMOB()) {
            AdMobInterstitialAd.getInstance().showInterstitialAd(activity, callback::run);
        } else if (ENABLE_FACEBOOK()) {
            FbInterstitialAd.getInstance().showInterstitialAd(activity, callback::run);
        } else if (ENABLE_APPLOVIN()) {
            ApplovinInterstitialAdDownload.getInstance().showInterstitialAd(activity, callback::run);
        } else {
            callback.run();
        }
    }

    public static boolean isReadyToShowInterstitialAd(Context context) {
        long interCap = SharedPrefsHelper.getLongPrefs(context, SharedPrefsHelper.Key.INTERSTITIAL_CAP.name(), 1);
        long interCapCounter = SharedPrefsHelper.getLongPrefs(context, SharedPrefsHelper.Key.INTERSTITIAL_CAP_COUNTER.name(), 1);
        return interCap > 0 && interCapCounter > 0 && interCapCounter % interCap == 0;
    }

    public static void updateCounterForNextInterstitialAd(Context context) {
        long interCapCounter = SharedPrefsHelper.getLongPrefs(context, SharedPrefsHelper.Key.INTERSTITIAL_CAP_COUNTER.name(), 1);
        SharedPrefsHelper.setLongPrefs(context, SharedPrefsHelper.Key.INTERSTITIAL_CAP_COUNTER.name(), interCapCounter + 1);
    }

    public static boolean isReadyToShowInterstitialAdDownload(Context context) {
        long interCap = SharedPrefsHelper.getLongPrefs(context, SharedPrefsHelper.Key.INTERSTITIAL_DOWNLOAD_CAP.name(), 1);
        long interCapCounter = SharedPrefsHelper.getLongPrefs(context, SharedPrefsHelper.Key.INTERSTITIAL_DOWNLOAD_CAP_COUNTER.name(), 1);
        return interCap > 0 && interCapCounter > 0 && interCapCounter % interCap == 0;
    }

    public static void updateCounterForNextInterstitialAdDownload(Context context) {
        long interCapCounter = SharedPrefsHelper.getLongPrefs(context, SharedPrefsHelper.Key.INTERSTITIAL_DOWNLOAD_CAP_COUNTER.name(), 1);
        SharedPrefsHelper.setLongPrefs(context, SharedPrefsHelper.Key.INTERSTITIAL_DOWNLOAD_CAP_COUNTER.name(), interCapCounter + 1);
    }
}
