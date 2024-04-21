package io.awesome.gagtube.fragments.detail;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static org.schabi.newpipe.extractor.stream.StreamExtractor.NO_AGE_LIMIT;
import static io.awesome.gagtube.ktx.ViewUtils.animate;
import static io.awesome.gagtube.player.helper.PlayerHelper.globalScreenOrientationLocked;
import static io.awesome.gagtube.util.AnimationUtils.animateView;
import static io.awesome.gagtube.util.NavigationHelper.AUTO_PLAY;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.ContentObserver;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import com.applovin.mediation.ads.MaxAdView;
import com.facebook.ads.AdSize;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.gms.ads.AdView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.jakewharton.rxbinding2.view.RxView;

import org.jetbrains.annotations.NotNull;
import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.ServiceList;
import org.schabi.newpipe.extractor.channel.ChannelInfo;
import org.schabi.newpipe.extractor.comments.CommentsInfoItem;
import org.schabi.newpipe.extractor.exceptions.ContentNotAvailableException;
import org.schabi.newpipe.extractor.playlist.PlaylistInfoItem;
import org.schabi.newpipe.extractor.stream.Description;
import org.schabi.newpipe.extractor.stream.StreamInfo;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.stream.StreamType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.State;
import io.awesome.gagtube.App;
import io.awesome.gagtube.R;
import io.awesome.gagtube.activities.ReCaptchaActivity;
import io.awesome.gagtube.adsmanager.AdUtils;
import io.awesome.gagtube.adsmanager.admob.AdMobConfig;
import io.awesome.gagtube.adsmanager.applovin.AppLovinConfig;
import io.awesome.gagtube.adsmanager.facebook.FacebookConfig;
import io.awesome.gagtube.database.subscription.SubscriptionEntity;
import io.awesome.gagtube.databinding.FragmentVideoDetailBinding;
import io.awesome.gagtube.download.ui.DownloadDialog;
import io.awesome.gagtube.fragments.BackPressable;
import io.awesome.gagtube.fragments.BaseStateFragment;
import io.awesome.gagtube.fragments.comments.CommentsFragment;
import io.awesome.gagtube.info_list.InfoItemBuilder;
import io.awesome.gagtube.ktx.ViewUtils;
import io.awesome.gagtube.local.dialog.PlaylistAppendDialog;
import io.awesome.gagtube.local.subscription.SubscriptionService;
import io.awesome.gagtube.player.MainPlayer;
import io.awesome.gagtube.player.VideoPlayerImpl;
import io.awesome.gagtube.player.event.OnKeyDownListener;
import io.awesome.gagtube.player.event.PlayerServiceExtendedEventListener;
import io.awesome.gagtube.player.helper.PlayerHolder;
import io.awesome.gagtube.player.playqueue.PlayQueue;
import io.awesome.gagtube.player.playqueue.PlayQueueItem;
import io.awesome.gagtube.player.playqueue.SinglePlayQueue;
import io.awesome.gagtube.util.AnimationUtils;
import io.awesome.gagtube.util.AppUtils;
import io.awesome.gagtube.util.Constants;
import io.awesome.gagtube.util.DeviceUtils;
import io.awesome.gagtube.util.ExtractorHelper;
import io.awesome.gagtube.util.GlideUtils;
import io.awesome.gagtube.util.Localization;
import io.awesome.gagtube.util.NavigationHelper;
import io.awesome.gagtube.util.OnClickGesture;
import io.awesome.gagtube.util.PermissionHelper;
import io.awesome.gagtube.util.SharedUtils;
import io.awesome.gagtube.util.TextLinkUtils;
import io.awesome.gagtube.util.ThemeHelper;
import io.awesome.gagtube.util.text.TextLinkifier;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class VideoDetailFragment extends BaseStateFragment<StreamInfo> implements BackPressable,
        View.OnClickListener,
        PlayerServiceExtendedEventListener,
        OnKeyDownListener {

    private static final String TAG = VideoDetailFragment.class.getSimpleName();
    private InfoItemBuilder infoItemBuilder = null;

    public static final float MAX_OVERLAY_ALPHA = 1.0f;
    private static final float MAX_PLAYER_HEIGHT = 0.7f;

    public static final String ACTION_SHOW_MAIN_PLAYER = "com.android.protube.VideoDetailFragment.ACTION_SHOW_MAIN_PLAYER";
    public static final String ACTION_HIDE_MAIN_PLAYER = "com.android.protube.VideoDetailFragment.ACTION_HIDE_MAIN_PLAYER";
    public static final String ACTION_MINIMIZE_MAIN_PLAYER = "com.android.protube.VideoDetailFragment.ACTION_MINIMIZE_MAIN_PLAYER";
    public static final String ACTION_PLAYER_STARTED = "com.android.protube.VideoDetailFragment.ACTION_PLAYER_STARTED";
    public static final String ACTION_VIDEO_FRAGMENT_RESUMED = "com.android.protube.VideoDetailFragment.ACTION_VIDEO_FRAGMENT_RESUMED";
    public static final String ACTION_VIDEO_FRAGMENT_STOPPED = "com.android.protube.VideoDetailFragment.ACTION_VIDEO_FRAGMENT_STOPPED";

    @State
    protected int serviceId = Constants.YOUTUBE_SERVICE_ID;
    @State
    protected String name;
    @State
    protected String url;
    protected static PlayQueue playQueue;
    @State
    int bottomSheetState = BottomSheetBehavior.STATE_EXPANDED;
    @State
    protected boolean autoPlayEnabled = true;

    private StreamInfo currentInfo;
    private Disposable currentWorker;
    @NonNull
    private final CompositeDisposable disposables = new CompositeDisposable();
    final CompositeDisposable descriptionDisposables = new CompositeDisposable();

    private Disposable subscribeButtonMonitor;
    private SubscriptionService subscriptionService;

    private BottomSheetBehavior<FrameLayout> bottomSheetBehavior;
    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;
    private BroadcastReceiver broadcastReceiver;

    private ContentObserver settingsContentObserver;
    private MainPlayer playerService;
    private VideoPlayerImpl player;

    // Views
    private LinearLayout contentRootLayoutHiding;

    private View videoPlayerLayout;
    private View frameVideoPlayer;
    private ViewGroup playerPlaceholder;
    private ImageView thumbnailImageView;
    private ImageView thumbnailPlayButton;
    private TextView detailDurationView;

    private View videoTitleRoot;
    private TextView videoTitleTextView;
    private ImageView videoTitleToggleArrow;
    private TextView videoCountView;

    private TextView detailControlsDownload;
    private TextView detailControlsPopup;
    private TextView detailControlsAddToPlaylist;

    private LinearLayout videoDescriptionRootLayout;
    private TextView videoUploadDateView;
    private TextView videoDescriptionView;

    private View uploaderRootLayout;
    private TextView uploaderTextView;
    private ImageView uploaderThumb;
    private TextView uploaderSubscriberTextView;
    private MaterialButton channelSubscribeButton;

    // overlay views
    private MaterialCardView overlay;
    private LinearLayout overlayMetadata;
    private ImageView overlayThumbnailImageView;
    private TextView overlayTitleTextView;
    private TextView overlayChannelTextView;
    private LinearLayout overlayButtons;
    private ImageButton overlayPlayPauseButton;
    private ImageButton overlayCloseButton;

    @BindView(R.id.message_restricted)
    TextView messageRestricted;

    /* AdMob */
    @BindView(R.id.adView)
    AdView admobAdView;

    /* Facebook */
    @BindView(R.id.banner_container)
    LinearLayout fbBannerAdContainer;
    com.facebook.ads.AdView fbAdView;

    /* AppLovin */
    @BindView(R.id.adContainer)
    LinearLayout adContainer;
    private MaxAdView maxAdView;
    private FragmentVideoDetailBinding binding;
    private TabAdapter pageAdapter;
    private TabAdapter commentAdapter;

    @Override
    public void onServiceConnected(VideoPlayerImpl connectedPlayer, MainPlayer connectedPlayerService, boolean playAfterConnect) {
        player = connectedPlayer;
        playerService = connectedPlayerService;

        // It will do nothing if the player is not in fullscreen mode
        hideSystemUiIfNeeded();

        if (!player.videoPlayerSelected() && !playAfterConnect) {
            return;
        }

        if (DeviceUtils.isLandscape(requireContext())) {
            // If the video is playing but orientation changed
            // let's make the video in fullscreen again
            checkLandscape();
        } else if (player.isFullscreen() && !player.isVerticalVideo()
                // Tablet UI has orientation-independent fullscreen
                && !DeviceUtils.isTablet(activity)) {
            // Device is in portrait orientation after rotation but UI is in fullscreen.
            // Return back to non-fullscreen state
            player.toggleFullscreen();
        }

        if (playerIsNotStopped() && player.videoPlayerSelected()) {
            addVideoPlayerView();
        }

        if (playAfterConnect || (currentInfo != null && isAutoplayEnabled())) {
            openMainPlayer();
        }
    }

    @Override
    public void onServiceDisconnected() {
        playerService = null;
        player = null;
    }

    public static VideoDetailFragment getInstance(int serviceId, String videoUrl, String name, final PlayQueue queue) {

        VideoDetailFragment instance = new VideoDetailFragment();
        instance.setInitialData(serviceId, videoUrl, name, queue);
        return instance;
    }

    public static VideoDetailFragment getInstanceInCollapsedState() {
        final VideoDetailFragment instance = new VideoDetailFragment();
        instance.bottomSheetState = BottomSheetBehavior.STATE_COLLAPSED;
        instance.setBottomNavigationViewVisibility(View.VISIBLE);
        return instance;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        subscriptionService = SubscriptionService.getInstance(context);
    }

    // Fragment's Lifecycle
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        setupBroadcastReceiver();

        settingsContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            @Override
            public void onChange(final boolean selfChange) {
                if (activity != null && !globalScreenOrientationLocked(activity)) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                }
            }
        };
        activity.getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION), false, settingsContentObserver);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentVideoDetailBinding.inflate(inflater, container, false);
        ButterKnife.bind(this, binding.getRoot());
        return binding.getRoot();
    }

    @Override
    public void onPause() {
        AdMobConfig.onPause(admobAdView);
        super.onPause();
        if (currentWorker != null)
            currentWorker.dispose();
    }

    @Override
    public void onResume() {
        AdMobConfig.onResume(admobAdView);
        super.onResume();
        activity.sendBroadcast(new Intent(ACTION_VIDEO_FRAGMENT_RESUMED));

        initTabs();
        if (currentInfo != null) {
            updateTabs(currentInfo);
        }

        // Check if it was loading when the fragment was stopped/paused
        if (wasLoading.getAndSet(false) && !wasCleared()) {
            startLoading(false);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!activity.isChangingConfigurations()) {
            activity.sendBroadcast(new Intent(ACTION_VIDEO_FRAGMENT_STOPPED));
        }
    }

    @Override
    public void onDestroy() {
        AppLovinConfig.destroyBannerAd(adContainer, maxAdView);
        AdMobConfig.destroyBannerAd(admobAdView);
        FacebookConfig.destroyBannerAd(fbAdView);
        super.onDestroy();
        // Stop the service when user leaves the app with double back press
        // if video player is selected. Otherwise unbind
        if (activity.isFinishing() && player != null && player.videoPlayerSelected()) {
            PlayerHolder.stopService(App.getAppContext());
        } else {
            PlayerHolder.removeListener();
        }

        activity.unregisterReceiver(broadcastReceiver);
        activity.getContentResolver().unregisterContentObserver(settingsContentObserver);

        if (currentWorker != null) {
            currentWorker.dispose();
        }
        disposables.clear();
        descriptionDisposables.clear();
        currentWorker = null;
        bottomSheetBehavior.removeBottomSheetCallback(bottomSheetCallback);

        if (activity.isFinishing()) {
            playQueue = null;
            currentInfo = null;
            stack = new LinkedList<>();
        }
    }

    @Override
    public void onDestroyView() {
        FacebookConfig.onDestroyView(fbBannerAdContainer);
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ReCaptchaActivity.RECAPTCHA_REQUEST && resultCode == Activity.RESULT_OK) {
            if (currentInfo != null) {
                NavigationHelper.openVideoDetailFragment(getFM(), serviceId, url, name);
            }
        }
    }

    // OnClick
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.detail_controls_download:
                if (PermissionHelper.checkStoragePermissions(activity, PermissionHelper.DOWNLOAD_DIALOG_REQUEST_CODE)) {
                    if (AppUtils.isShowInterstitialDownload(activity)) {
                        AdUtils.showInterstitialAdDownload(activity, this::openDownloadDialog);
                    } else {
                        openDownloadDialog();
                    }
                }
                break;

            case R.id.detail_controls_popup:
                openPopupPlayer(false);
                break;

            case R.id.detail_controls_playlist_append:
                if (getFM() != null && currentInfo != null) {
                    PlaylistAppendDialog.fromStreamInfo(currentInfo).show(getFM(), TAG);
                }
                break;

            case R.id.detail_uploader_root_layout:
                try {
                    if (!TextUtils.isEmpty(currentInfo.getUploaderUrl())) {
                        NavigationHelper.openChannelFragment(getFM(), currentInfo.getServiceId(), currentInfo.getUploaderUrl(), currentInfo.getUploaderName());
                    }
                } catch (Exception ignored) {
                }
                break;

            case R.id.detail_title_root_layout:
                toggleTitleAndDescription();
                break;

            case R.id.frame_video_player:
                openMainPlayer();
                break;

            case R.id.overlay_thumbnail:
            case R.id.overlay_metadata_layout:
            case R.id.overlay_buttons_layout:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                setBottomNavigationViewVisibility(View.GONE);
                break;

            case R.id.overlay_play_pause_button:
                if (playerIsNotStopped()) {
                    player.onPlayPause();
                    player.hideControls(0, 0);
                    if (DeviceUtils.isLandscape(requireContext())) {
                        showSystemUi();
                    }
                } else {
                    openMainPlayer();
                }

                setOverlayPlayPauseImage(player != null && player.isPlaying());
                break;

            case R.id.overlay_close_button:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                setBottomNavigationViewVisibility(View.VISIBLE);
                break;

            case R.id.comment_section:
                ViewUtils.slideUp(binding.commentsContainer, 120, 150, 0.06f);
                break;
        }
    }

    public void openDownloadDialog() {
        try {
            DownloadDialog downloadDialog = DownloadDialog.newInstance(activity, currentInfo);
            downloadDialog.show(activity.getSupportFragmentManager(), "DownloadDialog");
        } catch (Exception e) {
            Toast.makeText(activity, R.string.could_not_setup_download_menu, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void toggleTitleAndDescription() {
        if (videoDescriptionRootLayout.getVisibility() == View.VISIBLE) {
            videoTitleTextView.setMaxLines(2);
            videoDescriptionRootLayout.setVisibility(View.GONE);
            videoTitleToggleArrow.setImageResource(R.drawable.ic_arrow_down);
        } else {
            videoTitleTextView.setMaxLines(20);
            videoDescriptionRootLayout.setVisibility(View.VISIBLE);
            videoTitleToggleArrow.setImageResource(R.drawable.ic_arrow_up);
        }
    }

    // Init
    @Override
    protected void initViews(View rootView, Bundle savedInstanceState) {
        super.initViews(rootView, savedInstanceState);

        pageAdapter = new TabAdapter(getChildFragmentManager());
        binding.viewPager.setAdapter(pageAdapter);

        commentAdapter = new TabAdapter(getChildFragmentManager());
        binding.commentsViewPager.setAdapter(commentAdapter);

        if (AdUtils.ENABLE_ADMOB()) {
            // Banner
            AdMobConfig.showAdMobBannerAd(admobAdView);
        } else if (AdUtils.ENABLE_FACEBOOK()) {
            // Banner
            fbAdView = new com.facebook.ads.AdView(activity, getString(R.string.facebook_ad_banner), AdSize.BANNER_HEIGHT_50);
            FacebookConfig.showFBBannerAd(fbBannerAdContainer, fbAdView);
        } else if (AdUtils.ENABLE_APPLOVIN()) {
            maxAdView = new MaxAdView(getString(R.string.applovin_banner_ad), activity);
            AppLovinConfig.showBannerAd(activity, adContainer, maxAdView);
        }

        contentRootLayoutHiding = rootView.findViewById(R.id.detail_content_root_hiding);

        // video player
        videoPlayerLayout = rootView.findViewById(R.id.video_player_layout);
        frameVideoPlayer = rootView.findViewById(R.id.frame_video_player);
        thumbnailImageView = rootView.findViewById(R.id.detail_thumbnail_image_view);
        thumbnailPlayButton = rootView.findViewById(R.id.detail_thumbnail_play_button);
        detailDurationView = rootView.findViewById(R.id.detail_duration_view);
        playerPlaceholder = rootView.findViewById(R.id.player_placeholder);

        // title
        videoTitleRoot = rootView.findViewById(R.id.detail_title_root_layout);
        videoTitleTextView = rootView.findViewById(R.id.detail_video_title_view);
        videoTitleToggleArrow = rootView.findViewById(R.id.detail_toggle_description_view);
        videoCountView = rootView.findViewById(R.id.detail_view_count_view);

        // control views
        detailControlsDownload = rootView.findViewById(R.id.detail_controls_download);
        detailControlsDownload.setVisibility(AppUtils.isDownloadVisible(activity) ? View.VISIBLE : View.GONE);
        detailControlsPopup = rootView.findViewById(R.id.detail_controls_popup);
        detailControlsAddToPlaylist = rootView.findViewById(R.id.detail_controls_playlist_append);

        // description
        videoDescriptionRootLayout = rootView.findViewById(R.id.detail_description_root_layout);
        videoUploadDateView = rootView.findViewById(R.id.detail_upload_date_view);
        videoDescriptionView = rootView.findViewById(R.id.detail_description_view);
        videoDescriptionView.setMovementMethod(LinkMovementMethod.getInstance());
        videoDescriptionView.setAutoLinkMask(Linkify.WEB_URLS);
        videoDescriptionView.setLinkTextColor(ContextCompat.getColor(activity, R.color.md_blue_500));

        // channel views
        uploaderRootLayout = rootView.findViewById(R.id.detail_uploader_root_layout);
        uploaderTextView = rootView.findViewById(R.id.detail_uploader_text_view);
        uploaderThumb = rootView.findViewById(R.id.detail_uploader_thumbnail_view);
        uploaderSubscriberTextView = rootView.findViewById(R.id.detail_uploader_subscriber_text_view);
        channelSubscribeButton = rootView.findViewById(R.id.channel_subscribe_button);

        // overlay views
        overlay = rootView.findViewById(R.id.overlay_layout);
        overlayMetadata = rootView.findViewById(R.id.overlay_metadata_layout);
        overlayThumbnailImageView = rootView.findViewById(R.id.overlay_thumbnail);
        overlayTitleTextView = rootView.findViewById(R.id.overlay_title_text_view);
        overlayChannelTextView = rootView.findViewById(R.id.overlay_channel_text_view);
        overlayButtons = rootView.findViewById(R.id.overlay_buttons_layout);
        overlayPlayPauseButton = rootView.findViewById(R.id.overlay_play_pause_button);
        overlayCloseButton = rootView.findViewById(R.id.overlay_close_button);

        infoItemBuilder = new InfoItemBuilder(activity);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initListeners() {
        super.initListeners();
        infoItemBuilder.setOnStreamSelectedListener(new OnClickGesture<StreamInfoItem>() {

            @Override
            public void selected(StreamInfoItem selectedItem) {
                scrollToTop();
                NavigationHelper.openVideoDetailFragment(getFM(), selectedItem.getServiceId(), selectedItem.getUrl(), selectedItem.getName());
            }

            @Override
            public void more(StreamInfoItem selectedItem, View view) {
                showPopupMenu(selectedItem, view);
            }
        });

        infoItemBuilder.setOnPlaylistSelectedListener(new OnClickGesture<PlaylistInfoItem>() {
            @Override
            public void selected(PlaylistInfoItem selectedItem) {
                NavigationHelper.openPlaylistFragment(getFM(),
                        selectedItem.getServiceId(),
                        selectedItem.getUrl(),
                        selectedItem.getName());
            }
        });

        frameVideoPlayer.setOnClickListener(this);
        videoTitleRoot.setOnClickListener(this);
        uploaderRootLayout.setOnClickListener(this);
        detailControlsDownload.setOnClickListener(this);
        detailControlsPopup.setOnClickListener(this);
        detailControlsAddToPlaylist.setOnClickListener(this);
        overlayThumbnailImageView.setOnClickListener(this);
        overlayMetadata.setOnClickListener(this);
        overlayButtons.setOnClickListener(this);
        overlayCloseButton.setOnClickListener(this);
        overlayPlayPauseButton.setOnClickListener(this);
        binding.commentSection.setOnClickListener(this);

        setupBottomPlayer();
        if (!PlayerHolder.bound) {
            setHeightThumbnail();
        } else {
            PlayerHolder.startService(App.getAppContext(), true, this);
        }
    }

    private void initThumbnailViews(@NonNull StreamInfo info) {
        if (!TextUtils.isEmpty(info.getThumbnailUrl())) {
            GlideUtils.loadThumbnail(App.getAppContext(), thumbnailImageView, info.getThumbnailUrl());
        }

        if (!TextUtils.isEmpty(info.getUploaderAvatarUrl())) {
            GlideUtils.loadAvatar(App.getAppContext(), uploaderThumb, info.getUploaderAvatarUrl().replace("s48", "s720"));
        }
    }

    /**
     * Stack that contains the "navigation history".<br>
     * The peek is the current video.
     */
    private static LinkedList<StackItem> stack = new LinkedList<>();

    @Override
    public boolean onKeyDown(final int keyCode) {
        return player != null && player.onKeyDown(keyCode);
    }

    @Override
    public boolean onBackPressed() {
        if (binding.commentsContainer.getVisibility() == View.VISIBLE) {
            animate(binding.commentsContainer, false, 100);
            return true;
        }
        /*// If we are in fullscreen mode just exit from it via first back press
        if (player != null && player.isFullscreen()) {
            if (!DeviceUtils.isTablet(activity)) {
                player.onPlay();
            }
            restoreDefaultOrientation();
            setAutoplay(true);
            return true;
        }*/

        // If we have something in history of played items we replay it here
        if (player != null && player.getPlayQueue() != null && player.videoPlayerSelected() && player.getPlayQueue().previous()) {
            return true;
        }
        // That means that we are on the start of the stack,
        // return false to let the MainActivity handle the onBack
        if (stack.size() <= 1) {
            restoreDefaultOrientation();
            return false;
        }

        // Remove top
        stack.pop();
        // Get stack item from the new top
        setupFromHistoryItem(stack.peek());

        return true;
    }

    private void setupFromHistoryItem(final StackItem item) {
        setAutoplay(true);
        hideMainPlayer();

        setInitialData(item.getServiceId(), item.getUrl(), !TextUtils.isEmpty(item.getTitle()) ? item.getTitle() : "", item.getPlayQueue());
        startLoading(false);

        // Maybe an item was deleted in background activity
        if (item.getPlayQueue().getItem() == null) {
            return;
        }

        final PlayQueueItem playQueueItem = item.getPlayQueue().getItem();
        // Update title, url, uploader from the last item in the stack (it's current now)
        final boolean isPlayerStopped = player == null || player.isPlayerStopped();
        if (playQueueItem != null && isPlayerStopped) {
            updateOverlayData(playQueueItem.getTitle(), playQueueItem.getUploader(), playQueueItem.getThumbnailUrl());
        }
    }

    // Info loading and handling
    @Override
    protected void doInitialLoadLogic() {
        if (wasCleared()) {
            return;
        }

        if (currentInfo == null) {
            prepareAndLoadInfo();
        } else {
            prepareAndHandleInfoIfNeededAfterDelay(currentInfo, true, 50);
        }
    }

    public void selectAndLoadVideo(final int sid, final String videoUrl, final String title, final PlayQueue queue) {
        // Situation when user switches from players to main player.
        // All needed data is here, we can start watching
        if (playQueue != null && playQueue.equals(queue)) {
            openMainPlayer();
            return;
        }
        setInitialData(sid, videoUrl, title, queue);
        if (player != null) {
            player.disablePreloadingOfCurrentTrack();
        }
        startLoading(true, true);
    }

    private void prepareAndHandleInfoIfNeededAfterDelay(final StreamInfo info, final boolean scrollToTop, final long delay) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (activity == null) {
                return;
            }
            // Data can already be drawn, don't spend time twice
            if (info.getName().equals(videoTitleTextView.getText().toString())) {
                return;
            }
            prepareAndHandleInfo(info, scrollToTop);
        }, delay);
    }

    private void prepareAndHandleInfo(final StreamInfo info, final boolean scrollToTop) {
        showLoading();
        initTabs();
        if (scrollToTop) {
            scrollToTop();
        }
        showContent();
        handleResult(info);
    }

    protected void prepareAndLoadInfo() {
        scrollToTop();
        startLoading(false);
    }

    @Override
    public void startLoading(final boolean forceLoad) {
        super.startLoading(forceLoad);
        initTabs();
        currentInfo = null;
        if (currentWorker != null) {
            currentWorker.dispose();
        }

        runWorker(forceLoad, stack.isEmpty());
    }

    private void startLoading(final boolean forceLoad, final boolean addToBackStack) {
        super.startLoading(forceLoad);
        currentInfo = null;
        if (currentWorker != null) {
            currentWorker.dispose();
        }

        runWorker(forceLoad, addToBackStack);
    }

    private void runWorker(final boolean forceLoad, final boolean addToBackStack) {
        currentWorker = ExtractorHelper.getStreamInfo(serviceId, url, forceLoad)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    isLoading.set(false);
                    hideMainPlayer();
                    if (result.getAgeLimit() != NO_AGE_LIMIT) {
                        showAgeRestrictedContent();
                    } else {
                        showContent();
                        handleResult(result);
                        if (addToBackStack) {
                            if (playQueue == null) {
                                playQueue = new SinglePlayQueue(result);
                            }
                            if (stack.isEmpty() || !stack.peek().getPlayQueue().equals(playQueue)) {
                                stack.push(new StackItem(serviceId, url, name, playQueue));
                            }
                        }
                    }
                }, throwable -> {
                    isLoading.set(false);
                    onError(throwable);
                });
    }

    public void scrollToTop() {
        binding.appBarLayout.setExpanded(true, true);
        binding.viewPager.scrollTo(0, 0);
    }

    private void openPopupPlayer(final boolean append) {
        if (!PermissionHelper.isPopupEnabled(activity)) {
            PermissionHelper.showPopupEnableToast(activity);
            return;
        }

        // See UI changes while remote playQueue changes
        if (player == null) {
            PlayerHolder.startService(App.getAppContext(), true, this);
        }

        //  If a user watched video inside fullscreen mode and than chose another player
        //  return to non-fullscreen mode
        if (player != null && player.isFullscreen()) {
            player.toggleFullscreen();
        }

        final PlayQueue queue = setupPlayQueueForIntent(append);
        if (append) {
            NavigationHelper.enqueueOnPopupPlayer(activity, queue, false);
        } else {
            NavigationHelper.playOnPopupPlayer(activity, queue, true);
        }
    }

    private void openMainPlayer() {
        if (playerService == null || !PlayerHolder.bound) {
            PlayerHolder.startService(App.getAppContext(), true, this);
            return;
        }

        // Video view can have elements visible from popup,
        // We hide it here but once it ready the view will be shown in handleIntent()
        if (playerService.getView() != null) {
            playerService.getView().setVisibility(View.GONE);
        }
        addVideoPlayerView();

        Intent playerIntent;
        if (currentInfo != null) {
            final PlayQueue queue = setupPlayQueueForIntent(false);
            playerIntent = NavigationHelper.getPlayerIntent(activity, MainPlayer.class, queue, null, true);
        } else {
            playerIntent = NavigationHelper.getPlayerIntent(activity, MainPlayer.class, null, null, true);
        }
        playerIntent.putExtra(AUTO_PLAY, isAutoplayEnabled());
        activity.startService(playerIntent);
    }

    private void hideMainPlayer() {
        if (playerService == null || playerService.getView() == null || !player.videoPlayerSelected()) {
            return;
        }

        removeVideoPlayerView();
        playerService.stop(isAutoplayEnabled());
        playerService.getView().setVisibility(View.GONE);
    }

    private PlayQueue setupPlayQueueForIntent(final boolean append) {
        if (append) {
            return new SinglePlayQueue(currentInfo);
        }

        PlayQueue queue = playQueue;
        // Size can be 0 because queue removes bad stream automatically when error occurs
        if (queue == null || queue.size() == 0) {
            queue = new SinglePlayQueue(currentInfo);
        }

        return queue;
    }

    public void setAutoplay(final boolean autoplay) {
        this.autoPlayEnabled = autoplay;
    }

    // This method overrides default behaviour when setAutoplay() is called.
    // Don't auto play if the user selected an external player or disabled it in settings
    private boolean isAutoplayEnabled() {
        return autoPlayEnabled && (player == null || player.videoPlayerSelected()) && bottomSheetState != BottomSheetBehavior.STATE_HIDDEN;
    }

    private void addVideoPlayerView() {
        if (player == null || getView() == null) {
            return;
        }

        // Check if viewHolder already contains a child
        if (player.getRootView().getParent() != playerPlaceholder) {
            playerService.removeViewFromParent();
        }
        setHeightThumbnail();

        // Prevent from re-adding a view multiple times
        if (player.getRootView().getParent() == null) {
            playerPlaceholder.addView(player.getRootView());
        }
    }

    private void removeVideoPlayerView() {
        makeDefaultHeightForVideoPlaceholder();
        playerService.removeViewFromParent();
    }

    private void makeDefaultHeightForVideoPlaceholder() {
        if (getView() == null) {
            return;
        }
        playerPlaceholder.getLayoutParams().height = FrameLayout.LayoutParams.MATCH_PARENT;
        playerPlaceholder.requestLayout();
    }

    // Utils
    private void prepareDescription(final StreamInfo streamInfo) {
        final Description description = streamInfo.getDescription();
        switch (description.getType()) {
            case Description.HTML:
                TextLinkUtils.createLinksFromHtmlBlock(videoDescriptionView, description.getContent(), HtmlCompat.FROM_HTML_MODE_LEGACY, streamInfo, descriptionDisposables);
                break;
            case Description.MARKDOWN:
                TextLinkUtils.createLinksFromMarkdownText(videoDescriptionView, description.getContent(), streamInfo, descriptionDisposables);
                break;
            case Description.PLAIN_TEXT:
            default:
                TextLinkUtils.createLinksFromPlainText(videoDescriptionView, description.getContent(), streamInfo, descriptionDisposables);
                break;
        }
    }

    private final ViewTreeObserver.OnPreDrawListener preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            final DisplayMetrics metrics = getResources().getDisplayMetrics();

            if (getView() != null) {
                final int height = isInMultiWindow() ? requireView().getHeight() : activity.getWindow().getDecorView().getHeight();
                setHeightThumbnail(height, metrics);
                getView().getViewTreeObserver().removeOnPreDrawListener(preDrawListener);
            }
            return false;
        }
    };

    /**
     * Method which controls the size of thumbnail and the size of main player inside
     * a layout with thumbnail. It decides what height the player should have in both
     * screen orientations. It knows about multiWindow feature
     * and about videos with aspectRatio ZOOM (the height for them will be a bit higher,
     * {@link #MAX_PLAYER_HEIGHT})
     */
    private void setHeightThumbnail() {
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        final boolean isPortrait = metrics.heightPixels > metrics.widthPixels;
        requireView().getViewTreeObserver().removeOnPreDrawListener(preDrawListener);

        if (player != null && player.isFullscreen()) {
            final int height = isInMultiWindow() ? requireView().getHeight() : activity.getWindow().getDecorView().getHeight();
            // Height is zero when the view is not yet displayed like after orientation change
            if (height != 0) {
                setHeightThumbnail(height, metrics);
            } else {
                requireView().getViewTreeObserver().addOnPreDrawListener(preDrawListener);
            }
        } else {
            final int height = isPortrait ? (int) (metrics.widthPixels / (16.0f / 9.0f)) : (int) (metrics.heightPixels / 2.0f);
            setHeightThumbnail(height, metrics);
        }
    }

    private void setHeightThumbnail(final int newHeight, final DisplayMetrics metrics) {
        thumbnailImageView.setLayoutParams(new FrameLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, newHeight));
        thumbnailImageView.setMinimumHeight(newHeight);
        if (player != null) {
            final int maxHeight = (int) (metrics.heightPixels * MAX_PLAYER_HEIGHT);
            player.getSurfaceView().setHeights(newHeight, player.isFullscreen() ? newHeight : maxHeight);
        }
    }

    private void showContent() {
        contentRootLayoutHiding.setVisibility(View.VISIBLE);
        binding.viewPager.setVisibility(View.VISIBLE);
    }

    protected void setInitialData(int serviceId, String url, String name, final PlayQueue queue) {
        this.serviceId = serviceId;
        this.url = url;
        this.name = !TextUtils.isEmpty(name) ? name : "";
        playQueue = queue;
    }

    private void setErrorImage(final int imageResource) {
        if (thumbnailImageView != null) {
            thumbnailImageView.setImageDrawable(ContextCompat.getDrawable(activity, imageResource));
            animateView(thumbnailImageView, false, 0, 0, () -> animateView(thumbnailImageView, true, 0));
        }
    }

    @Override
    public void showError(String message, boolean showRetryButton) {
        showError(message, showRetryButton, R.drawable.no_image);
    }

    protected void showError(String message, boolean showRetryButton, @DrawableRes int imageError) {
        super.showError(message, showRetryButton);
        setErrorImage(imageError);
    }

    private void setupBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                if (intent == null || intent.getAction() == null)
                    return;
                switch (intent.getAction()) {
                    case ACTION_SHOW_MAIN_PLAYER:
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        setBottomNavigationViewVisibility(View.GONE);
                        break;
                    case ACTION_HIDE_MAIN_PLAYER:
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        setBottomNavigationViewVisibility(View.VISIBLE);
                        break;
                    case ACTION_MINIMIZE_MAIN_PLAYER:
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        setBottomNavigationViewVisibility(View.VISIBLE);
                        break;
                    case ACTION_PLAYER_STARTED:
                        // If the state is not hidden we don't need to show the mini player
                        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            setBottomNavigationViewVisibility(View.VISIBLE);
                        }
                        // Rebound to the service if it was closed via notification or mini player
                        if (!PlayerHolder.bound) {
                            PlayerHolder.startService(App.getAppContext(), true, VideoDetailFragment.this);
                        }
                        break;
                }
            }
        };
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_SHOW_MAIN_PLAYER);
        intentFilter.addAction(ACTION_HIDE_MAIN_PLAYER);
        intentFilter.addAction(ACTION_MINIMIZE_MAIN_PLAYER);
        intentFilter.addAction(ACTION_PLAYER_STARTED);
        activity.registerReceiver(broadcastReceiver, intentFilter);
    }

    // Orientation listener
    private void restoreDefaultOrientation() {
        if (player == null || !player.videoPlayerSelected() || activity == null) {
            return;
        }

        if (player != null && player.isFullscreen()) {
            player.toggleFullscreen();
        }
        // This will show systemUI and pause the player.
        // User can tap on Play button and video will be in fullscreen mode again
        // Note for tablet: trying to avoid orientation changes since it's not easy
        // to physically rotate the tablet every time
        if (!DeviceUtils.isTablet(activity)) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    // Contract
    @Override
    public void showLoading() {
        super.showLoading();

        // if data is already cached, transition from VISIBLE -> INVISIBLE -> VISIBLE is not required
        if (!ExtractorHelper.isCached(serviceId, url, InfoItem.InfoType.STREAM)) {
            contentRootLayoutHiding.setVisibility(View.INVISIBLE);
            binding.viewPager.setVisibility(View.INVISIBLE);
        }

        animateView(thumbnailPlayButton, false, 50);
        animateView(detailDurationView, false, 100);
        videoTitleTextView.setText(name != null ? name : "");
        videoTitleTextView.setMaxLines(2);
        animateView(videoTitleTextView, true, 0);

        videoDescriptionRootLayout.setVisibility(View.GONE);
        videoTitleToggleArrow.setImageResource(R.drawable.ic_arrow_down);
        videoTitleToggleArrow.setVisibility(View.GONE);
        videoTitleRoot.setClickable(false);
        uploaderThumb.setImageBitmap(null);
    }

    @SuppressLint("CheckResult")
    @Override
    public void handleResult(@NonNull StreamInfo streamInfo) {
        super.handleResult(streamInfo);

        currentInfo = streamInfo;
        setInitialData(streamInfo.getServiceId(), streamInfo.getOriginalUrl(), streamInfo.getName(), playQueue);
        updateTabs(currentInfo);
        displayCommentSection();

        if (isAutoplayEnabled()) {
            openMainPlayer();
        }

        animateView(thumbnailPlayButton, true, 200);
        videoTitleTextView.setText(name);

        if (!TextUtils.isEmpty(streamInfo.getUploaderName())) {
            uploaderTextView.setText(streamInfo.getUploaderName());
            uploaderTextView.setVisibility(View.VISIBLE);
            uploaderTextView.setSelected(true);
        } else {
            uploaderTextView.setVisibility(View.GONE);
        }

        videoCountView.setText(getStreamInfoDetail(streamInfo));

        uploaderSubscriberTextView.setVisibility(streamInfo.getUploaderSubscriberCount() > 0 ? View.VISIBLE : View.GONE);
        uploaderSubscriberTextView.setText(Localization.shortSubscriberCount(activity, streamInfo.getUploaderSubscriberCount()));

        // get channel's subscribers
        if (subscribeButtonMonitor != null)
            subscribeButtonMonitor.dispose();
        ExtractorHelper.getChannelInfo(this.serviceId, streamInfo.getUploaderUrl(), true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                        // onNext
                        channelInfo -> {
                            updateSubscription(channelInfo);
                            monitorSubscription(channelInfo);
                        },
                        // onError
                        throwable -> {
                            uploaderSubscriberTextView.setVisibility(View.GONE);
                            uploaderSubscriberTextView.setText(R.string.unknown_content);
                        });

        if (streamInfo.getDuration() > 0) {
            detailDurationView.setText(Localization.getDurationString(streamInfo.getDuration()));
            detailDurationView.setBackgroundResource(R.drawable.duration_background);
            animateView(detailDurationView, true, 100);
        } else if (streamInfo.getStreamType() == StreamType.LIVE_STREAM) {
            detailDurationView.setText(R.string.duration_live);
            detailDurationView.setBackgroundResource(R.drawable.duration_background_live);
            animateView(detailDurationView, true, 100);
        } else {
            detailDurationView.setVisibility(View.GONE);
        }

        videoTitleRoot.setClickable(true);
        videoTitleToggleArrow.setVisibility(View.VISIBLE);
        videoTitleToggleArrow.setImageResource(R.drawable.ic_arrow_down);
        videoDescriptionView.setVisibility(View.GONE);
        videoDescriptionRootLayout.setVisibility(View.GONE);
        if (streamInfo.getUploadDate() != null) {
            videoUploadDateView.setText(Localization.localizeDate(activity, streamInfo.getUploadDate().date().getTime()));
        }

        initThumbnailViews(streamInfo);
        prepareDescription(streamInfo);

        if (player == null || player.isPlayerStopped()) {
            updateOverlayData(streamInfo.getName(), streamInfo.getUploaderName(), streamInfo.getThumbnailUrl());
        }

//		if (!streamInfo.getErrors().isEmpty()) {
//			showSnackBarError(streamInfo.getErrors(), UserAction.REQUESTED_STREAM, NewPipe.getNameOfService(streamInfo.getServiceId()), streamInfo.getUrl(), 0);
//		}

        final boolean noVideoStreams = streamInfo.getVideoStreams().isEmpty() && streamInfo.getVideoOnlyStreams().isEmpty();
        detailControlsPopup.setVisibility(noVideoStreams ? View.GONE : View.VISIBLE);
        thumbnailPlayButton.setImageResource(noVideoStreams ? R.drawable.ic_headset_white_shadow_24dp : R.drawable.ic_play_arrow_white_shadow_24dp);
    }

    @SuppressLint("CheckResult")
    private void displayCommentSection() {
        ExtractorHelper.getCommentsInfo(serviceId, url, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                        commentsInfo -> {
                            if (activity != null && binding != null) {
                                activity.runOnUiThread(() -> {
                                    if (commentsInfo.isCommentsDisabled()) {
                                        binding.commentHeader.setText(getString(R.string.comments));
                                        binding.commentSection.setEnabled(false);
                                        binding.userCommentAvatar.setVisibility(View.GONE);
                                        binding.userComment.setVisibility(View.GONE);
                                        binding.comment.setText(R.string.comments_are_turned_off);
                                        binding.comment.setPadding(0, 0, 0, 0);
                                    } else {
                                        binding.commentHeader.setText(Localization.concatenateStrings(getString(R.string.comments), Localization.shortCount(activity, commentsInfo.getCommentsCount())));
                                        if (commentsInfo.getCommentsCount() > 0) {
                                            CommentsInfoItem commentsInfoItem = commentsInfo.getRelatedItems().get(0);
                                            binding.commentSection.setEnabled(true);
                                            GlideUtils.loadAvatar(activity, binding.userCommentAvatar, commentsInfoItem.getUploaderAvatarUrl());
                                            binding.userComment.setText(commentsInfoItem.getUploaderName());
                                            TextLinkifier.fromDescription(binding.comment, commentsInfoItem.getCommentText(), HtmlCompat.FROM_HTML_MODE_LEGACY, ServiceList.YouTube, commentsInfo.getUrl(), disposables, null);
                                            binding.userCommentAvatar.setVisibility(View.VISIBLE);
                                            binding.comment.setPadding(DeviceUtils.dpToPx(8, activity), 0, 0, 0);
                                            binding.userComment.setVisibility(View.VISIBLE);
                                        } else {
                                            binding.commentSection.setEnabled(false);
                                            binding.userCommentAvatar.setVisibility(View.GONE);
                                            binding.userComment.setVisibility(View.GONE);
                                            binding.comment.setPadding(0, 0, 0, 0);
                                            binding.comment.setText(R.string.no_comments);
                                        }
                                    }
                                    binding.commentSection.setVisibility(View.VISIBLE);
                                });
                            }
                        }, throwable -> {
                            if (activity != null && binding != null) {
                                activity.runOnUiThread(() -> binding.commentSection.setVisibility(View.GONE));
                            }
                        }
                );
    }

    private String getStreamInfoDetail(final StreamInfo streamInfo) {
        String detailInfo = "";
        if (streamInfo.getViewCount() >= 0) {
            if (streamInfo.getStreamType().equals(StreamType.AUDIO_LIVE_STREAM)) {
                detailInfo += Localization.listeningCount(activity, streamInfo.getViewCount());
            } else if (streamInfo.getStreamType().equals(StreamType.LIVE_STREAM)) {
                detailInfo += Localization.shortViewCount(activity, streamInfo.getViewCount());
            } else {
                detailInfo += Localization.shortViewCount(activity, streamInfo.getViewCount());
            }
        }

        final String uploadDate = getFormattedRelativeUploadDate(streamInfo);
        if (!TextUtils.isEmpty(uploadDate)) {
            return Localization.concatenateStrings(detailInfo, uploadDate);
        }
        return detailInfo;
    }

    private String getFormattedRelativeUploadDate(final StreamInfo streamInfo) {
        if (streamInfo.getUploadDate() != null) {
            return Localization.relativeTime(streamInfo.getUploadDate().date());
        } else {
            return streamInfo.getTextualUploadDate();
        }
    }

    private void showAgeRestrictedContent() {
        messageRestricted.setVisibility(View.VISIBLE);
    }

    @Override
    protected boolean onError(Throwable exception) {
        if (super.onError(exception))
            return true;
        if (exception instanceof ContentNotAvailableException) {
            showError(getString(R.string.content_not_available), false);
        }
//		else {
//			int errorId = exception instanceof YoutubeStreamExtractor.DeobfuscateException
//					? R.string.youtube_signature_decryption_error
//					: exception instanceof ParsingException
//					? R.string.parsing_error
//					: R.string.general_error;
//			onUnrecoverableError(exception, UserAction.REQUESTED_STREAM, NewPipe.getNameOfService(serviceId), url, errorId);
//		}
        return true;
    }

    @Override
    public void onQueueUpdate(PlayQueue queue) {
        playQueue = queue;
        // This should be the only place where we push data to stack.
        // It will allow to have live instance of PlayQueue with actual information about
        // deleted/added items inside Channel/Playlist queue and makes possible to have
        // a history of played items
        if ((stack.isEmpty() || !stack.peek().getPlayQueue().equals(queue) && queue.getItem() != null)) {
            stack.push(new StackItem(queue.getItem().getServiceId(), queue.getItem().getUrl(), queue.getItem().getTitle(), queue));
        } else {
            final StackItem stackWithQueue = findQueueInStack(queue);
            if (stackWithQueue != null) {
                // On every MainPlayer service's destroy() playQueue gets disposed and
                // no longer able to track progress. That's why we update our cached disposed
                // queue with the new one that is active and have the same history.
                // Without that the cached playQueue will have an old recovery position
                stackWithQueue.setPlayQueue(queue);
            }
        }
    }

    @Override
    public void onPlaybackUpdate(int state, int repeatMode, boolean shuffled, PlaybackParameters parameters) {
        setOverlayPlayPauseImage(player != null && player.isPlaying());
    }

    @Override
    public void onProgressUpdate(int currentProgress, int duration, int bufferPercent) {

    }

    @Override
    public void onMetadataUpdate(StreamInfo info, PlayQueue queue) {
        final StackItem item = findQueueInStack(queue);
        if (item != null) {
            // When PlayQueue can have multiple streams (PlaylistPlayQueue or ChannelPlayQueue)
            // every new played stream gives new title and url.
            // StackItem contains information about first played stream. Let's update it here
            item.setTitle(info.getName());
            item.setUrl(info.getUrl());
        }
        // They are not equal when user watches something in popup while browsing in fragment and
        // then changes screen orientation. In that case the fragment will set itself as
        // a service listener and will receive initial call to onMetadataUpdate()
        if (!queue.equals(playQueue)) {
            return;
        }

        updateOverlayData(info.getName(), info.getUploaderName(), info.getThumbnailUrl());
        if (currentInfo != null && info.getUrl().equals(currentInfo.getUrl())) {
            return;
        }

        currentInfo = info;
        setInitialData(info.getServiceId(), info.getUrl(), info.getName(), queue);
        setAutoplay(true);
        // Delay execution just because it freezes the main thread, and while playing
        // next/previous video you see visual glitches
        // (when non-vertical video goes after vertical video)
        prepareAndHandleInfoIfNeededAfterDelay(info, true, 200);
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        if (error.type == ExoPlaybackException.TYPE_SOURCE || error.type == ExoPlaybackException.TYPE_UNEXPECTED) {
            // Properly exit from fullscreen
            if (playerService != null && player.isFullscreen()) {
                player.toggleFullscreen();
            }
            hideMainPlayer();
        }
    }

    @Override
    public void onServiceStopped() {
        setOverlayPlayPauseImage(false);
        if (currentInfo != null) {
            updateOverlayData(currentInfo.getName(), currentInfo.getUploaderName(), currentInfo.getThumbnailUrl());
        }
    }

    @Override
    public void onFullscreenStateChanged(boolean fullscreen) {
        if (playerService.getView() == null || player.getParentActivity() == null) {
            return;
        }

        final View view = playerService.getView();
        final ViewGroup parent = (ViewGroup) view.getParent();
        if (parent == null) {
            return;
        }

        if (fullscreen) {
            hideSystemUiIfNeeded();
        } else {
            showSystemUi();
        }

        scrollToTop();
        addVideoPlayerView();

        ViewGroup.LayoutParams params = videoPlayerLayout.getLayoutParams();
        ConstraintLayout.LayoutParams frameVideoPlayerParams = (ConstraintLayout.LayoutParams) frameVideoPlayer.getLayoutParams();
        if (player.isVerticalVideo()) {
            if (fullscreen) {
                params.height = MATCH_PARENT;
                frameVideoPlayerParams.dimensionRatio = "H,9:16";
            } else {
                params.height = WRAP_CONTENT;
                frameVideoPlayerParams.dimensionRatio = "H,16:9";
            }
        } else {
            if (fullscreen) {
                params.height = MATCH_PARENT;
                frameVideoPlayerParams.dimensionRatio = null;
            } else {
                params.height = WRAP_CONTENT;
                frameVideoPlayerParams.dimensionRatio = "H,16:9";
            }
        }
        videoPlayerLayout.setLayoutParams(params);
        frameVideoPlayer.setLayoutParams(frameVideoPlayerParams);
    }

    @Override
    public void onScreenRotationButtonClicked() {
        // In tablet user experience will be better if screen will not be rotated
        // from landscape to portrait every time.
        // Just turn on fullscreen mode in landscape orientation
        // or portrait & unlocked global orientation
        final boolean isLandscape = DeviceUtils.isLandscape(requireContext());
        if (DeviceUtils.isTablet(activity) && (!globalScreenOrientationLocked(activity) || isLandscape)) {
            player.toggleFullscreen();
            return;
        }
        final int newOrientation = isLandscape ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
        activity.setRequestedOrientation(newOrientation);
    }

    private void showSystemUi() {
        if (activity == null) {
            return;
        }

        // Prevent jumping of the player on devices with cutout
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            activity.getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
        }
        activity.getWindow().getDecorView().setSystemUiVisibility(0);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().setStatusBarColor(ThemeHelper.resolveColorFromAttr(activity, android.R.attr.colorPrimary));
    }

    private void hideSystemUi() {
        if (activity == null) {
            return;
        }
        // Prevent jumping of the player on devices with cutout
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            activity.getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
        }
        int visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        // In multiWindow mode status bar is not transparent for devices with cutout
        // if I include this flag. So without it is better in this case
        if (!isInMultiWindow()) {
            visibility |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }
        activity.getWindow().getDecorView().setSystemUiVisibility(visibility);

        if (isInMultiWindow() || player != null && player.isFullscreen()) {
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    // Listener implementation
    public void hideSystemUiIfNeeded() {
        if (player != null && player.isFullscreen() && bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            hideSystemUi();
        }
    }

    private boolean playerIsNotStopped() {
        return player != null && player.getPlayer() != null && player.getPlayer().getPlaybackState() != Player.STATE_IDLE;
    }

    private void checkLandscape() {
        if ((!player.isPlaying() && player.getPlayQueue() != playQueue) || player.getPlayQueue() == null) {
            setAutoplay(true);
        }

        player.checkLandscape();
        // Let's give a user time to look at video information page if video is not playing
        if (globalScreenOrientationLocked(activity) && !player.isPlaying()) {
            player.onPlay();
        }
    }

    private boolean isInMultiWindow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && activity.isInMultiWindowMode();
    }

    /*
     * Means that the player fragment was swiped away via BottomSheetLayout
     * and is empty but ready for any new actions. See cleanUp()
     * */
    private boolean wasCleared() {
        return url == null;
    }

    private StackItem findQueueInStack(final PlayQueue queue) {
        StackItem item = null;
        final Iterator<StackItem> iterator = stack.descendingIterator();
        while (iterator.hasNext()) {
            final StackItem next = iterator.next();
            if (next.getPlayQueue().equals(queue)) {
                item = next;
                break;
            }
        }
        return item;
    }

    // Remove unneeded information while waiting for a next task
    private void cleanUp() {
        // New beginning
        stack.clear();
        if (currentWorker != null) {
            currentWorker.dispose();
        }
        PlayerHolder.stopService(App.getAppContext());
        setInitialData(0, null, "", null);
        currentInfo = null;
        updateOverlayData(null, null, null);
    }

    /**
     * Move focus from main fragment to the player or back
     * based on what is currently selected
     *
     * @param toMain if true than the main fragment will be focused or the player otherwise
     */
    private void moveFocusToMainFragment(final boolean toMain) {
        final ViewGroup mainFragment = requireActivity().findViewById(R.id.fragment_holder);
        // Hamburger button steels a focus even under bottomSheet
        final Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        final int afterDescendants = ViewGroup.FOCUS_AFTER_DESCENDANTS;
        final int blockDescendants = ViewGroup.FOCUS_BLOCK_DESCENDANTS;
        if (toolbar != null) {
            if (toMain) {
                mainFragment.setDescendantFocusability(afterDescendants);
                toolbar.setDescendantFocusability(afterDescendants);
                ((ViewGroup) requireView()).setDescendantFocusability(blockDescendants);
                mainFragment.requestFocus();
            } else {
                mainFragment.setDescendantFocusability(blockDescendants);
                toolbar.setDescendantFocusability(blockDescendants);
                ((ViewGroup) requireView()).setDescendantFocusability(afterDescendants);
            }
        }
    }

    private void showPopupMenu(final StreamInfoItem streamInfoItem, final View view) {

        PopupMenu popup = new PopupMenu(activity, view, Gravity.END, 0, R.style.mPopupMenu);
        popup.getMenuInflater().inflate(R.menu.menu_popup_detail, popup.getMenu());
        popup.show();

        popup.setOnMenuItemClickListener(item -> {

            int id = item.getItemId();
            final int index = Math.max(currentInfo.getRelatedItems().indexOf(streamInfoItem), 0);
            switch (id) {

                case R.id.action_play:
                    NavigationHelper.playOnMainPlayer(activity, getPlayQueue(index), true);
                    break;

                case R.id.action_append_playlist:
                    if (getFragmentManager() != null) {
                        PlaylistAppendDialog.fromStreamInfoItems(Collections.singletonList(streamInfoItem)).show(getFragmentManager(), TAG);
                    }
                    break;

                case R.id.action_share:
                    SharedUtils.shareUrl(activity);
                    break;
            }
            return true;
        });
    }

    private PlayQueue getPlayQueue(final int index) {

        final List<InfoItem> infoItems = currentInfo.getRelatedItems();
        List<StreamInfoItem> streamInfoItems = new ArrayList<>(infoItems.size());

        for (final InfoItem item : infoItems) {
            if (item instanceof StreamInfoItem) {
                streamInfoItems.add((StreamInfoItem) item);
            }
        }
        return new SinglePlayQueue(streamInfoItems, index);
    }

    private void monitorSubscription(final ChannelInfo info) {

        final Consumer<Throwable> onError = throwable -> {
            animateView(channelSubscribeButton, false, 100);
        };

        final Observable<List<SubscriptionEntity>> observable = subscriptionService.subscriptionTable()
                .getSubscription(info.getServiceId(), info.getUrl())
                .toObservable();

        disposables.add(observable.observeOn(AndroidSchedulers.mainThread()).subscribe(getSubscribeUpdateMonitor(info), onError));

        disposables.add(observable
                // Some updates are very rapid (when calling the updateSubscription(info), for example)
                // so only update the UI for the latest emission ("sync" the subscribe button's state)
                .debounce(100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriptionEntities -> updateSubscribeButton(!subscriptionEntities.isEmpty()), onError));

    }

    private Function<Object, Object> mapOnSubscribe(final SubscriptionEntity subscription) {

        return object -> {
            subscriptionService.subscriptionTable().insert(subscription);
            return object;
        };
    }

    private Function<Object, Object> mapOnUnsubscribe(final SubscriptionEntity subscription) {

        return object -> {
            subscriptionService.subscriptionTable().delete(subscription);
            return object;
        };
    }

    private void updateSubscription(final ChannelInfo info) {

        final Action onComplete = () -> {
        };

        final Consumer<Throwable> onError = throwable -> {
        };

        disposables.add(subscriptionService.updateChannelInfo(info)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onComplete, onError));
    }

    private Disposable monitorSubscribeButton(final Button subscribeButton, final Function<Object, Object> action) {

        final Consumer<Object> onNext = object -> {
        };

        final Consumer<Throwable> onError = throwable -> {
        };

        /* Emit clicks from main thread unto io thread */
        return RxView.clicks(subscribeButton)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .debounce(100, TimeUnit.MILLISECONDS) // Ignore rapid clicks
                .map(action)
                .subscribe(onNext, onError);
    }

    private Consumer<List<SubscriptionEntity>> getSubscribeUpdateMonitor(final ChannelInfo info) {

        return subscriptionEntities -> {

            if (subscribeButtonMonitor != null)
                subscribeButtonMonitor.dispose();

            if (subscriptionEntities.isEmpty()) {
                SubscriptionEntity channel = new SubscriptionEntity();
                channel.setServiceId(info.getServiceId());
                channel.setUrl(info.getUrl());
                channel.setData(info.getName(), info.getAvatarUrl(), info.getDescription(), info.getSubscriberCount());
                subscribeButtonMonitor = monitorSubscribeButton(channelSubscribeButton, mapOnSubscribe(channel));
            } else {
                final SubscriptionEntity subscription = subscriptionEntities.get(0);
                subscribeButtonMonitor = monitorSubscribeButton(channelSubscribeButton, mapOnUnsubscribe(subscription));
            }
        };
    }

    private void updateSubscribeButton(boolean isSubscribed) {

        boolean isButtonVisible = channelSubscribeButton.getVisibility() == View.VISIBLE;
        int backgroundDuration = isButtonVisible ? 100 : 0;
        int textDuration = isButtonVisible ? 100 : 0;

        int subscribeBackground = ContextCompat.getColor(activity, R.color.subscribe_background_color);
        int subscribeText = ContextCompat.getColor(activity, R.color.subscribe_text_color);
        int subscribedBackground = ContextCompat.getColor(activity, R.color.subscribed_background_color);
        int subscribedText = ContextCompat.getColor(activity, R.color.subscribed_text_color);

        if (!isSubscribed) {
            channelSubscribeButton.setText(R.string.subscribe_button_title);
            AnimationUtils.animateBackgroundColor(channelSubscribeButton, backgroundDuration, subscribedBackground, subscribeBackground);
            AnimationUtils.animateTextColor(channelSubscribeButton, textDuration, subscribedText, subscribeText);
        } else {
            channelSubscribeButton.setText(R.string.subscribed_button_title);
            AnimationUtils.animateBackgroundColor(channelSubscribeButton, backgroundDuration, subscribeBackground, subscribedBackground);
            AnimationUtils.animateTextColor(channelSubscribeButton, textDuration, subscribeText, subscribedText);
        }

        animateView(channelSubscribeButton, AnimationUtils.Type.LIGHT_SCALE_AND_ALPHA, true, 100);
    }

    // Bottom mini player
    private void setupBottomPlayer() {
        final CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) binding.appBarLayout.getLayoutParams();
        final AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();

        final FrameLayout bottomSheetLayout = activity.findViewById(R.id.fragment_player_holder);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.setState(bottomSheetState);
        final int peekHeight = getResources().getDimensionPixelSize(R.dimen.bottom_sheet_height);
        if (bottomSheetState != BottomSheetBehavior.STATE_HIDDEN) {
            //manageSpaceAtTheBottom(false);
            bottomSheetBehavior.setPeekHeight(peekHeight);
            if (bottomSheetState == BottomSheetBehavior.STATE_COLLAPSED) {
                overlay.setAlpha(MAX_OVERLAY_ALPHA);
                setBottomNavigationViewAlpha(MAX_OVERLAY_ALPHA);
                setBottomNavigationViewVisibility(View.VISIBLE);
            } else if (bottomSheetState == BottomSheetBehavior.STATE_EXPANDED) {
                overlay.setAlpha(0f);
                setOverlayElementsClickable(false);
                setBottomNavigationViewAlpha(0);
                setBottomNavigationViewVisibility(View.GONE);
            }
        }

        bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull final View bottomSheet, final int newState) {
                bottomSheetState = newState;

                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        moveFocusToMainFragment(true);
                        //manageSpaceAtTheBottom(true);
                        bottomSheetBehavior.setPeekHeight(0);
                        setBottomNavigationViewVisibility(View.VISIBLE);
                        setOverlayLook(behavior, 1);
                        cleanUp();
                        break;

                    case BottomSheetBehavior.STATE_EXPANDED:
                        binding.commentSection.setVisibility(View.VISIBLE);
                        moveFocusToMainFragment(false);
                        //manageSpaceAtTheBottom(false);
                        bottomSheetBehavior.setPeekHeight(peekHeight);
                        setBottomNavigationViewVisibility(View.GONE);
                        // Disable click because overlay buttons located on top of buttons
                        // from the player
                        setOverlayElementsClickable(false);
                        hideSystemUiIfNeeded();
                        // Conditions when the player should be expanded to fullscreen
                        if (DeviceUtils.isLandscape(requireContext())
                                && player != null
                                && player.isPlaying()
                                && !player.isFullscreen()
                                && !DeviceUtils.isTablet(activity)
                                && player.videoPlayerSelected()) {
                            player.toggleFullscreen();
                        }
                        setOverlayLook(behavior, 1);
                        setBottomNavigationViewLook(1);
                        break;

                    case BottomSheetBehavior.STATE_COLLAPSED:
                        if (binding.commentsContainer.getVisibility() == View.VISIBLE) {
                            animate(binding.commentsContainer, false, 100);
                        }
                        moveFocusToMainFragment(true);
                        //manageSpaceAtTheBottom(false);
                        bottomSheetBehavior.setPeekHeight(peekHeight);
                        setBottomNavigationViewVisibility(View.VISIBLE);

                        // Re-enable clicks
                        setOverlayElementsClickable(true);
                        if (player != null) {
                            player.onQueueClosed();
                        }
                        setOverlayLook(behavior, 0);
                        setBottomNavigationViewLook(0);
                        break;

                    case BottomSheetBehavior.STATE_DRAGGING:
                    case BottomSheetBehavior.STATE_SETTLING:
                        if (player != null && player.isFullscreen()) {
                            hideSystemUi();
                        }
                        if (player != null && player.isControlsVisible()) {
                            player.hideControls(0, 0);
                        }
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull final View bottomSheet, final float slideOffset) {
                setOverlayLook(behavior, slideOffset);
                setBottomNavigationViewLook(slideOffset);
            }
        };
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback);

        // User opened a new page and the player will hide itself
        activity.getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                setBottomNavigationViewVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * When the mini player exists the view underneath it is not touchable.
     * Bottom padding should be equal to the mini player's height in this case
     *
     * @param showMore whether main fragment should be expanded or not
     */
    private void manageSpaceAtTheBottom(final boolean showMore) {
        final int peekHeight = getResources().getDimensionPixelSize(R.dimen.mini_player_height);
        final ViewGroup holder = activity.findViewById(R.id.fragment_holder);
        final int newBottomPadding;
        if (showMore) {
            newBottomPadding = 0;
        } else {
            newBottomPadding = peekHeight;
        }
        if (holder.getPaddingBottom() == newBottomPadding) {
            return;
        }
        holder.setPadding(holder.getPaddingLeft(), holder.getPaddingTop(), holder.getPaddingRight(), newBottomPadding);
    }

    private void updateOverlayData(@Nullable final String title, @Nullable final String uploader, @Nullable final String thumbnailUrl) {
        overlayTitleTextView.setText(TextUtils.isEmpty(title) ? "" : title);
        overlayChannelTextView.setText(TextUtils.isEmpty(uploader) ? "" : uploader);
        if (!TextUtils.isEmpty(thumbnailUrl)) {
            GlideUtils.loadThumbnail(App.getAppContext(), overlayThumbnailImageView, thumbnailUrl);
        }
    }

    private void setOverlayPlayPauseImage(final boolean playerIsPlaying) {
        final int attr = playerIsPlaying ? R.attr.pause : R.attr.play;
        overlayPlayPauseButton.setImageResource(ThemeHelper.resolveResourceIdFromAttr(activity, attr));
    }

    private void setOverlayLook(final AppBarLayout.Behavior behavior, final float slideOffset) {
        // SlideOffset < 0 when mini player is about to close via swipe.
        // Stop animation in this case
        if (slideOffset < 0) {
            return;
        }
        overlay.setAlpha(Math.min(MAX_OVERLAY_ALPHA, 1 - slideOffset));
        behavior.setTopAndBottomOffset((int) (-thumbnailImageView.getHeight() * 2 * (1 - slideOffset) / 3));
    }

    private void setOverlayElementsClickable(final boolean enable) {
        overlayThumbnailImageView.setClickable(enable);
        overlayMetadata.setClickable(enable);
        overlayButtons.setClickable(enable);
        overlayPlayPauseButton.setClickable(enable);
        overlayCloseButton.setClickable(enable);
    }

    private void setBottomNavigationViewLook(final float slideOffset) {
        if (slideOffset < 0)
            return;
        setBottomNavigationViewAlpha(Math.min(MAX_OVERLAY_ALPHA, 1 - slideOffset));
    }

    private void initTabs() {
        pageAdapter.clearAllItems();
        pageAdapter.addFragment(new Fragment(), "NEXT_VIDEOS");
        pageAdapter.notifyDataSetUpdate();

        commentAdapter.clearAllItems();
        commentAdapter.addFragment(new Fragment(), "COMMENTS");
        commentAdapter.notifyDataSetUpdate();
    }

    private void updateTabs(@NonNull final StreamInfo info) {
        pageAdapter.updateItem("NEXT_VIDEOS", RelatedVideosFragment.getInstance(info));
        binding.viewPager.setVisibility(View.VISIBLE);
        pageAdapter.notifyDataSetUpdate();

        commentAdapter.updateItem("COMMENTS", CommentsFragment.getInstance(info.getServiceId(), info.getUrl(), info.getName()));
        binding.commentsViewPager.setVisibility(View.VISIBLE);
        commentAdapter.notifyDataSetUpdate();
    }
}