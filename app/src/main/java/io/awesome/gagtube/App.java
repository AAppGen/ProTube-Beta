package io.awesome.gagtube;

import static com.facebook.ads.AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CRASH_DEBUG_MODE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDexApplication;
import androidx.preference.PreferenceManager;

import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinPrivacySettings;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkSettings;
import com.facebook.ads.AdSettings;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.FirebaseApp;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.downloader.Downloader;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

import io.awesome.gagtube.activities.MainActivity;
import io.awesome.gagtube.activities.ReCaptchaActivity;
import io.awesome.gagtube.adsmanager.AdUtils;
import io.awesome.gagtube.adsmanager.facebook.AudienceNetworkInitializeHelper;
import io.awesome.gagtube.download.DownloaderImpl;
import io.awesome.gagtube.notification.NotificationOreo;
import io.awesome.gagtube.settings.GAGTubeSettings;
import io.awesome.gagtube.util.ExtractorHelper;
import io.awesome.gagtube.util.Localization;
import io.awesome.gagtube.util.StateSaver;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.exceptions.OnErrorNotImplementedException;
import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

@SuppressLint("Registered")
public class App extends MultiDexApplication implements LifecycleObserver, Application.ActivityLifecycleCallbacks {

    public static Context applicationContext;

    public static Context getAppContext() {
        return applicationContext;
    }

//    private static final String ONESIGNAL_APP_ID = "9a49cfda-41b5-4911-a23b-f3e506d13e80";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        applicationContext = this;

        // initialize settings first because others inits can use its values
        GAGTubeSettings.initSettings(this);

        // initialize localization
        NewPipe.init(getDownloader(),
                Localization.getPreferredLocalization(this),
                Localization.getPreferredContentCountry(this));
        Localization.init();
        StateSaver.init(this);

        // image loader
        ImageLoader.getInstance().init(getImageLoaderConfigurations());

        // initialize notification channels for android-o
        NotificationOreo.init(this);
        initNotificationChannel();

        // initialize firebase
        FirebaseApp.initializeApp(this);

        configureRxJavaErrorHandler();

        this.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        // AppLovin
        if (AdUtils.ENABLE_APPLOVIN()) {
            AppLovinSdk.getInstance(this).setMediationProvider(AppLovinMediationProvider.MAX);
            AppLovinSdk.initializeSdk(this, configuration -> {
                // AppLovin SDK is initialized, start loading ads
                AppLovinPrivacySettings.setHasUserConsent(false, this);
                AppLovinPrivacySettings.setIsAgeRestrictedUser(false, this);
                AppLovinPrivacySettings.setDoNotSell(false, this);
                AppLovinSdkSettings appLovinSdkSettings = AppLovinSdk.getInstance(this).getSettings();
                appLovinSdkSettings.setLocationCollectionEnabled(true);
                appLovinSdkSettings.setMuted(true);
                if (BuildConfig.FLAVOR.equals("develop")) {
                    appLovinSdkSettings.setTestDeviceAdvertisingIds(List.of("98717d97-7bdf-41e7-84f7-57923b1dcfb0", "a84a0559-0e70-4710-933a-fb0a040e6beb"));
                    appLovinSdkSettings.setVerboseLogging(true);
                    appLovinSdkSettings.setCreativeDebuggerEnabled(true);
                }
            });

            // AdMob bidding
            MobileAds.initialize(this, initializationStatus -> {
            });

            if (BuildConfig.FLAVOR.equals("develop")) {
                // Test device
                RequestConfiguration builder = new RequestConfiguration.Builder()
                        .setTestDeviceIds(Collections.singletonList("92094EE8DBF3A70B378025A71ED90503"))
                        .build();
                MobileAds.setRequestConfiguration(builder);
            }
        }

        // AdMob ads
        if (AdUtils.ENABLE_ADMOB()) {
            // AdMob
            MobileAds.initialize(this, initializationStatus -> {
            });
            MobileAds.setAppMuted(true);
            MobileAds.setAppVolume(0.0f);

            if (BuildConfig.FLAVOR.equals("develop")) {
                // Test device
                RequestConfiguration builder = new RequestConfiguration.Builder()
                        .setTestDeviceIds(Collections.singletonList("AE77C8775F93C1AD766D1A848B83F87A"))
                        .build();
                MobileAds.setRequestConfiguration(builder);
            }
        }

