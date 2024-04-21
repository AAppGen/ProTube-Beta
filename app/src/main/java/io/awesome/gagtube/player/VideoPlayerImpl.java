package io.awesome.gagtube.player;

import static android.content.Context.WINDOW_SERVICE;
import static io.awesome.gagtube.player.MainPlayer.ACTION_CLOSE;
import static io.awesome.gagtube.player.MainPlayer.ACTION_FAST_FORWARD;
import static io.awesome.gagtube.player.MainPlayer.ACTION_FAST_REWIND;
import static io.awesome.gagtube.player.MainPlayer.ACTION_OPEN_CONTROLS;
import static io.awesome.gagtube.player.MainPlayer.ACTION_PLAY_NEXT;
import static io.awesome.gagtube.player.MainPlayer.ACTION_PLAY_PAUSE;
import static io.awesome.gagtube.player.MainPlayer.ACTION_PLAY_PREVIOUS;
import static io.awesome.gagtube.player.MainPlayer.ACTION_RECREATE_NOTIFICATION;
import static io.awesome.gagtube.player.MainPlayer.ACTION_REPEAT;
import static io.awesome.gagtube.player.MainPlayer.ACTION_SHUFFLE;
import static io.awesome.gagtube.player.helper.PlayerHelper.globalScreenOrientationLocked;
import static io.awesome.gagtube.util.AnimationUtils.Type.SLIDE_AND_ALPHA;
import static io.awesome.gagtube.util.AnimationUtils.animateView;
import static io.awesome.gagtube.util.ListHelper.getPopupResolutionIndex;
import static io.awesome.gagtube.util.ListHelper.getResolutionIndex;
import static io.awesome.gagtube.util.NavigationHelper.AUTO_PLAY;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.DisplayCutout;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnticipateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.annimon.stream.Stream;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.text.CaptionStyleCompat;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SubtitleView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nostra13.universalimageloader.core.assist.FailReason;

import org.jetbrains.annotations.NotNull;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.stream.StreamInfo;
import org.schabi.newpipe.extractor.stream.VideoStream;

import java.util.List;

import io.awesome.gagtube.R;
import io.awesome.gagtube.activities.MainActivity;
import io.awesome.gagtube.fragments.OnScrollBelowItemsListener;
import io.awesome.gagtube.fragments.detail.VideoDetailFragment;
import io.awesome.gagtube.player.event.PlayerEventListener;
import io.awesome.gagtube.player.event.PlayerGestureListener;
import io.awesome.gagtube.player.event.PlayerServiceEventListener;
import io.awesome.gagtube.player.helper.PlayerHelper;
import io.awesome.gagtube.player.playqueue.PlayQueue;
import io.awesome.gagtube.player.playqueue.PlayQueueItem;
import io.awesome.gagtube.player.playqueue.PlayQueueItemBuilder;
import io.awesome.gagtube.player.playqueue.PlayQueueItemHolder;
import io.awesome.gagtube.player.playqueue.PlayQueueItemTouchCallback;
import io.awesome.gagtube.player.resolver.MediaSourceTag;
import io.awesome.gagtube.player.resolver.VideoPlaybackResolver;
import io.awesome.gagtube.util.AnimationUtils;
import io.awesome.gagtube.util.Constants;
import io.awesome.gagtube.util.DeviceUtils;
import io.awesome.gagtube.util.ListHelper;
import io.awesome.gagtube.util.NavigationHelper;
import io.awesome.gagtube.util.SharedUtils;

/**
 * Unified UI for all players.
 */

public class VideoPlayerImpl extends VideoPlayer implements View.OnLayoutChangeListener {

    static final String POPUP_SAVED_WIDTH = "popup_saved_width";
    static final String POPUP_SAVED_X = "popup_saved_x";
    static final String POPUP_SAVED_Y = "popup_saved_y";
    private static final int IDLE_WINDOW_FLAGS = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
    private static final int ONGOING_PLAYBACK_WINDOW_FLAGS = IDLE_WINDOW_FLAGS | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
    private static final float MAXIMUM_OPACITY_ALLOWED_FOR_S_AND_HIGHER = 0.8f;

    private TextView titleTextView;
    private TextView resizingIndicator;
    private ImageButton fullscreenButton;
    private ImageButton screenRotationButton;
    private ImageButton playPauseButton;
    private ImageButton playPreviousButton;
    private ImageButton playNextButton;
    private ImageButton minimizeButton;

    private ConstraintLayout queueLayout;
    private ImageButton itemsListCloseButton;
    private RecyclerView itemsList;
    private ItemTouchHelper itemTouchHelper;

    private RelativeLayout playerOverlays;

    private boolean queueVisible;
    private MainPlayer.PlayerType playerType = MainPlayer.PlayerType.VIDEO;

    private ImageButton shareButton;
    private View secondaryControls;

    private boolean isFullscreen = false;
    private boolean isVerticalVideo = false;
    private boolean fragmentIsVisible = false;
    boolean shouldUpdateOnProgress;

    private final MainPlayer service;
    private PlayerServiceEventListener fragmentListener;
    private PlayerEventListener activityListener;
    private GestureDetector gestureDetector;
    private final SharedPreferences defaultPreferences;
    private ContentObserver settingsContentObserver;

    // Popup
    private WindowManager.LayoutParams popupLayoutParams;
    public WindowManager windowManager;

    private View closingOverlayView;
    private View closeOverlayView;
    private FloatingActionButton closeOverlayButton;

    public boolean isPopupClosing = false;

    private float screenWidth;
    private float screenHeight;
    private float popupWidth;
    private float popupHeight;
    private float minimumWidth;
    private float minimumHeight;
    private float maximumWidth;
    private float maximumHeight;
    // Popup end

    @Override
    public void handleIntent(final Intent intent) {
        if (intent.getStringExtra(VideoPlayer.PLAY_QUEUE_KEY) == null) {
            return;
        }

        final MainPlayer.PlayerType oldPlayerType = playerType;
        choosePlayerTypeFromIntent(intent);

        // We need to setup audioOnly before super(), see "sourceOf"
        super.handleIntent(intent);

        if (oldPlayerType != playerType && playQueue != null) {
            // If playerType changes from one to another we should reload the player
            // (to disable/enable video stream or to set quality)
            setRecovery();
            reload();
        }

        setupElementsVisibility();
        setupElementsSize();

        if (popupPlayerSelected()) {
            getRootView().setVisibility(View.VISIBLE);
            initPopup();
            initPopupCloseOverlay();
            playPauseButton.requestFocus();
        } else {
            getRootView().setVisibility(View.VISIBLE);
            initVideoPlayer();
            onQueueClosed();
            // Android TV: without it focus will frame the whole player
            playPauseButton.requestFocus();
            onPlay();
        }
        NavigationHelper.sendPlayerStartedEvent(service);
    }

