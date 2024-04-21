package io.awesome.gagtube.adsmanager.admob;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.NativeAdOptions;

import io.awesome.gagtube.R;
import io.awesome.gagtube.adsmanager.nativead.AppNativeAdView;
import io.awesome.gagtube.adsmanager.nativead.NativeAdStyle;
import io.awesome.gagtube.fragments.discover.adapter.VideoListAdapter;
import io.awesome.gagtube.info_list.InfoListAdapter;

public class AdMobConfig {

    public static void showAdMobBannerAd(AdView adView) {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                adView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Code to be executed when an ad request fails.
                adView.setVisibility(View.GONE);
            }
        });
        adView.loadAd(adRequest);
    }

    public static void onPause(AdView adView) {
        if (adView != null) {
            adView.pause();
        }
    }

    public static void onResume(AdView adView) {
        if (adView != null) {
            adView.resume();
        }
    }

    public static void destroyBannerAd(AdView adView) {
        if (adView != null) {
            adView.destroy();
        }
    }

    public static void showNativeAd(final Context context, AppNativeAdView nativeAdView) {
        VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

        AdLoader adLoader = new AdLoader.Builder(context, context.getString(R.string.admob_native_ad))
                .forNativeAd(nativeAd -> {
                    NativeAdStyle styles = new NativeAdStyle.Builder().build();
                    nativeAdView.setStyles(styles);
                    nativeAdView.setNativeAd(nativeAd);
                }).withAdListener(new AdListener() {

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        nativeAdView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        nativeAdView.setVisibility(View.VISIBLE);
                    }
                }).withNativeAdOptions(adOptions).build();

        AdRequest.Builder builder = new AdRequest.Builder();
        adLoader.loadAd(builder.build());
    }

    public static void showNativeAd(final Context context, AppNativeAdView nativeAdView, InfoListAdapter adapter, View headerView) {
        VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

        AdLoader adLoader = new AdLoader.Builder(context, context.getString(R.string.admob_native_ad))
                .forNativeAd(nativeAd -> {
                    NativeAdStyle styles = new NativeAdStyle.Builder().build();
                    nativeAdView.setStyles(styles);
                    nativeAdView.setNativeAd(nativeAd);
                }).withAdListener(new AdListener() {

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        nativeAdView.setVisibility(View.GONE);
                        adapter.setHeader(null);
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        nativeAdView.setVisibility(View.VISIBLE);
                        adapter.setHeader(headerView);
                    }
                }).withNativeAdOptions(adOptions).build();

        AdRequest.Builder builder = new AdRequest.Builder();
        adLoader.loadAd(builder.build());
    }

    public static void showNativeAd(final Context context, AppNativeAdView nativeAdView, VideoListAdapter adapter, View headerView) {
        VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

        AdLoader adLoader = new AdLoader.Builder(context, context.getString(R.string.admob_native_ad))
                .forNativeAd(nativeAd -> {
                    NativeAdStyle styles = new NativeAdStyle.Builder().build();
                    nativeAdView.setStyles(styles);
                    nativeAdView.setNativeAd(nativeAd);
                }).withAdListener(new AdListener() {

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        nativeAdView.setVisibility(View.GONE);
                        adapter.setHeader(null);
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        nativeAdView.setVisibility(View.VISIBLE);
                        adapter.setHeader(headerView);
                    }
                }).withNativeAdOptions(adOptions).build();

        AdRequest.Builder builder = new AdRequest.Builder();
        adLoader.loadAd(builder.build());
    }

    public static void destroyNativeAd(AppNativeAdView nativeAdView) {
        if (nativeAdView != null) {
            nativeAdView.destroyNativeAd();
        }
    }
}
