package io.awesome.gagtube.adsmanager.facebook;

import android.content.Context;
import android.util.Log;

import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;

import io.awesome.gagtube.BuildConfig;

public class AudienceNetworkInitializeHelper implements AudienceNetworkAds.InitListener {

    /**
     * It's recommended to call this method from Application.onCreate().
     * Otherwise you can call it from all Activity.onCreate()
     * methods for Activities that contain ads.
     *
     * @param context Application or Activity.
     */
    public static void initialize(Context context) {
        if (!AudienceNetworkAds.isInitialized(context)) {
            if (BuildConfig.FLAVOR.equals("develop")) {
                AdSettings.turnOnSDKDebugger(context);
            }

            AudienceNetworkAds.buildInitSettings(context)
                    .withInitListener(new AudienceNetworkInitializeHelper())
                    .initialize();
        }
    }

    @Override
    public void onInitialized(AudienceNetworkAds.InitResult initResult) {
        Log.d(AudienceNetworkAds.TAG, initResult.getMessage());
    }
}
