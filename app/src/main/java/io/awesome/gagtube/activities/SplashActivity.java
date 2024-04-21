package io.awesome.gagtube.activities;

import static org.schabi.newpipe.extractor.NewPipe.getDownloader;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.annimon.stream.Stream;

import org.schabi.newpipe.extractor.NewPipe;

import java.util.Locale;

import io.awesome.gagtube.BuildConfig;
import io.awesome.gagtube.R;
import io.awesome.gagtube.base.BaseActivity;
import io.awesome.gagtube.databinding.SplashActivityBinding;
import io.awesome.gagtube.util.AppUtils;
import io.awesome.gagtube.util.Constants;
import io.awesome.gagtube.util.Localization;
import io.awesome.gagtube.util.ThemeHelper;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {

    /**
     * Number of seconds to count down before showing the app open ad. This simulates the time needed
     * to load the app.
     */
    private static final long COUNTER_TIME = 2;
    private SplashActivityBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        setTheme(ThemeHelper.getSettingsThemeStyle(this));
        super.onCreate(savedInstanceState);
        binding = SplashActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // show version app
        binding.version.setText(String.format(getString(R.string.version), BuildConfig.VERSION_NAME));

        // Create a timer so the SplashActivity will be displayed for a fixed amount of time.
        createTimer(COUNTER_TIME);

        SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = defaultPreferences.edit();

        String countryCode = AppUtils.getDeviceCountryIso(this);
        String languageCode = Stream.of(Locale.getAvailableLocales()).filter(locale -> locale.getCountry().equals(AppUtils.getDeviceCountryIso(this))).map(Locale::getLanguage).findFirst().get();
        // save COUNTRY_CODE, LANGUAGE_CODE to preferences
        editor.putString(Constants.COUNTRY_CODE, !TextUtils.isEmpty(countryCode) ? countryCode : "GB");
        editor.putString(Constants.LANGUAGE_CODE, !TextUtils.isEmpty(languageCode) ? languageCode : "en");
        editor.apply();

        // init localization
        NewPipe.init(getDownloader(), Localization.getPreferredLocalization(this), Localization.getPreferredContentCountry(this));
    }

    /**
     * Create the countdown timer, which counts down to zero and show the app open ad.
     *
     * @param seconds the number of seconds that the timer counts down from
     */
    private void createTimer(long seconds) {
        CountDownTimer countDownTimer = new CountDownTimer(seconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                startMainActivity();
            }
        };
        countDownTimer.start();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
