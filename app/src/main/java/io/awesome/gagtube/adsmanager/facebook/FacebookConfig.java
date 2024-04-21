package io.awesome.gagtube.adsmanager.facebook;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AdView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;

import java.util.ArrayList;
import java.util.List;

import io.awesome.gagtube.R;
import io.awesome.gagtube.fragments.discover.adapter.VideoListAdapter;
import io.awesome.gagtube.info_list.InfoListAdapter;

public class FacebookConfig {

    public enum NativeAdType {
        SMALL, MEDIUM, BIG
    }

    public static void showFBBannerAd(LinearLayout adContainer, AdView adView) {
        if (adContainer.getChildCount() > 0) {
            adContainer.removeAllViews();
        }
        // Add the ad view to adContainer
        adContainer.addView(adView);

        AdListener adListener = new AdListener() {

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                adContainer.setVisibility(View.GONE);
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback
                adContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        };
        // Request an ad
        adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());
    }

    public static void onDestroyView(LinearLayout bannerAdContainer) {
        if (bannerAdContainer != null) {
            bannerAdContainer.removeAllViews();
        }
    }

    public static void destroyBannerAd(AdView bannerAdView) {
        if (bannerAdView != null) {
            bannerAdView.destroy();
        }
    }

    public static void showNativeAd(Activity activity, View view, NativeAd nativeAd, NativeAdType nativeAdType) {
        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Race condition, load() called again before last ad was displayed
                if (nativeAd == null || nativeAd != ad) {
                    return;
                }
                // Inflate Native Ad into Container
                inflateAd(activity, view, nativeAd, nativeAdType);
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };
        // Request an ad
        nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
    }

    public static void showNativeAd(Activity activity, NativeAd nativeAd, NativeAdType nativeAdType, InfoListAdapter adapter, View headerView) {
        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {
                adapter.setHeader(null);
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Race condition, load() called again before last ad was displayed
                if (nativeAd == null || nativeAd != ad) {
                    return;
                }
                // Inflate Native Ad into Container
                inflateAd(activity, headerView, nativeAd, nativeAdType);
                adapter.setHeader(headerView);
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };
        // Request an ad
        nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
    }

    public static void showNativeAd(Activity activity, NativeAd nativeAd, NativeAdType nativeAdType, VideoListAdapter adapter, View headerView) {
        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {
                adapter.setHeader(null);
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Race condition, load() called again before last ad was displayed
                if (nativeAd == null || nativeAd != ad) {
                    return;
                }
                // Inflate Native Ad into Container
                inflateAd(activity, headerView, nativeAd, nativeAdType);
                adapter.setHeader(headerView);
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };
        // Request an ad
        nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
    }

    private static void inflateAd(Activity activity, View view, NativeAd nativeAd, NativeAdType nativeAdType) {
        nativeAd.unregisterView();

        // Add the Ad view into the ad container.
        NativeAdLayout nativeAdLayout = view.findViewById(R.id.native_ad_container);
        LayoutInflater inflater = LayoutInflater.from(activity);
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        LinearLayout nativeAdView;
        if (nativeAdType == NativeAdType.SMALL) {
            nativeAdView = (LinearLayout) inflater.inflate(R.layout.fb_native_ad_small, nativeAdLayout, false);
        } else if (nativeAdType == NativeAdType.MEDIUM) {
            nativeAdView = (LinearLayout) inflater.inflate(R.layout.fb_native_ad_medium, nativeAdLayout, false);
        } else {
            nativeAdView = (LinearLayout) inflater.inflate(R.layout.fb_native_ad_big, nativeAdLayout, false);
        }
        nativeAdLayout.removeAllViews();
        nativeAdLayout.addView(nativeAdView);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = nativeAdView.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(activity, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        TextView nativeAdTitle = nativeAdView.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = nativeAdView.findViewById(R.id.native_ad_media);
        TextView nativeAdBody = nativeAdView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = nativeAdView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = nativeAdView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.GONE);
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(nativeAdView, nativeAdMedia, clickableViews);
    }

    public static void destroyNativeAd(NativeAd nativeAd) {
        if (nativeAd != null) {
            nativeAd.unregisterView();
            nativeAd.destroy();
        }
    }
}
