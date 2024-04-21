package io.awesome.gagtube.adsmanager.applovin;

import android.app.Activity;
import android.os.Handler;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;

import java.util.concurrent.TimeUnit;

import io.awesome.gagtube.R;
import io.awesome.gagtube.adsmanager.AdUtils;

public class ApplovinInterstitialAd implements MaxAdListener {

    private static ApplovinInterstitialAd mInstance;
    private MaxInterstitialAd interstitialAd;
    private AdClosedListener mAdClosedListener;
    private int retryAttempt;

    public interface AdClosedListener {
        void onAdClosed();
    }

    public static ApplovinInterstitialAd getInstance() {
        if (mInstance == null) {
            mInstance = new ApplovinInterstitialAd();
        }
        return mInstance;
    }

    public void init(Activity activity) {
        loadInterstitialAd(activity);
    }

    private void loadInterstitialAd(Activity activity) {
        interstitialAd = new MaxInterstitialAd(activity.getString(R.string.applovin_interstitial_ad), activity);
        interstitialAd.setListener(this);
        // Load the first ad
        interstitialAd.loadAd();
    }

    @Override
    public void onAdLoaded(MaxAd ad) {
        // Interstitial ad is ready to be shown. interstitialAd.isReady() will now return 'true'
        // Reset retry attempt
        retryAttempt = 0;
    }

    @Override
    public void onAdDisplayed(MaxAd ad) {

    }

    @Override
    public void onAdHidden(MaxAd ad) {
        if (mAdClosedListener != null) {
            mAdClosedListener.onAdClosed();
        }
        // Interstitial ad is hidden. Pre-load the next ad
        interstitialAd.loadAd();
    }

    @Override
    public void onAdClicked(MaxAd ad) {

    }

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {
        // Interstitial ad failed to load
        // AppLovin recommends that you retry with exponentially higher delays up to a maximum delay (in this case 64 seconds)
        retryAttempt++;
        long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttempt)));
        new Handler().postDelayed(() -> interstitialAd.loadAd(), delayMillis);
    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
        // Interstitial ad failed to display. AppLovin recommends that you load the next ad.
        interstitialAd.loadAd();
    }

    public void showInterstitialAd(Activity activity, AdClosedListener mAdClosedListener) {
        if (AdUtils.isReadyToShowInterstitialAd(activity) && interstitialAd != null && interstitialAd.isReady()) {
            this.mAdClosedListener = mAdClosedListener;
            interstitialAd.showAd();
        } else {
            loadInterstitialAd(activity);
            mAdClosedListener.onAdClosed();
        }
        AdUtils.updateCounterForNextInterstitialAd(activity);
    }
}