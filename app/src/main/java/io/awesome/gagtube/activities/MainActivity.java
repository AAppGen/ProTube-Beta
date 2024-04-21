package io.awesome.gagtube.activities;

import static io.awesome.gagtube.fragments.detail.VideoDetailFragment.MAX_OVERLAY_ALPHA;
import static io.awesome.gagtube.util.NavigationHelper.AUTO_PLAY;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.schabi.newpipe.extractor.StreamingService;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.awesome.gagtube.R;
import io.awesome.gagtube.adsmanager.AdUtils;
import io.awesome.gagtube.base.BaseActivity;
import io.awesome.gagtube.fragments.BackPressable;
import io.awesome.gagtube.fragments.detail.VideoDetailFragment;
import io.awesome.gagtube.fragments.discover.DiscoverFragment;
import io.awesome.gagtube.fragments.list.main.TrendingFragment;
import io.awesome.gagtube.library.LibraryFragment;
import io.awesome.gagtube.player.VideoPlayer;
import io.awesome.gagtube.player.event.OnKeyDownListener;
import io.awesome.gagtube.player.playqueue.PlayQueue;
import io.awesome.gagtube.util.Constants;
import io.awesome.gagtube.util.Localization;
import io.awesome.gagtube.util.NavigationHelper;
import io.awesome.gagtube.util.PermissionHelper;
import io.awesome.gagtube.util.SerializedCache;
import io.awesome.gagtube.util.StateSaver;
import io.awesome.gagtube.util.ThemeHelper;

public class MainActivity extends BaseActivity {
	
	@BindView(R.id.coordinator)
	CoordinatorLayout coordinatorLayout;
	@BindView(R.id.bottom_navigation)
	AHBottomNavigation mBottomNavigation;
	
	private BroadcastReceiver broadcastReceiver;
	