    VideoPlayerImpl(final MainPlayer service) {
        super(service);
        this.service = service;
        this.shouldUpdateOnProgress = true;
        this.windowManager = (WindowManager) service.getSystemService(WINDOW_SERVICE);
        this.defaultPreferences = PreferenceManager.getDefaultSharedPreferences(service);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initViews(final View view) {
        super.initViews(view);
        this.titleTextView = view.findViewById(R.id.titleTextView);
        this.resizingIndicator = view.findViewById(R.id.resizing_indicator);
        this.fullscreenButton = view.findViewById(R.id.fullScreenButton);
        this.screenRotationButton = view.findViewById(R.id.screenRotationButton);
        this.playPauseButton = view.findViewById(R.id.playPauseButton);
        this.playPreviousButton = view.findViewById(R.id.playPreviousButton);
        this.playNextButton = view.findViewById(R.id.playNextButton);
        this.minimizeButton = view.findViewById(R.id.minimizeButton);

        this.secondaryControls = view.findViewById(R.id.secondaryControls);
        this.shareButton = view.findViewById(R.id.shareButton);

        this.queueLayout = view.findViewById(R.id.playQueuePanel);
        this.itemsListCloseButton = view.findViewById(R.id.playQueueClose);
        this.itemsList = view.findViewById(R.id.relatedRecyclerView);

        this.playerOverlays = view.findViewById(R.id.player_overlays);
        this.closingOverlayView = view.findViewById(R.id.closingOverlay);

        // Prevent hiding of bottom sheet via swipe inside queue
        this.itemsList.setNestedScrollingEnabled(false);
    }

    @Override
    protected void setupSubtitleView(final @NonNull SubtitleView view, final float captionScale, @NonNull final CaptionStyleCompat captionStyle) {
        if (popupPlayerSelected()) {
            final float captionRatio = (captionScale - 1.0f) / 5.0f + 1.0f;
            view.setFractionalTextSize(SubtitleView.DEFAULT_TEXT_SIZE_FRACTION * captionRatio);
        } else {
            final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            final int minimumLength = Math.min(metrics.heightPixels, metrics.widthPixels);
            final float captionRatioInverse = 20f + 4f * (1.0f - captionScale);
            view.setFixedTextSize(TypedValue.COMPLEX_UNIT_PX, (float) minimumLength / captionRatioInverse);
        }
        view.setApplyEmbeddedStyles(captionStyle.equals(CaptionStyleCompat.DEFAULT));
        view.setStyle(captionStyle);
    }

    /**
     * This method ensures that popup and main players have different look.
     * We use one layout for both players and need to decide what to show and what to hide.
     * Additional measuring should be done inside {@link #setupElementsSize}.
     */
    private void setupElementsVisibility() {
        if (popupPlayerSelected()) {
            fullscreenButton.setVisibility(View.VISIBLE);
            screenRotationButton.setVisibility(View.GONE);
            secondaryControls.setAlpha(1.0f);
            secondaryControls.setVisibility(View.VISIBLE);
            secondaryControls.setTranslationY(0);
            shareButton.setVisibility(View.GONE);
            getTopControlsRoot().bringToFront();
            getTopControlsRoot().setClickable(false);
            getTopControlsRoot().setFocusable(false);
            getBottomControlsRoot().bringToFront();
            //onQueueClosed();
        } else {
            fullscreenButton.setVisibility(View.GONE);
            setupScreenRotationButton();
            //secondaryControls.setVisibility(View.INVISIBLE);
            // Top controls have a large minHeight which is allows to drag the player
            // down in fullscreen mode (just larger area to make easy to locate by finger)
            getTopControlsRoot().setClickable(true);
            getTopControlsRoot().setFocusable(true);
        }
        if (!isFullscreen()) {
            titleTextView.setVisibility(View.INVISIBLE);
        } else {
            titleTextView.setVisibility(View.VISIBLE);
        }
        minimizeButton.setVisibility(popupPlayerSelected() || isFullscreen() ? View.GONE : View.VISIBLE);
    }

    /**
     * Changes padding, size of elements based on player selected right now.
     * Popup player has small padding in comparison with the main player
     */
    private void setupElementsSize() {
        if (popupPlayerSelected()) {
            final int controlsPadding = service.getResources().getDimensionPixelSize(R.dimen.player_popup_controls_padding);
            final int buttonsPadding = service.getResources().getDimensionPixelSize(R.dimen.player_popup_buttons_padding);
            getTopControlsRoot().setPaddingRelative(controlsPadding, 0, controlsPadding, 0);
            getBottomControlsRoot().setPaddingRelative(controlsPadding, 0, controlsPadding, 0);
            getQualityTextView().setPadding(buttonsPadding, buttonsPadding, buttonsPadding, buttonsPadding);
            getPlaybackSpeedTextView().setPadding(buttonsPadding, buttonsPadding, buttonsPadding, buttonsPadding);
            getCaptionTextView().setPadding(buttonsPadding, buttonsPadding, buttonsPadding, buttonsPadding);
            Stream.of(getQualityTextView(), getCaptionTextView(), getPlaybackTime(), getPlaybackLiveSync()).forEach(textView -> textView.setTextSize(12));
        } else if (videoPlayerSelected()) {
            final int controlsPadding = service.getResources().getDimensionPixelSize(R.dimen.player_main_controls_padding);
            getTopControlsRoot().setPaddingRelative(controlsPadding, controlsPadding, controlsPadding, 0);
            getBottomControlsRoot().setPaddingRelative(controlsPadding, 0, controlsPadding, 0);
            Stream.of(getQualityTextView(), getQualityTextView(), getCaptionTextView(), getPlaybackTime(), getPlaybackLiveSync()).forEach(textView -> textView.setTextSize(14));
        }
    }

    @Override
    public void initListeners() {
        super.initListeners();

        final PlayerGestureListener listener = new PlayerGestureListener(this, service);
        gestureDetector = new GestureDetector(context, listener);
        getRootView().setOnTouchListener(listener);

        playPauseButton.setOnClickListener(this);
        playPreviousButton.setOnClickListener(this);
        playNextButton.setOnClickListener(this);
        minimizeButton.setOnClickListener(this);

        shareButton.setOnClickListener(this);
        fullscreenButton.setOnClickListener(this);
        screenRotationButton.setOnClickListener(this);

        settingsContentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(final boolean selfChange) {
                setupScreenRotationButton();
            }
        };
        service.getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION), false, settingsContentObserver);
        getRootView().addOnLayoutChangeListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            queueLayout.setOnApplyWindowInsetsListener((view, windowInsets) -> {
                final DisplayCutout cutout = windowInsets.getDisplayCutout();
                if (cutout != null) {
                    view.setPadding(cutout.getSafeInsetLeft(), cutout.getSafeInsetTop(), cutout.getSafeInsetRight(), cutout.getSafeInsetBottom());
                }
                return windowInsets;
            });
        }

        // PlaybackControlRoot already consumed window insets but we should pass them to
        // player_overlays too. Without it they will be off-centered
        getControlsRoot().addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) ->
                playerOverlays.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom()));
    }

    public boolean onKeyDown(final int keyCode) {
        switch (keyCode) {
            default:
                break;
            case KeyEvent.KEYCODE_BACK:
                if (isControlsVisible()) {
                    hideControls(0, 0);
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (getRootView().hasFocus() && !getControlsRoot().hasFocus()) {
                    // do not interfere with focus in playlist etc.
                    return false;
                }

                if (getCurrentState() == BasePlayer.STATE_BLOCKED) {
                    return true;
                }

                if (!isControlsVisible()) {
                    if (!queueVisible) {
                        playPauseButton.requestFocus();
                    }
                    showControlsThenHide();
                    showSystemUIPartially();
                    return true;
                } else {
                    hideControls(DEFAULT_CONTROLS_DURATION, DPAD_CONTROLS_HIDE_TIME);
                }
                break;
        }

        return false;
    }

    public AppCompatActivity getParentActivity() {
        // ! instanceof ViewGroup means that view was added via windowManager for Popup
        if (getRootView() == null || getRootView().getParent() == null || !(getRootView().getParent() instanceof ViewGroup)) {
            return null;
        }

        final ViewGroup parent = (ViewGroup) getRootView().getParent();
        return (AppCompatActivity) parent.getContext();
    }

    // View
    private void setRepeatModeButton(final ImageButton imageButton, final int repeatMode) {
        switch (repeatMode) {
            case Player.REPEAT_MODE_OFF:
                imageButton.setImageResource(R.drawable.exo_controls_repeat_off);
                break;
            case Player.REPEAT_MODE_ONE:
                imageButton.setImageResource(R.drawable.exo_controls_repeat_one);
                break;
            case Player.REPEAT_MODE_ALL:
                imageButton.setImageResource(R.drawable.exo_controls_repeat_all);
                break;
        }
    }

    private void setShuffleButton(final ImageButton button, final boolean shuffled) {
        final int shuffleAlpha = shuffled ? 255 : 77;
        button.setImageAlpha(shuffleAlpha);
    }

    @Override
    public void onVideoSizeChanged(final int width, final int height, final int unappliedRotationDegrees, final float pixelWidthHeightRatio) {
        super.onVideoSizeChanged(width, height, unappliedRotationDegrees, pixelWidthHeightRatio);
        isVerticalVideo = width < height;
        prepareOrientation(width, height);
        setupScreenRotationButton();
    }

    // ExoPlayer Video Listener
    void onShuffleOrRepeatModeChanged() {
        updatePlayback();
        NotificationUtil.getInstance().createNotificationIfNeededAndUpdate(this, false);
    }

    @Override
    public void onRepeatModeChanged(final int i) {
        super.onRepeatModeChanged(i);
        onShuffleOrRepeatModeChanged();
    }

    @Override
    public void onShuffleClicked() {
        super.onShuffleClicked();
        onShuffleOrRepeatModeChanged();

    }

    // Playback Listener
    @Override
    public void onPlayerError(@NotNull final ExoPlaybackException error) {
        super.onPlayerError(error);
        if (fragmentListener != null) {
            fragmentListener.onPlayerError(error);
        }
    }

    @Override
    public void onTimelineChanged(@NotNull final Timeline timeline, final int reason) {
        super.onTimelineChanged(timeline, reason);
        // force recreate notification to ensure seek bar is shown when preparation finishes
        NotificationUtil.getInstance().createNotificationIfNeededAndUpdate(this, true);
    }

    protected void onMetadataChanged(@NonNull final MediaSourceTag tag) {
        super.onMetadataChanged(tag);
        titleTextView.setText(tag.getMetadata().getName());
        NotificationUtil.getInstance().createNotificationIfNeededAndUpdate(this, false);
        updateMetadata();
    }

    @Override
    public void onPlaybackShutdown() {
        service.onDestroy();
    }

    @Override
    public void onUpdateProgress(final int currentProgress, final int duration, final int bufferPercent) {
        super.onUpdateProgress(currentProgress, duration, bufferPercent);
        updateProgress(currentProgress, duration, bufferPercent);

        // setMetadata only updates the metadata when any of the metadata keys are null
        mediaSessionManager.setMetadata(getVideoTitle(), getUploaderName(), getThumbnail(), duration);
    }

    @Override
    public void onPlayQueueEdited() {
        updatePlayback();
        showOrHideButtons();
        NotificationUtil.getInstance().createNotificationIfNeededAndUpdate(this, false);
    }

    @Override
    @Nullable
    public MediaSource sourceOf(final PlayQueueItem item, final StreamInfo info) {
        // For LiveStream or video/popup players we can use super() method
        return super.sourceOf(item, info);
    }

    @Override
    public void onPlayPrevious() {
        super.onPlayPrevious();
        triggerProgressUpdate();
    }

    @Override
    public void onPlayNext() {
        super.onPlayNext();
        triggerProgressUpdate();
    }

    @Override
    protected void initPlayback(@NonNull final PlayQueue queue, final int repeatMode,
                                final float playbackSpeed, final float playbackPitch,
                                final boolean playbackSkipSilence,
                                final boolean playOnReady, final boolean isMuted) {
        super.initPlayback(queue, repeatMode, playbackSpeed, playbackPitch, playbackSkipSilence, playOnReady, isMuted);
        updateQueue();
    }

    // Player Overrides
    @Override
    public void toggleFullscreen() {
        if (popupPlayerSelected() || simpleExoPlayer == null || getCurrentMetadata() == null || fragmentListener == null) {
            return;
        }

        isFullscreen = !isFullscreen;
        if (!isFullscreen) {
            // Apply window insets because Android will not do it when orientation changes
            // from landscape to portrait (open vertical video to reproduce)
            getControlsRoot().setPadding(0, 0, 0, 0);
        } else {
            // Android needs tens milliseconds to send new insets but a user is able to see
            // how controls changes it's position from `0` to `nav bar height` padding.
            // So just hide the controls to hide this visual inconsistency
            hideControls(0, 0);
        }
        fragmentListener.onFullscreenStateChanged(isFullscreen());

        if (!isFullscreen()) {
            titleTextView.setVisibility(View.INVISIBLE);
        } else {
            titleTextView.setVisibility(View.VISIBLE);
        }
        minimizeButton.setVisibility(popupPlayerSelected() || isFullscreen() ? View.GONE : View.VISIBLE);
        setupScreenRotationButton();
    }

    public void switchFromPopupToMain() {
        if (!popupPlayerSelected() || simpleExoPlayer == null || getCurrentMetadata() == null) {
            return;
        }

        setRecovery();
        service.removeViewFromParent();
        final Intent intent = NavigationHelper.getPlayerIntent(
                service,
                MainActivity.class,
                this.getPlayQueue(),
                this.getRepeatMode(),
                this.getPlaybackSpeed(),
                this.getPlaybackPitch(),
                this.getPlaybackSkipSilence(),
                null,
                true,
                !isPlaying(),
                isMuted()
        );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.KEY_SERVICE_ID, getCurrentMetadata().getMetadata().getServiceId());
        intent.putExtra(Constants.KEY_LINK_TYPE, StreamingService.LinkType.STREAM);
        intent.putExtra(Constants.KEY_URL, getVideoUrl());
        intent.putExtra(Constants.KEY_TITLE, getVideoTitle());
        intent.putExtra(AUTO_PLAY, true);
        service.onDestroy();
        context.startActivity(intent);
    }

    @Override
    public void onClick(final View v) {
        super.onClick(v);
        if (v.getId() == playPauseButton.getId()) {
            onPlayPause();
        } else if (v.getId() == playPreviousButton.getId()) {
            onPlayPrevious();
        } else if (v.getId() == playNextButton.getId()) {
            onPlayNext();
        } else if (v.getId() == shareButton.getId()) {
            onShareClicked();
        } else if (v.getId() == fullscreenButton.getId()) {
            switchFromPopupToMain();
        } else if (v.getId() == screenRotationButton.getId()) {
            // Only if it's not a vertical video or vertical video but in landscape with locked
            // orientation a screen orientation can be changed automatically
            if (!isVerticalVideo || (service.isLandscape() && globalScreenOrientationLocked(service))) {
                fragmentListener.onScreenRotationButtonClicked();
            } else {
                toggleFullscreen();
            }
        } else if (v.getId() == minimizeButton.getId()) {
            service.sendBroadcast(new Intent(VideoDetailFragment.ACTION_MINIMIZE_MAIN_PLAYER));
        }

        if (getCurrentState() != STATE_COMPLETED) {
            getControlsVisibilityHandler().removeCallbacksAndMessages(null);
            animateView(getControlsRoot(), true, DEFAULT_CONTROLS_DURATION, 0, () -> {
                if (getCurrentState() == STATE_PLAYING && !isSomePopupMenuVisible()) {
                    if (v.getId() == playPauseButton.getId()
                            // Hide controls in fullscreen immediately
                            || (v.getId() == screenRotationButton.getId() && isFullscreen)) {
                        hideControls(0, 0);
                    } else {
                        hideControls(DEFAULT_CONTROLS_DURATION, DEFAULT_CONTROLS_HIDE_TIME);
                    }
                }
            });
        }
    }

    private void onQueueClicked() {
        queueVisible = true;

        hideSystemUIIfNeeded();
        buildQueue();

        hideControls(0, 0);
        queueLayout.requestFocus();
        animateView(queueLayout, SLIDE_AND_ALPHA, true, DEFAULT_CONTROLS_DURATION);

        itemsList.scrollToPosition(playQueue.getIndex());
    }

    public void onQueueClosed() {
        if (!queueVisible) {
            return;
        }

        animateView(queueLayout, SLIDE_AND_ALPHA, false, DEFAULT_CONTROLS_DURATION, 0, () -> {
            // Even when queueLayout is GONE it receives touch events
            // and ruins normal behavior of the app. This line fixes it
            queueLayout.setTranslationY(-queueLayout.getHeight() * 5);
        });
        queueVisible = false;
        playPauseButton.requestFocus();
    }

    private void onShareClicked() {
        // share video at the current time (youtube.com/watch?v=ID&t=SECONDS)
        // Timestamp doesn't make sense in a live stream so drop it
        //final String ts = isLive() ? "" : ("&t=" + (getPlaybackSeekBar().getProgress() / 1000));
        SharedUtils.shareUrl(service);
    }

    private void setupScreenRotationButton() {
        //final boolean orientationLocked = PlayerHelper.globalScreenOrientationLocked(service);
        //final boolean showButton = videoPlayerSelected() && (orientationLocked || isVerticalVideo);
        screenRotationButton.setVisibility(View.VISIBLE);
        screenRotationButton.setImageDrawable(ContextCompat.getDrawable(service, isFullscreen() ? R.drawable.ic_fullscreen_exit_white_24dp : R.drawable.ic_fullscreen_white_24dp));
    }

    private void prepareOrientation(int width, int height) {
        getSurfaceView().setAspectRatio(((float) width) / height);
        isVerticalVideo = width < height;
        if (globalScreenOrientationLocked(context)
                && isFullscreen
                && service.isLandscape() == isVerticalVideo
                && !DeviceUtils.isTablet(service)
                && fragmentListener != null) {
            fragmentListener.onScreenRotationButtonClicked();
        }
    }

    @Override
    public void onPlaybackSpeedClicked() {
        super.onPlaybackSpeedClicked();
    }

    @Override
    public void onStopTrackingTouch(final SeekBar seekBar) {
        super.onStopTrackingTouch(seekBar);
        if (wasPlaying()) {
            showControlsThenHide();
        }
    }

    @Override
    @SuppressWarnings("checkstyle:ParameterNumber")
    public void onLayoutChange(final View view, final int l, final int t, final int r, final int b, final int ol, final int ot, final int or, final int ob) {
        if (l != ol || t != ot || r != or || b != ob) {
            // Use smaller value to be consistent between screen orientations
            // (and to make usage easier)
            final int width = r - l;
            final int height = b - t;
            queueLayout.getLayoutParams().height = height - queueLayout.getTop();
        }
    }

    @Override
    protected int nextResizeMode(final int currentResizeMode) {
        final int newResizeMode;
        switch (currentResizeMode) {
            case AspectRatioFrameLayout.RESIZE_MODE_FIT:
                newResizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM;
                break;
            default:
                newResizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT;
                break;
        }

        storeResizeMode(newResizeMode);
        return newResizeMode;
    }

    private void storeResizeMode(final @AspectRatioFrameLayout.ResizeMode int resizeMode) {
        defaultPreferences.edit().putInt(service.getString(R.string.last_resize_mode), resizeMode).apply();
    }

    private void restoreResizeMode() {
        setResizeMode(defaultPreferences.getInt(service.getString(R.string.last_resize_mode), AspectRatioFrameLayout.RESIZE_MODE_FIT));
    }

    @Override
    protected VideoPlaybackResolver.QualityResolver getQualityResolver() {
        return new VideoPlaybackResolver.QualityResolver() {
            @Override
            public int getDefaultResolutionIndex(final List<VideoStream> sortedVideos) {
                return videoPlayerSelected() ? ListHelper.getDefaultResolutionIndex(context, sortedVideos) : ListHelper.getPopupDefaultResolutionIndex(context, sortedVideos);
            }

            @Override
            public int getOverrideResolutionIndex(final List<VideoStream> sortedVideos, final String playbackQuality) {
                return videoPlayerSelected() ? getResolutionIndex(context, sortedVideos, playbackQuality) : getPopupResolutionIndex(context, sortedVideos, playbackQuality);
            }
        };
    }

        /*//////////////////////////////////////////////////////////////////////////
        // States
        //////////////////////////////////////////////////////////////////////////*/

    private void animatePlayButtons(final boolean show, final int duration) {
        animateView(playPauseButton, AnimationUtils.Type.ALPHA, show, duration);
        if (playQueue.getIndex() > 0 || !show) {
            animateView(playPreviousButton, AnimationUtils.Type.ALPHA, show, duration);
        }
        if (playQueue.getIndex() + 1 < playQueue.getStreams().size() || !show) {
            animateView(playNextButton, AnimationUtils.Type.ALPHA, show, duration);
        }
    }

    @Override
    public void changeState(final int state) {
        super.changeState(state);
        updatePlayback();
    }

    @Override
    public void onBlocked() {
        super.onBlocked();
        playPauseButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        animatePlayButtons(false, 100);
        getRootView().setKeepScreenOn(false);

        NotificationUtil.getInstance().createNotificationIfNeededAndUpdate(this, false);
    }

    @Override
    public void onBuffering() {
        super.onBuffering();
        getRootView().setKeepScreenOn(true);

        if (NotificationUtil.getInstance().shouldUpdateBufferingSlot()) {
            NotificationUtil.getInstance().createNotificationIfNeededAndUpdate(this, false);
        }
    }

    @Override
    public void onPlaying() {
        super.onPlaying();
        animateView(playPauseButton, AnimationUtils.Type.ALPHA, false, 80, 0, () -> {
            playPauseButton.setImageResource(R.drawable.ic_pause_white_24dp);
            animatePlayButtons(true, 200);
            if (!queueVisible) {
                playPauseButton.requestFocus();
            }
        });

        updateWindowFlags(ONGOING_PLAYBACK_WINDOW_FLAGS);
        checkLandscape();
        getRootView().setKeepScreenOn(true);

        NotificationUtil.getInstance().createNotificationIfNeededAndUpdate(this, false);
    }

    @Override
    public void onPaused() {
        super.onPaused();
        animateView(playPauseButton, AnimationUtils.Type.ALPHA, false, 80, 0, () -> {
            playPauseButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
            animatePlayButtons(true, 200);
            if (!queueVisible) {
                playPauseButton.requestFocus();
            }
        });

        updateWindowFlags(IDLE_WINDOW_FLAGS);

        // Remove running notification when user don't want music (or video in popup)
        if (!minimizeOnPopupEnabled() && videoPlayerSelected()) {
            NotificationUtil.getInstance().cancelNotificationAndStopForeground(service);
        } else {
            NotificationUtil.getInstance().createNotificationIfNeededAndUpdate(this, false);
        }

        getRootView().setKeepScreenOn(false);
    }

    @Override
    public void onPausedSeek() {
        super.onPausedSeek();
        animatePlayButtons(false, 100);
        getRootView().setKeepScreenOn(true);

        NotificationUtil.getInstance().createNotificationIfNeededAndUpdate(this, false);
    }

    @Override
    public void onCompleted() {
        animateView(playPauseButton, AnimationUtils.Type.SCALE_AND_ALPHA, false, 0, 0, () -> {
            playPauseButton.setImageResource(R.drawable.ic_replay_white_24dp);
            animatePlayButtons(true, DEFAULT_CONTROLS_DURATION);
        });

        getRootView().setKeepScreenOn(false);
        updateWindowFlags(IDLE_WINDOW_FLAGS);

        NotificationUtil.getInstance().createNotificationIfNeededAndUpdate(this, false);
        if (isFullscreen) {
            toggleFullscreen();
        }
        super.onCompleted();
    }

    @Override
    public void destroy() {
        super.destroy();
        service.getContentResolver().unregisterContentObserver(settingsContentObserver);
    }

    // Broadcast Receiver
    @Override
    protected void setupBroadcastReceiver(final IntentFilter intentFilter) {
        super.setupBroadcastReceiver(intentFilter);
        intentFilter.addAction(ACTION_CLOSE);
        intentFilter.addAction(ACTION_PLAY_PAUSE);
        intentFilter.addAction(ACTION_OPEN_CONTROLS);
        intentFilter.addAction(ACTION_REPEAT);
        intentFilter.addAction(ACTION_PLAY_PREVIOUS);
        intentFilter.addAction(ACTION_PLAY_NEXT);
        intentFilter.addAction(ACTION_FAST_REWIND);
        intentFilter.addAction(ACTION_FAST_FORWARD);
        intentFilter.addAction(ACTION_SHUFFLE);
        intentFilter.addAction(ACTION_RECREATE_NOTIFICATION);

        intentFilter.addAction(VideoDetailFragment.ACTION_VIDEO_FRAGMENT_RESUMED);
        intentFilter.addAction(VideoDetailFragment.ACTION_VIDEO_FRAGMENT_STOPPED);

        intentFilter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);

        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
    }

    @Override
    public void onBroadcastReceived(final Intent intent) {
        super.onBroadcastReceived(intent);
        if (intent.getAction() == null) {
            return;
        }

        switch (intent.getAction()) {
            case ACTION_CLOSE:
                service.onDestroy();
                break;
            case ACTION_PLAY_NEXT:
                onPlayNext();
                break;
            case ACTION_PLAY_PREVIOUS:
                onPlayPrevious();
                break;
            case ACTION_FAST_FORWARD:
                onFastForward();
                break;
            case ACTION_FAST_REWIND:
                onFastRewind();
                break;
            case ACTION_PLAY_PAUSE:
                onPlayPause();
                if (!fragmentIsVisible) {
                    // Ensure that we have audio-only stream playing when a user
                    // started to play from notification's play button from outside of the app
                    onFragmentStopped();
                }
                break;
            case ACTION_REPEAT:
                onRepeatClicked();
                break;
            case ACTION_SHUFFLE:
                onShuffleClicked();
                break;
            case ACTION_RECREATE_NOTIFICATION:
                NotificationUtil.getInstance().createNotificationIfNeededAndUpdate(this, true);
                break;
            case Intent.ACTION_HEADSET_PLUG: //FIXME
                /*notificationManager.cancel(NOTIFICATION_ID);
                mediaSessionManager.dispose();
                mediaSessionManager.enable(getBaseContext(), basePlayerImpl.simpleExoPlayer);*/
                break;
            case VideoDetailFragment.ACTION_VIDEO_FRAGMENT_RESUMED:
                fragmentIsVisible = true;
                useVideoSource(true);
                break;
            case VideoDetailFragment.ACTION_VIDEO_FRAGMENT_STOPPED:
                fragmentIsVisible = false;
                onFragmentStopped();
                break;
            case Intent.ACTION_CONFIGURATION_CHANGED:
                if (popupPlayerSelected()) {
                    updateScreenSize();
                    updatePopupSize(getPopupLayoutParams().width, -1);
                    checkPopupPositionBounds();
                }
                // Close it because when changing orientation from portrait
                // (in fullscreen mode) the size of queue layout can be larger than the screen size
                onQueueClosed();
                break;
            case Intent.ACTION_SCREEN_ON:
                shouldUpdateOnProgress = true;
                // Interrupt playback only when screen turns on
                // and user is watching video in popup player.
                // Same actions for video player will be handled in ACTION_VIDEO_FRAGMENT_RESUMED
                if (popupPlayerSelected() && (isPlaying() || isLoading())) {
                    useVideoSource(true);
                }
                break;
            case Intent.ACTION_SCREEN_OFF:
                shouldUpdateOnProgress = false;
                // Interrupt playback only when screen turns off with popup player working
                if (popupPlayerSelected() && (isPlaying() || isLoading())) {
                    useVideoSource(false);
                }
                break;
        }
    }

    // Thumbnail Loading
    @Override
    public void onLoadingComplete(final String imageUri, final View view, final Bitmap loadedImage) {
        super.onLoadingComplete(imageUri, view, loadedImage);
        NotificationUtil.getInstance().createNotificationIfNeededAndUpdate(this, false);
    }

    @Override
    public void onLoadingFailed(final String imageUri, final View view, final FailReason failReason) {
        super.onLoadingFailed(imageUri, view, failReason);
        NotificationUtil.getInstance().createNotificationIfNeededAndUpdate(this, false);
    }

    @Override
    public void onLoadingCancelled(final String imageUri, final View view) {
        super.onLoadingCancelled(imageUri, view);
        NotificationUtil.getInstance().createNotificationIfNeededAndUpdate(this, false);
    }

    // Utils
    private void choosePlayerTypeFromIntent(final Intent intent) {
        // If you want to open popup from the app just include Constants.POPUP_ONLY into an extra
        if (intent.getIntExtra(PLAYER_TYPE, PLAYER_TYPE_VIDEO) == PLAYER_TYPE_POPUP) {
            playerType = MainPlayer.PlayerType.POPUP;
        } else {
            playerType = MainPlayer.PlayerType.VIDEO;
        }
    }

    public boolean minimizeOnPopupEnabled() {
        return PlayerHelper.getMinimizeOnExitAction(service) == PlayerHelper.MinimizeMode.MINIMIZE_ON_EXIT_MODE_POPUP;
    }

    public boolean videoPlayerSelected() {
        return playerType == MainPlayer.PlayerType.VIDEO;
    }

    public boolean popupPlayerSelected() {
        return playerType == MainPlayer.PlayerType.POPUP;
    }

    public boolean isPlayerStopped() {
        return getPlayer() == null || getPlayer().getPlaybackState() == SimpleExoPlayer.STATE_IDLE;
    }

    private int distanceFromCloseButton(final MotionEvent popupMotionEvent) {
        final int closeOverlayButtonX = closeOverlayButton.getLeft() + closeOverlayButton.getWidth() / 2;
        final int closeOverlayButtonY = closeOverlayButton.getTop() + closeOverlayButton.getHeight() / 2;

        final float fingerX = popupLayoutParams.x + popupMotionEvent.getX();
        final float fingerY = popupLayoutParams.y + popupMotionEvent.getY();

        return (int) Math.sqrt(Math.pow(closeOverlayButtonX - fingerX, 2) + Math.pow(closeOverlayButtonY - fingerY, 2));
    }

    private float getClosingRadius() {
        final int buttonRadius = closeOverlayButton.getWidth() / 2;
        // 20% wider than the button itself
        return buttonRadius * 1.2f;
    }

    public boolean isInsideClosingRadius(final MotionEvent popupMotionEvent) {
        return distanceFromCloseButton(popupMotionEvent) <= getClosingRadius();
    }

    public boolean isFullscreen() {
        return isFullscreen;
    }

    public void showControlsThenHide() {
        showOrHideButtons();
        showSystemUIPartially();
        super.showControlsThenHide();
    }

    @Override
    public void showControls(final long duration) {
        showOrHideButtons();
        showSystemUIPartially();
        super.showControls(duration);
    }

    @Override
    public void hideControls(final long duration, final long delay) {
        showOrHideButtons();
        getControlsVisibilityHandler().removeCallbacksAndMessages(null);
        getControlsVisibilityHandler().postDelayed(() -> animateView(getControlsRoot(), false, duration, 0, this::hideSystemUIIfNeeded), delay);
    }

    @Override
    public void safeHideControls(final long duration, final long delay) {
        if (getControlsRoot().isInTouchMode()) {
            hideControls(duration, delay);
        }
    }

    private void showOrHideButtons() {
        if (playQueue == null) {
            return;
        }

        final boolean showPrev = playQueue.getIndex() != 0;
        final boolean showNext = playQueue.getIndex() + 1 != playQueue.getStreams().size();
        final boolean showQueue = playQueue.getStreams().size() > 1 && !popupPlayerSelected();

        playPreviousButton.setVisibility(showPrev ? View.VISIBLE : View.INVISIBLE);
        playPreviousButton.setAlpha(showPrev ? 1.0f : 0.0f);
        playNextButton.setVisibility(showNext ? View.VISIBLE : View.INVISIBLE);
        playNextButton.setAlpha(showNext ? 1.0f : 0.0f);
    }

    private void showSystemUIPartially() {
        final AppCompatActivity activity = getParentActivity();
        if (isFullscreen() && activity != null) {
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
            final int visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            activity.getWindow().getDecorView().setSystemUiVisibility(visibility);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public void hideSystemUIIfNeeded() {
        if (fragmentListener != null) {
            fragmentListener.hideSystemUiIfNeeded();
        }
    }

    public void disablePreloadingOfCurrentTrack() {
        getLoadController().disablePreloadingOfCurrentTrack();
    }

    /**
     * @return true if main player is attached to activity and activity inside multiWindow mode
     */
    private boolean isInMultiWindow() {
        final AppCompatActivity parent = getParentActivity();
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && parent != null && parent.isInMultiWindowMode();
    }

    public void checkLandscape() {
        final AppCompatActivity parent = getParentActivity();
        final boolean videoInLandscapeButNotInFullscreen = service.isLandscape() && !isFullscreen() && videoPlayerSelected();

        final boolean playingState = getCurrentState() != STATE_COMPLETED && getCurrentState() != STATE_PAUSED;
        if (parent != null && videoInLandscapeButNotInFullscreen && playingState && !DeviceUtils.isTablet(service)) {
            toggleFullscreen();
        }
    }

    private void buildQueue() {
        itemsList.setAdapter(playQueueAdapter);
        itemsList.setClickable(true);
        itemsList.setLongClickable(true);

        itemsList.clearOnScrollListeners();
        itemsList.addOnScrollListener(getQueueScrollListener());

        itemTouchHelper = new ItemTouchHelper(getItemTouchCallback());
        itemTouchHelper.attachToRecyclerView(itemsList);

        playQueueAdapter.setSelectedListener(getOnSelectedListener());

        itemsListCloseButton.setOnClickListener(view -> onQueueClosed());
    }

    public void useVideoSource(final boolean video) {
        if (!isControlsVisible()) {
            hideSystemUIIfNeeded();
        }
        setRecovery();
        reload();
    }

    private OnScrollBelowItemsListener getQueueScrollListener() {
        return new OnScrollBelowItemsListener() {
            @Override
            public void onScrolledDown(final RecyclerView recyclerView) {
                if (playQueue != null && !playQueue.isComplete()) {
                    playQueue.fetch();
                } else if (itemsList != null) {
                    itemsList.clearOnScrollListeners();
                }
            }
        };
    }

    private ItemTouchHelper.SimpleCallback getItemTouchCallback() {

        return new PlayQueueItemTouchCallback() {

            @Override
            public void onMove(final int sourceIndex, final int targetIndex) {
                if (playQueue != null) {
                    playQueue.move(sourceIndex, targetIndex);
                }
            }

            @Override
            public void onSwiped(final int index) {
                if (index != -1) {
                    playQueue.remove(index);
                }
            }
        };
    }

    private PlayQueueItemBuilder.OnSelectedListener getOnSelectedListener() {
        return new PlayQueueItemBuilder.OnSelectedListener() {
            @Override
            public void selected(final PlayQueueItem item, final View view) {
                onSelected(item);
            }

            @Override
            public void held(final PlayQueueItem item, final View view) {
                final int index = playQueue.indexOf(item);
                if (index != -1) {
                    playQueue.remove(index);
                }
            }

            @Override
            public void onStartDrag(final PlayQueueItemHolder viewHolder) {
                if (itemTouchHelper != null) {
                    itemTouchHelper.startDrag(viewHolder);
                }
            }
        };
    }

    // Init
    @SuppressLint("RtlHardcoded")
    private void initPopup() {
        // Popup is already added to windowManager
        if (popupHasParent()) {
            return;
        }

        updateScreenSize();

        final boolean popupRememberSizeAndPos = PlayerHelper.isRememberingPopupDimensions(service);
        final float defaultSize = service.getResources().getDimension(R.dimen.popup_default_width);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(service);
        popupWidth = popupRememberSizeAndPos ? sharedPreferences.getFloat(POPUP_SAVED_WIDTH, defaultSize) : defaultSize;
        popupHeight = getMinimumVideoHeight(popupWidth);

        popupLayoutParams = new WindowManager.LayoutParams((int) popupWidth, (int) popupHeight, popupLayoutParamType(), IDLE_WINDOW_FLAGS, PixelFormat.TRANSLUCENT);
        popupLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        popupLayoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        getSurfaceView().setHeights((int) popupHeight, (int) popupHeight);

        final int centerX = (int) (screenWidth / 2f - popupWidth / 2f);
        final int centerY = (int) (screenHeight / 2f - popupHeight / 2f);
        popupLayoutParams.x = popupRememberSizeAndPos ? sharedPreferences.getInt(POPUP_SAVED_X, centerX) : centerX;
        popupLayoutParams.y = popupRememberSizeAndPos ? sharedPreferences.getInt(POPUP_SAVED_Y, centerY) : centerY;

        checkPopupPositionBounds();

        getLoadingPanel().setMinimumWidth(popupLayoutParams.width);
        getLoadingPanel().setMinimumHeight(popupLayoutParams.height);

        service.removeViewFromParent();
        windowManager.addView(getRootView(), popupLayoutParams);

        // Popup doesn't have aspectRatio selector, using FIT automatically
        setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
    }

    @SuppressLint("RtlHardcoded")
    private void initPopupCloseOverlay() {
        // closeOverlayView is already added to windowManager
        if (closeOverlayView != null) {
            return;
        }

        closeOverlayView = View.inflate(service, R.layout.player_popup_close_overlay, null);
        closeOverlayButton = closeOverlayView.findViewById(R.id.closeButton);

        final int flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;

        final WindowManager.LayoutParams closeOverlayLayoutParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, popupLayoutParamType(), flags, PixelFormat.TRANSLUCENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Setting maximum opacity allowed for touch events to other apps for Android 12 and
            // higher to prevent non interaction when using other apps with the popup player
            closeOverlayLayoutParams.alpha = MAXIMUM_OPACITY_ALLOWED_FOR_S_AND_HIGHER;
        }

        closeOverlayLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        closeOverlayLayoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;

        closeOverlayButton.setVisibility(View.GONE);
        windowManager.addView(closeOverlayView, closeOverlayLayoutParams);
    }

    private void initVideoPlayer() {
        restoreResizeMode();
        getRootView().setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
    }

    // Popup utils

    /**
     * @return if the popup was out of bounds and have been moved back to it
     * @see #checkPopupPositionBounds(float, float)
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean checkPopupPositionBounds() {
        return checkPopupPositionBounds(screenWidth, screenHeight);
    }

    /**
     * Check if {@link #popupLayoutParams}' position is within a arbitrary boundary
     * that goes from (0, 0) to (boundaryWidth, boundaryHeight).
     * <p>
     * If it's out of these boundaries, {@link #popupLayoutParams}' position is changed
     * and {@code true} is returned to represent this change.
     * </p>
     *
     * @param boundaryWidth  width of the boundary
     * @param boundaryHeight height of the boundary
     * @return if the popup was out of bounds and have been moved back to it
     */
    public boolean checkPopupPositionBounds(final float boundaryWidth, final float boundaryHeight) {
        if (popupLayoutParams.x < 0) {
            popupLayoutParams.x = 0;
            return true;
        } else if (popupLayoutParams.x > boundaryWidth - popupLayoutParams.width) {
            popupLayoutParams.x = (int) (boundaryWidth - popupLayoutParams.width);
            return true;
        }

        if (popupLayoutParams.y < 0) {
            popupLayoutParams.y = 0;
            return true;
        } else if (popupLayoutParams.y > boundaryHeight - popupLayoutParams.height) {
            popupLayoutParams.y = (int) (boundaryHeight - popupLayoutParams.height);
            return true;
        }

        return false;
    }

    public void savePositionAndSize() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(service);
        sharedPreferences.edit().putInt(POPUP_SAVED_X, popupLayoutParams.x).apply();
        sharedPreferences.edit().putInt(POPUP_SAVED_Y, popupLayoutParams.y).apply();
        sharedPreferences.edit().putFloat(POPUP_SAVED_WIDTH, popupLayoutParams.width).apply();
    }

    private float getMinimumVideoHeight(final float width) {
        return width / (16.0f / 9.0f);
    }

    public void updateScreenSize() {
        final DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);

        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        popupWidth = service.getResources().getDimension(R.dimen.popup_default_width);
        popupHeight = getMinimumVideoHeight(popupWidth);

        minimumWidth = service.getResources().getDimension(R.dimen.popup_minimum_width);
        minimumHeight = getMinimumVideoHeight(minimumWidth);

        maximumWidth = screenWidth;
        maximumHeight = screenHeight;
    }

    public void updatePopupSize(final int width, final int height) {
        if (popupLayoutParams == null || windowManager == null || getParentActivity() != null || getRootView().getParent() == null) {
            return;
        }

        final int actualWidth = (int) (width > maximumWidth ? maximumWidth : width < minimumWidth ? minimumWidth : width);
        final int actualHeight;
        if (height == -1) {
            actualHeight = (int) getMinimumVideoHeight(width);
        } else {
            actualHeight = (int) (height > maximumHeight ? maximumHeight : height < minimumHeight ? minimumHeight : height);
        }

        popupLayoutParams.width = actualWidth;
        popupLayoutParams.height = actualHeight;
        popupWidth = actualWidth;
        popupHeight = actualHeight;
        getSurfaceView().setHeights((int) popupHeight, (int) popupHeight);
        windowManager.updateViewLayout(getRootView(), popupLayoutParams);
    }

    private void updateWindowFlags(final int flags) {
        if (popupLayoutParams == null || windowManager == null || getParentActivity() != null || getRootView().getParent() == null) {
            return;
        }
        popupLayoutParams.flags = flags;
        windowManager.updateViewLayout(getRootView(), popupLayoutParams);
    }

    private int popupLayoutParamType() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_PHONE : WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
    }

    // Misc
    public void closePopup() {
        if (isPopupClosing) {
            return;
        }
        isPopupClosing = true;

        savePlaybackState();
        windowManager.removeView(getRootView());

        animateOverlayAndFinishService();
    }

    public void removePopupFromView() {
        final boolean isCloseOverlayHasParent = closeOverlayView != null && closeOverlayView.getParent() != null;
        if (popupHasParent()) {
            windowManager.removeView(getRootView());
        }
        if (isCloseOverlayHasParent) {
            windowManager.removeView(closeOverlayView);
        }
    }

    private void animateOverlayAndFinishService() {
        final int targetTranslationY = (int) (closeOverlayButton.getRootView().getHeight() - closeOverlayButton.getY());

        closeOverlayButton.animate().setListener(null).cancel();
        closeOverlayButton.animate()
                .setInterpolator(new AnticipateInterpolator())
                .translationY(targetTranslationY)
                .setDuration(400)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(final Animator animation) {
                        end();
                    }

                    @Override
                    public void onAnimationEnd(final Animator animation) {
                        end();
                    }

                    private void end() {
                        windowManager.removeView(closeOverlayView);
                        closeOverlayView = null;

                        service.onDestroy();
                    }
                }).start();
    }

    private boolean popupHasParent() {
        final View root = getRootView();
        return root != null && root.getLayoutParams() instanceof WindowManager.LayoutParams && root.getParent() != null;
    }

    // Manipulations with listener
    public void setFragmentListener(final PlayerServiceEventListener listener) {
        fragmentListener = listener;
        fragmentIsVisible = true;
        // Apply window insets because Android will not do it when orientation changes
        // from landscape to portrait
        if (!isFullscreen) {
            getControlsRoot().setPadding(0, 0, 0, 0);
        }
        queueLayout.setPadding(0, 0, 0, 0);
        updateQueue();
        updateMetadata();
        updatePlayback();
        triggerProgressUpdate();
    }

    public void removeFragmentListener(final PlayerServiceEventListener listener) {
        if (fragmentListener == listener) {
            fragmentListener = null;
        }
    }

    void setActivityListener(final PlayerEventListener listener) {
        activityListener = listener;
        updateMetadata();
        updatePlayback();
        triggerProgressUpdate();
    }

    void removeActivityListener(final PlayerEventListener listener) {
        if (activityListener == listener) {
            activityListener = null;
        }
    }

    private void updateQueue() {
        if (fragmentListener != null && playQueue != null) {
            fragmentListener.onQueueUpdate(playQueue);
        }
        if (activityListener != null && playQueue != null) {
            activityListener.onQueueUpdate(playQueue);
        }
    }

    private void updateMetadata() {
        if (fragmentListener != null && getCurrentMetadata() != null) {
            fragmentListener.onMetadataUpdate(getCurrentMetadata().getMetadata(), playQueue);
        }
        if (activityListener != null && getCurrentMetadata() != null) {
            activityListener.onMetadataUpdate(getCurrentMetadata().getMetadata(), playQueue);
        }
    }

    private void updatePlayback() {
        if (fragmentListener != null && simpleExoPlayer != null && playQueue != null) {
            fragmentListener.onPlaybackUpdate(currentState, getRepeatMode(), playQueue.isShuffled(), simpleExoPlayer.getPlaybackParameters());
        }
        if (activityListener != null && simpleExoPlayer != null && playQueue != null) {
            activityListener.onPlaybackUpdate(currentState, getRepeatMode(), playQueue.isShuffled(), getPlaybackParameters());
        }
    }

    private void updateProgress(final int currentProgress, final int duration, final int bufferPercent) {
        if (fragmentListener != null) {
            fragmentListener.onProgressUpdate(currentProgress, duration, bufferPercent);
        }
        if (activityListener != null) {
            activityListener.onProgressUpdate(currentProgress, duration, bufferPercent);
        }
    }

    void stopActivityBinding() {
        if (fragmentListener != null) {
            fragmentListener.onServiceStopped();
            fragmentListener = null;
        }
        if (activityListener != null) {
            activityListener.onServiceStopped();
            activityListener = null;
        }
    }

    /**
     * This will be called when a user goes to another app/activity, turns off a screen.
     * We don't want to interrupt playback and don't want to see notification so
     * next lines of code will enable audio-only playback only if needed
     */
    private void onFragmentStopped() {
        if (videoPlayerSelected() && (isPlaying() || isLoading())) {
            if (minimizeOnPopupEnabled()) {
                setRecovery();
                //NavigationHelper.playOnPopupPlayer(getParentActivity(), playQueue, true);
            } else {
                onPause();
            }
        }
    }

    // Getters
    public ImageButton getPlayPauseButton() {
        return playPauseButton;
    }

    public TextView getResizingIndicator() {
        return resizingIndicator;
    }

    public GestureDetector getGestureDetector() {
        return gestureDetector;
    }

    public WindowManager.LayoutParams getPopupLayoutParams() {
        return popupLayoutParams;
    }

    public float getScreenWidth() {
        return screenWidth;
    }

    public float getScreenHeight() {
        return screenHeight;
    }

    public float getPopupWidth() {
        return popupWidth;
    }

    public float getPopupHeight() {
        return popupHeight;
    }

    public void setPopupWidth(final float width) {
        popupWidth = width;
    }

    public void setPopupHeight(final float height) {
        popupHeight = height;
    }

    public View getCloseOverlayButton() {
        return closeOverlayButton;
    }

    public View getClosingOverlayView() {
        return closingOverlayView;
    }

    public boolean isVerticalVideo() {
        return isVerticalVideo;
    }
}