        // Facebook ads
        if (AdUtils.ENABLE_FACEBOOK()) {
            AudienceNetworkInitializeHelper.initialize(this);
            AdSettings.setDataProcessingOptions(new String[]{});
            // Example for setting the SDK to crash when in debug mode
            if (BuildConfig.FLAVOR.equals("develop")) {
                AdSettings.setTestMode(true);
                AdSettings.setIntegrationErrorMode(INTEGRATION_ERROR_CRASH_DEBUG_MODE);
                AdSettings.addTestDevice("cd309e1f-1751-4c64-98e9-203c471ace9d");
            }
        }

        //initOneSignal();
    }

//    private void initOneSignal() {
//        // Enable verbose OneSignal logging to debug issues if needed.
//        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
//
//        // OneSignal Initialization
//        OneSignal.initWithContext(this);
//        OneSignal.setAppId(ONESIGNAL_APP_ID);
//
//        if (BuildConfig.FLAVOR.equals("develop")) {
//            // promptForPushNotifications will show the native Android notification permission prompt.
//            // We recommend removing the following code and instead using an In-App Message to prompt for notification permission (See step 7)
//            OneSignal.promptForPushNotifications();
//        }
//    }

    protected Downloader getDownloader() {
        DownloaderImpl downloader = DownloaderImpl.init(null);
        setCookiesToDownloader(downloader);
        return downloader;
    }

    protected void setCookiesToDownloader(final DownloaderImpl downloader) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String key = getApplicationContext().getString(R.string.recaptcha_cookies_key);
        downloader.setCookie(ReCaptchaActivity.RECAPTCHA_COOKIES_KEY, prefs.getString(key, ""));
        downloader.updateYoutubeRestrictedModeCookies(getApplicationContext());
    }

    private void configureRxJavaErrorHandler() {

        // https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0#error-handling
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {

            @Override
            public void accept(@NonNull Throwable throwable) {

                if (throwable instanceof UndeliverableException) {
                    // As UndeliverableException is a wrapper, get the cause of it to get the "real" exception
                    throwable = throwable.getCause();
                }

                final List<Throwable> errors;
                if (throwable instanceof CompositeException) {
                    errors = ((CompositeException) throwable).getExceptions();
                } else {
                    errors = Collections.singletonList(throwable);
                }

                for (final Throwable error : errors) {
                    if (isThrowableIgnored(error))
                        return;
                    if (isThrowableCritical(error)) {
                        reportException(error);
                        return;
                    }
                }
            }

            private boolean isThrowableIgnored(@NonNull final Throwable throwable) {

                // Don't crash the application over a simple network problem
                return ExtractorHelper.hasAssignableCauseThrowable(throwable, IOException.class, SocketException.class, // network api cancellation
                        InterruptedException.class, InterruptedIOException.class); // blocking code disposed
            }

            private boolean isThrowableCritical(@NonNull final Throwable throwable) {

                // Though these exceptions cannot be ignored
                return ExtractorHelper.hasAssignableCauseThrowable(throwable,
                        NullPointerException.class, IllegalArgumentException.class, // bug in app
                        OnErrorNotImplementedException.class, MissingBackpressureException.class,
                        IllegalStateException.class); // bug in operator
            }

            private void reportException(@NonNull final Throwable throwable) {

                // Throw uncaught exception that will trigger the report system
                Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), throwable);
            }
        });
    }

    private ImageLoaderConfiguration getImageLoaderConfigurations() {

        return new ImageLoaderConfiguration.Builder(this)
                .memoryCache(new LRULimitedMemoryCache(100 * 1024 * 1024))
                .diskCacheSize(500 * 1024 * 1024)
                .build();
    }

    public void initNotificationChannel() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        final String id = getString(R.string.notification_channel_id);
        final CharSequence name = getString(R.string.notification_channel_name);
        final String description = getString(R.string.notification_channel_description);

        // Keep this below DEFAULT to avoid making noise on every notification update
        final int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
        mChannel.setDescription(description);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        if (activity instanceof MainActivity && BuildConfig.FLAVOR.equals("production")) {
            // In-app updates
            AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);
            // Returns an intent object that you use to check for an update.
            Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
            // Checks that the platform will allow the specified type of update.
            appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        // This example applies an immediate update. To apply a flexible update
                        // instead, pass in AppUpdateType.FLEXIBLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    // Request the update.
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                appUpdateInfo,
                                // The current activity making the update request.
                                activity,
                                // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                                AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(),
                                // Include a request code to later monitor this update request.
                                9999);
                    } catch (IntentSender.SendIntentException ignored) {
                    }
                }
            });
        }
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
