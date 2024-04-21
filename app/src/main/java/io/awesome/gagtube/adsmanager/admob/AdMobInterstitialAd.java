package io.awesome.gagtube.adsmanager.admob;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import io.awesome.gagtube.R;
import io.awesome.gagtube.adsmanager.AdUtils;

public class AdMobInterstitialAd {

    private static AdMobInterstitialAd mInstance;
    private InterstitialAd mInterstitialAd;
    private AdClosedListener mAdClosedListener;
    private boolean isReloaded = false;

    public interface AdClosedListener {
        void onAdClosed();
    }

    public static AdMobInterstitialAd getInstance() {
        if (mInstance == null) {
            mInstance = new AdMobInterstitialAd();
        }
        return mInstance;
    }

    public void init(Activity activity) {
        // load interstitial ads
        loadInterstitialAd(activity);
    }

    private void loadInterstitialAd(Activity activity) {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(activity, activity.getString(R.string.admob_interstitial_ad), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until an ad is loaded.
                mInterstitialAd = interstitialAd;
                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        // Make sure to set your reference to null so you don't show it a second time.
                        if (mAdClosedListener != null) {
                            mAdClosedListener.onAdClosed();
                        }
                        // load a new interstitial
                        loadInterstitialAd(activity);
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        // Called when fullscreen content failed to show.
                        // Make sure to set your reference to null so you don't show it a second time.
                        if (!isReloaded) {
                            isReloaded = true;
                            loadInterstitialAd(activity);
                        }
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                mInterstitialAd = null;
            }
        });
    }

    public void showInterstitialAd(Activity activity, AdClosedListener mAdClosedListener) {
        if (AdUtils.isReadyToShowInterstitialAd(activity) && mInterstitialAd != null) {
            isReloaded = false;
            this.mAdClosedListener = mAdClosedListener;
            // show ads
            mInterstitialAd.show(activity);
        } else {
            // reload a new ad for next time
            loadInterstitialAd(activity);
            // call onAdClosed
            mAdClosedListener.onAdClosed();
        }
        AdUtils.updateCounterForNextInterstitialAd(activity);
    }
}