	// Activity's LifeCycle
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(ThemeHelper.getSettingsThemeStyle(this));
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
			initFragments();
		}
		setSupportActionBar(findViewById(R.id.toolbar));
		// setup bottom navigation
		setUpBottomNavigation();
		
		// init InterstitialAd
		AdUtils.initInterstitialAd(this);

		setupBroadcastReceiver();

		PermissionHelper.checkPostNotificationsPermission(this, PermissionHelper.POST_NOTIFICATIONS_REQUEST_CODE);
	}
	
	private void setUpBottomNavigation() {
		mBottomNavigation.setBehaviorTranslationEnabled(false);
		mBottomNavigation.setTranslucentNavigationEnabled(false);
		
		// Force to tint the drawable (useful for font with icon for example)
		mBottomNavigation.setForceTint(true);
		// always show title and icon
		mBottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
		
		// Change colors
		mBottomNavigation.setAccentColor(ThemeHelper.isLightThemeSelected(this) ? ContextCompat.getColor(this, R.color.light_bottom_navigation_accent_color) : ContextCompat.getColor(this, R.color.white));
		mBottomNavigation.setDefaultBackgroundColor(ThemeHelper.isLightThemeSelected(this) ? ContextCompat.getColor(this, R.color.light_bottom_navigation_background_color) : ContextCompat.getColor(this, R.color.dark_bottom_navigation_background_color));
		
		AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.menu_navigation);
		navigationAdapter.setupWithBottomNavigation(mBottomNavigation);
		
		// onTabSelected listener
		mBottomNavigation.setOnTabSelectedListener((position, wasSelected) -> {
			final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_holder);
			switch (position) {
				case 0:
					if (!(fragment instanceof TrendingFragment)) {
						NavigationHelper.gotoMainFragment(getSupportFragmentManager());
					}
					return true;
				
				case 1:
					if (!(fragment instanceof DiscoverFragment)) {
						NavigationHelper.openDiscoverFragment(getSupportFragmentManager());
					}
					return true;
				
				case 2:
					if (!(fragment instanceof LibraryFragment)) {
						NavigationHelper.openLibraryFragment(getSupportFragmentManager());
					}
					return true;
			}
			return false;
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (!isChangingConfigurations()) {
			StateSaver.clearStateFiles();
		}
		if (broadcastReceiver != null) {
			unregisterReceiver(broadcastReceiver);
		}
	}
	
	@Override
	protected void onResume() {
		Localization.init();
		super.onResume();
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		if (sharedPreferences.getBoolean(Constants.KEY_THEME_CHANGE, false)) {
			sharedPreferences.edit().putBoolean(Constants.KEY_THEME_CHANGE, false).apply();
			NavigationHelper.recreateActivity(this);
		}
		
		if (sharedPreferences.getBoolean(Constants.KEY_CONTENT_CHANGE, false)) {
			sharedPreferences.edit().putBoolean(Constants.KEY_CONTENT_CHANGE, false).apply();
			NavigationHelper.recreateActivity(this);
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		if (intent != null) {
			// Return if launched from a launcher (e.g. Nova Launcher, Pixel Launcher ...)
			// to not destroy the already created backstack
			final String action = intent.getAction();
			if ((action != null && action.equals(Intent.ACTION_MAIN)) && intent.hasCategory(Intent.CATEGORY_LAUNCHER)) {
				return;
			}
		}
		super.onNewIntent(intent);
		setIntent(intent);
		handleIntent(intent);
	}
	
	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_player_holder);
		if (fragment instanceof OnKeyDownListener && !bottomSheetHiddenOrCollapsed()) {
			// Provide keyDown event to fragment which then sends this event
			// to the main player service
			return ((OnKeyDownListener) fragment).onKeyDown(keyCode) || super.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onBackPressed() {
		// In case bottomSheet is not visible on the screen or collapsed we can assume that the user
		// interacts with a fragment inside fragment_holder so all back presses should be handled by it
		if (bottomSheetHiddenOrCollapsed()) {
			final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_holder);
			// If current fragment implements BackPressable (i.e. can/wanna handle back press)
			// delegate the back press to it
			if (fragment instanceof BackPressable) {
				if (((BackPressable) fragment).onBackPressed()) {
					return;
				}
			}
		} else {
			final Fragment fragmentPlayer = getSupportFragmentManager().findFragmentById(R.id.fragment_player_holder);
			// If current fragment implements BackPressable (i.e. can/wanna handle back press)
			// delegate the back press to it
			if (fragmentPlayer instanceof BackPressable) {
				if (!((BackPressable) fragmentPlayer).onBackPressed()) {
					final FrameLayout bottomSheetLayout = findViewById(R.id.fragment_player_holder);
					BottomSheetBehavior.from(bottomSheetLayout).setState(BottomSheetBehavior.STATE_COLLAPSED);
					setBottomNavigationVisibility(View.VISIBLE);
				}
				return;
			}
		}
		
		// if has only fragment in activity
		if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
			finish();
		} else {
			super.onBackPressed();
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		for (int i : grantResults) {
			if (i == PackageManager.PERMISSION_DENIED) {
				return;
			}
		}

		switch (requestCode) {
			case PermissionHelper.DOWNLOADS_REQUEST_CODE:
				NavigationHelper.openDownloads(this);
				break;

			case PermissionHelper.DOWNLOAD_DIALOG_REQUEST_CODE:
				final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_player_holder);
				if (fragment instanceof VideoDetailFragment) {
					((VideoDetailFragment) fragment).openDownloadDialog();
				}
				break;
		}
	}
	
	private void onHomeButtonPressed() {
		// If search fragment wasn't found in the backstack...
		if (!NavigationHelper.hasSearchFragmentInBackstack(getSupportFragmentManager())) {
			// go to the main fragment
			NavigationHelper.gotoMainFragment(getSupportFragmentManager());
			mBottomNavigation.setCurrentItem(0);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onHomeButtonPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	// Init fragments
	private void initFragments() {
		StateSaver.clearStateFiles();
		if (getIntent() != null && getIntent().hasExtra(Constants.KEY_LINK_TYPE)) {
			// When user watch a video inside popup and then tries to open the video in main player
			// while the app is closed he will see a blank fragment on place of kiosk.
			// Let's open it first
			if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
				NavigationHelper.openMainFragment(getSupportFragmentManager());
				mBottomNavigation.setCurrentItem(0);
			}
			handleIntent(getIntent());
		} else {
			NavigationHelper.gotoMainFragment(getSupportFragmentManager());
			mBottomNavigation.setCurrentItem(0);
		}
	}
	
	private void handleIntent(Intent intent) {
		try {
			if (intent.hasExtra(Constants.KEY_LINK_TYPE)) {
				final String url = intent.getStringExtra(Constants.KEY_URL);
				final int serviceId = intent.getIntExtra(Constants.KEY_SERVICE_ID, 0);
				final String title = intent.getStringExtra(Constants.KEY_TITLE);
				StreamingService.LinkType linkType = ((StreamingService.LinkType) intent.getSerializableExtra(Constants.KEY_LINK_TYPE));
				if (linkType != null) {
					switch (linkType) {
						case STREAM:
							final boolean autoPlay = intent.getBooleanExtra(AUTO_PLAY, false);
							final String intentCacheKey = intent.getStringExtra(VideoPlayer.PLAY_QUEUE_KEY);
							final PlayQueue playQueue = intentCacheKey != null ? SerializedCache.getInstance().take(intentCacheKey, PlayQueue.class) : null;
							NavigationHelper.openVideoDetailFragment(getSupportFragmentManager(), serviceId, url, title, autoPlay, playQueue);
							break;
						
						case CHANNEL:
							NavigationHelper.openChannelFragment(getSupportFragmentManager(), serviceId, url, title);
							break;
						
						case PLAYLIST:
							NavigationHelper.openPlaylistFragment(getSupportFragmentManager(), serviceId, url, title);
							break;
					}
				}
			} else if (intent.hasExtra(Constants.KEY_OPEN_SEARCH)) {
				String searchString = intent.getStringExtra(Constants.KEY_SEARCH_STRING);
				if (searchString == null) {
					searchString = "";
				}
				final int serviceId = intent.getIntExtra(Constants.KEY_SERVICE_ID, 0);
				NavigationHelper.openSearchFragment(getSupportFragmentManager(), serviceId, searchString);
			} else {
				NavigationHelper.gotoMainFragment(getSupportFragmentManager());
				mBottomNavigation.setCurrentItem(0);
			}
		} catch (final Exception ignored) {
		}
	}
	
	private void setupBroadcastReceiver() {
		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(final Context context, final Intent intent) {
				if (VideoDetailFragment.ACTION_PLAYER_STARTED.equals(intent.getAction())) {
					final Fragment fragmentPlayer = getSupportFragmentManager().findFragmentById(R.id.fragment_player_holder);
					if (fragmentPlayer == null) {
						/*
						 * We still don't have a fragment attached to the activity.
						 * It can happen when a user started popup or background players
						 * without opening a stream inside the fragment.
						 * Adding it in a collapsed state (only mini player will be visible)
						 * */
						NavigationHelper.showMiniPlayer(getSupportFragmentManager());
					}
					/*
					 * At this point the player is added 100%, we can unregister.
					 * Other actions are useless since the fragment will not be removed after that
					 * */
					unregisterReceiver(broadcastReceiver);
					broadcastReceiver = null;
				}
			}
		};
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(VideoDetailFragment.ACTION_PLAYER_STARTED);
		registerReceiver(broadcastReceiver, intentFilter);
	}
	
	private boolean bottomSheetHiddenOrCollapsed() {
		final FrameLayout bottomSheetLayout = findViewById(R.id.fragment_player_holder);
		final BottomSheetBehavior<FrameLayout> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
		
		final int sheetState = bottomSheetBehavior.getState();
		return sheetState == BottomSheetBehavior.STATE_HIDDEN || sheetState == BottomSheetBehavior.STATE_COLLAPSED;
	}
	
	public void setBottomNavigationVisibility(int visibility) {
		Transition transition = new Slide(Gravity.BOTTOM);
		transition.addTarget(R.id.bottom_navigation);
		
		TransitionManager.beginDelayedTransition(coordinatorLayout, transition);
		mBottomNavigation.setVisibility(visibility);
	}
	
	public void setBottomNavigationAlpha(final float slideOffset) {
		mBottomNavigation.setAlpha(Math.min(MAX_OVERLAY_ALPHA, slideOffset));
	}
}