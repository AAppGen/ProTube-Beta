package io.awesome.gagtube.adsmanager.applovin;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder;
import com.applovin.sdk.AppLovinSdkUtils;

import io.awesome.gagtube.R;
import io.awesome.gagtube.fragments.discover.adapter.VideoListAdapter;
import io.awesome.gagtube.info_list.InfoListAdapter;

public class AppLovinConfig {

    public static void showApplovinNativeAd(Activity activity, View rootView, MaxNativeAdLoader nativeAdLoader, boolean useBigStyle) {
        FrameLayout nativeAdContainerView = rootView.findViewById(R.id.native_ad_layout);
        if (nativeAdContainerView != null && nativeAdLoader != null) {
            nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                @Override
                public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
                    // Add ad view to view.
                    nativeAdContainerView.removeAllViews();
                    nativeAdContainerView.addView(nativeAdView);
                }

                @Override
                public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
                    // We recommend retrying with exponentially higher delays up to a maximum delay
                    nativeAdContainerView.removeAllViews();
                }

                @Override
                public void onNativeAdClicked(final MaxAd ad) {
                    // Optional click callback
                }
            });

            MaxNativeAdViewBinder binder = new MaxNativeAdViewBinder.Builder(useBigStyle ? R.layout.applovin_native_ad_manual : R.layout.applovin_native_ad_manual_small)
                    .setTitleTextViewId(R.id.title_text_view)
                    .setBodyTextViewId(R.id.body_text_view)
                    .setStarRatingContentViewGroupId(R.id.star_rating_view)
                    .setAdvertiserTextViewId(R.id.advertiser_textView)
                    .setIconImageViewId(R.id.icon_image_view)
                    .setMediaContentViewGroupId(R.id.media_view_container)
                    .setOptionsContentViewGroupId(R.id.ad_options_view)
                    .setCallToActionButtonId(R.id.cta_button)
                    .build();

            nativeAdLoader.loadAd(new MaxNativeAdView(binder, activity));
        }
    }

    public static void showApplovinNativeAd(Activity activity, View headerView, VideoListAdapter adapter, MaxNativeAdLoader nativeAdLoader, boolean useBigStyle) {
        FrameLayout nativeAdContainerView = headerView.findViewById(R.id.native_ad_layout);
        if (nativeAdContainerView != null && nativeAdLoader != null) {
            nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                @Override
                public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
                    // Add ad view to view.
                    nativeAdContainerView.removeAllViews();
                    nativeAdContainerView.addView(nativeAdView);
                    adapter.setHeader(headerView);
                }

                @Override
                public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
                    // We recommend retrying with exponentially higher delays up to a maximum delay
                    nativeAdContainerView.removeAllViews();
                    adapter.setHeader(null);
                }

                @Override
                public void onNativeAdClicked(final MaxAd ad) {
                    // Optional click callback
                }
            });

            MaxNativeAdViewBinder binder = new MaxNativeAdViewBinder.Builder(useBigStyle ? R.layout.applovin_native_ad_manual : R.layout.applovin_native_ad_manual_small)
                    .setTitleTextViewId(R.id.title_text_view)
                    .setBodyTextViewId(R.id.body_text_view)
                    .setStarRatingContentViewGroupId(R.id.star_rating_view)
                    .setAdvertiserTextViewId(R.id.advertiser_textView)
                    .setIconImageViewId(R.id.icon_image_view)
                    .setMediaContentViewGroupId(R.id.media_view_container)
                    .setOptionsContentViewGroupId(R.id.ad_options_view)
                    .setCallToActionButtonId(R.id.cta_button)
                    .build();

            nativeAdLoader.loadAd(new MaxNativeAdView(binder, activity));
        }
    }

    public static void showApplovinNativeAd(Activity activity, View headerView, InfoListAdapter adapter, MaxNativeAdLoader nativeAdLoader, boolean useBigStyle) {
        FrameLayout nativeAdContainerView = headerView.findViewById(R.id.native_ad_layout);
        if (nativeAdContainerView != null && nativeAdLoader != null) {
            nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                @Override
                public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
                    // Add ad view to view.
                    nativeAdContainerView.removeAllViews();
                    nativeAdContainerView.addView(nativeAdView);
                    adapter.setHeader(headerView);
                }

                @Override
                public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
                    // We recommend retrying with exponentially higher delays up to a maximum delay
                    nativeAdContainerView.removeAllViews();
                    adapter.setHeader(null);
                }

                @Override
                public void onNativeAdClicked(final MaxAd ad) {
                    // Optional click callback
                }
            });

            MaxNativeAdViewBinder binder = new MaxNativeAdViewBinder.Builder(useBigStyle ? R.layout.applovin_native_ad_manual : R.layout.applovin_native_ad_manual_small)
                    .setTitleTextViewId(R.id.title_text_view)
                    .setBodyTextViewId(R.id.body_text_view)
                    .setStarRatingContentViewGroupId(R.id.star_rating_view)
                    .setAdvertiserTextViewId(R.id.advertiser_textView)
                    .setIconImageViewId(R.id.icon_image_view)
                    .setMediaContentViewGroupId(R.id.media_view_container)
                    .setOptionsContentViewGroupId(R.id.ad_options_view)
                    .setCallToActionButtonId(R.id.cta_button)
                    .build();

            nativeAdLoader.loadAd(new MaxNativeAdView(binder, activity));
        }
    }

    public static void destroyApplovinNativeAd(MaxNativeAdLoader nativeAdLoader) {
        if (nativeAdLoader != null) {
            nativeAdLoader.destroy();
        }
    }

    public static void showBannerAd(Activity activity, LinearLayout adContainer, MaxAdView adView) {
        adContainer.removeAllViews();
        adView.setListener(new MaxAdViewAdListener() {
            @Override
            public void onAdExpanded(MaxAd ad) {

            }

            @Override
            public void onAdCollapsed(MaxAd ad) {

            }

            @Override
            public void onAdLoaded(MaxAd ad) {
                adContainer.removeAllViews();
                adContainer.addView(adView);
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {

            }

            @Override
            public void onAdHidden(MaxAd ad) {

            }

            @Override
            public void onAdClicked(MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                adContainer.removeAllViews();
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {

            }
        });
        // Stretch to the width of the screen for banners to be fully functional
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        // Get the adaptive banner height.
        int heightDp = MaxAdFormat.BANNER.getAdaptiveSize(activity).getHeight();
        int heightPx = AppLovinSdkUtils.dpToPx(activity, heightDp);
        adView.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
        adView.setExtraParameter("adaptive_banner", "true");
        adView.startAutoRefresh();
        adView.setGravity(Gravity.CENTER);
        // Load the ad
        adView.loadAd();
    }

    public static void destroyBannerAd(LinearLayout adContainer, MaxAdView adView) {
        if (adView != null) {
            adView.destroy();
        }
        if (adContainer != null) {
            adContainer.removeAllViews();
        }
    }
}
