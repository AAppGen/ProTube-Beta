package io.awesome.gagtube.adsmanager.facebook;

import android.app.Activity;
import android.util.Log;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

import io.awesome.gagtube.R;
import io.awesome.gagtube.adsmanager.AdUtils;

public class FbInterstitialAd {

    private final String TAG = FbInterstitialAd.class.getSimpleName();
    private static FbInterstitialAd mInstance;
    private InterstitialAd mInterstitialAd;
    private InterstitialAdListener mInterstitialAdListener;
    private AdClosedListener mAdClosedListener;
    private boolean isReloaded = false;

    public interface AdClosedListener {
        void onAdClosed();
    }

    public static FbInterstitialAd getInstance() {
        if (mInstance == null) {
            mInstance = new FbInterstitialAd();
        }
        return mInstance;
    }

    public void init(Activity activity) {
        mInterstitialAd = new InterstitialAd(activity, activity.getString(R.string.facebook_ad_interstitial));

        // set listeners for the Interstitial Ad
        mInterstitialAdListener = new InterstitialAdListener() {

            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");
                // auto reload new ad when current ad is closed
                if (mAdClosedListener != null) {
                    mAdClosedListener.onAdClosed();
                }
                // load a new interstitial
                loadInterstitialAd();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
                // retry if load failed
                if (!isReloaded) {
                    isReloaded = true;
                    loadInterstitialAd();
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        };

        loadInterstitialAd();
    }

    private void loadInterstitialAd() {
        if (mInterstitialAd != null && !mInterstitialAd.isAdLoaded()) {
            mInterstitialAd.loadAd(mInterstitialAd.buildLoadAdConfig().withAdListener(mInterstitialAdListener).build());
        }
    }

    public void showInterstitialAd(Activity activity, AdClosedListener mAdClosedListener) {
        if (AdUtils.isReadyToShowInterstitialAd(activity) && mInterstitialAd != null && mInterstitialAd.isAdLoaded()) {
            isReloaded = false;
            this.mAdClosedListener = mAdClosedListener;
            // show ads
            mInterstitialAd.show();
        } else {
            // reload a new ad for next time
            loadInterstitialAd();
            // call onAdClosed
            mAdClosedListener.onAdClosed();
        }
        AdUtils.updateCounterForNextInterstitialAd(activity);
    }
}