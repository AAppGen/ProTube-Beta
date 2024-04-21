package io.awesome.gagtube.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;

import io.awesome.gagtube.R;
import io.awesome.gagtube.activities.MainActivity;
import io.awesome.gagtube.download.ui.DownloadActivity;
import io.awesome.gagtube.fragments.detail.VideoDetailFragment;
import io.awesome.gagtube.fragments.discover.DiscoverFragment;
import io.awesome.gagtube.fragments.list.channel.ChannelFragment;
import io.awesome.gagtube.fragments.list.main.TrendingFragment;
import io.awesome.gagtube.fragments.list.playlist.PlaylistFragment;
import io.awesome.gagtube.fragments.list.search.SearchFragment;
import io.awesome.gagtube.library.LibraryFragment;
import io.awesome.gagtube.local.feed.FeedFragment;
import io.awesome.gagtube.local.history.HistoryFragment;
import io.awesome.gagtube.local.playlist.LocalPlaylistFragment;
import io.awesome.gagtube.local.subscription.SubscriptionFragment;
import io.awesome.gagtube.player.BasePlayer;
import io.awesome.gagtube.player.MainPlayer;
import io.awesome.gagtube.player.PopupPlayerActivity;
import io.awesome.gagtube.player.VideoPlayer;
import io.awesome.gagtube.player.playqueue.PlayQueue;
import io.awesome.gagtube.player.playqueue.PlayQueueItem;
import io.awesome.gagtube.settings.SettingsActivity;

public class NavigationHelper {

    public static final String MAIN_FRAGMENT_TAG = "main_fragment_tag";
    public static final String SEARCH_FRAGMENT_TAG = "search_fragment_tag";
    public static final String AUTO_PLAY = "auto_play";

    // Players
    @NonNull
    public static <T> Intent getPlayerIntent(@NonNull final Context context,
                                             @NonNull final Class<T> targetClazz,
                                             @Nullable final PlayQueue playQueue,
                                             @Nullable final String quality,
                                             final boolean resumePlayback) {

        Intent intent = new Intent(context, targetClazz);

        if (playQueue != null) {
            final String cacheKey = SerializedCache.getInstance().put(playQueue, PlayQueue.class);
            if (cacheKey != null) {
                intent.putExtra(VideoPlayer.PLAY_QUEUE_KEY, cacheKey);
            }
        }

        if (quality != null) {
            intent.putExtra(VideoPlayer.PLAYBACK_QUALITY, quality);
        }
        intent.putExtra(VideoPlayer.RESUME_PLAYBACK, resumePlayback);
        intent.putExtra(VideoPlayer.PLAYER_TYPE, VideoPlayer.PLAYER_TYPE_VIDEO);
        intent.putExtra(VideoPlayer.START_PAUSED, false);

        return intent;
    }

    @NonNull
    public static <T> Intent getPlayerIntent(@NonNull final Context context,
                                             @NonNull final Class<T> targetClazz,
                                             @Nullable final PlayQueue playQueue,
                                             final boolean resumePlayback) {
        return getPlayerIntent(context, targetClazz, playQueue, null, resumePlayback);
    }

    @NonNull
    public static <T> Intent getPlayerEnqueueIntent(@NonNull final Context context,
                                                    @NonNull final Class<T> targetClazz,
                                                    @NonNull final PlayQueue playQueue,
                                                    final boolean selectOnAppend,
                                                    final boolean resumePlayback) {

        return getPlayerIntent(context, targetClazz, playQueue, resumePlayback)
                .putExtra(BasePlayer.APPEND_ONLY, true)
                .putExtra(BasePlayer.SELECT_ON_APPEND, selectOnAppend);
    }

    @NonNull
    public static <T> Intent getPlayerIntent(@NonNull final Context context,
                                             @NonNull final Class<T> targetClazz,
                                             @Nullable final PlayQueue playQueue,
                                             final int repeatMode,
                                             final float playbackSpeed,
                                             final float playbackPitch,
                                             final boolean playbackSkipSilence,
                                             @Nullable final String playbackQuality,
                                             final boolean resumePlayback,
                                             final boolean startPaused,
                                             final boolean isMuted) {
        return getPlayerIntent(context, targetClazz, playQueue, playbackQuality, resumePlayback)
                .putExtra(BasePlayer.REPEAT_MODE, repeatMode)
                .putExtra(BasePlayer.START_PAUSED, startPaused)
                .putExtra(BasePlayer.IS_MUTED, isMuted);
    }

    public static void playOnMainPlayer(final AppCompatActivity activity, final PlayQueue queue, final boolean autoPlay) {
        playOnMainPlayer(activity.getSupportFragmentManager(), queue, autoPlay);
    }

    public static void playOnMainPlayer(final FragmentManager fragmentManager, final PlayQueue queue, final boolean autoPlay) {
        final PlayQueueItem currentStream = queue.getItem();
        if (currentStream != null) {
            openVideoDetailFragment(fragmentManager, currentStream.getServiceId(), currentStream.getUrl(), currentStream.getTitle(), autoPlay, queue);
        }
    }

    public static void playOnMainPlayer(@NonNull final Context context,
                                        @Nullable final PlayQueue queue,
                                        @NonNull final StreamingService.LinkType linkType,
                                        @NonNull final String url,
                                        @NonNull final String title,
                                        final boolean autoPlay,
                                        final boolean resumePlayback) {

        final Intent intent = getPlayerIntent(context, MainActivity.class, queue, resumePlayback);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.KEY_LINK_TYPE, linkType);
        intent.putExtra(Constants.KEY_URL, url);
        intent.putExtra(Constants.KEY_TITLE, title);
        intent.putExtra(AUTO_PLAY, autoPlay);
        context.startActivity(intent);
    }

    public static void playOnPopupPlayer(final Context context, final PlayQueue queue, final boolean resumePlayback) {
        if (!PermissionHelper.isPopupEnabled(context)) {
            PermissionHelper.showPopupEnableToast(context);
            return;
        }

        Toast.makeText(context, R.string.popup_playing_toast, Toast.LENGTH_SHORT).show();
        final Intent intent = getPlayerIntent(context, MainPlayer.class, queue, resumePlayback);
        intent.putExtra(VideoPlayer.PLAYER_TYPE, VideoPlayer.PLAYER_TYPE_POPUP);
        startService(context, intent);
    }

    public static void enqueueOnPopupPlayer(final Context context, final PlayQueue queue, final boolean resumePlayback) {
        enqueueOnPopupPlayer(context, queue, false, resumePlayback);
    }

    public static void enqueueOnPopupPlayer(final Context context, final PlayQueue queue, boolean selectOnAppend, final boolean resumePlayback) {

        if (!PermissionHelper.isPopupEnabled(context)) {
            PermissionHelper.showPopupEnableToast(context);
            return;
        }

        Toast.makeText(context, R.string.popup_playing_append, Toast.LENGTH_SHORT).show();
        final Intent intent = getPlayerEnqueueIntent(context, MainPlayer.class, queue, selectOnAppend, resumePlayback);
        intent.putExtra(VideoPlayer.PLAYER_TYPE, VideoPlayer.PLAYER_TYPE_POPUP);
        startService(context, intent);
    }

    public static void startService(@NonNull final Context context, @NonNull final Intent intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    @SuppressLint("CommitTransaction")
    private static FragmentTransaction defaultTransaction(FragmentManager fragmentManager) {

        return fragmentManager.beginTransaction().setCustomAnimations(R.animator.custom_fade_in, R.animator.custom_fade_out, R.animator.custom_fade_in, R.animator.custom_fade_out);
    }

    public static void gotoMainFragment(FragmentManager fragmentManager) {

        ImageLoader.getInstance().clearMemoryCache();

        boolean popped = fragmentManager.popBackStackImmediate(MAIN_FRAGMENT_TAG, 0);
        if (!popped)
            openMainFragment(fragmentManager);
    }

    public static void openMainFragment(FragmentManager fragmentManager) {

        InfoCache.getInstance().trimCache();
        InfoCache.getInstance().clearCache();

        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        TrendingFragment trendingFragment = TrendingFragment.getInstance(Constants.YOUTUBE_SERVICE_ID, "Trending");
        trendingFragment.useAsFrontPage(true);

        defaultTransaction(fragmentManager)
                .replace(R.id.fragment_holder, trendingFragment)
                .addToBackStack(MAIN_FRAGMENT_TAG)
                .commit();
    }

    public static boolean hasSearchFragmentInBackstack(FragmentManager fragmentManager) {

        return fragmentManager.popBackStackImmediate(SEARCH_FRAGMENT_TAG, 0);
    }

    public static void openSearchFragment(FragmentManager fragmentManager, int serviceId, String searchString) {

        defaultTransaction(fragmentManager)
                .replace(R.id.fragment_holder, SearchFragment.getInstance(serviceId, searchString))
                .addToBackStack(SEARCH_FRAGMENT_TAG)
                .commit();
    }

    public static void openSearch(final Context context, final int serviceId, final String searchString) {
        final Intent mIntent = new Intent(context, MainActivity.class);
        mIntent.putExtra(Constants.KEY_SERVICE_ID, serviceId);
        mIntent.putExtra(Constants.KEY_SEARCH_STRING, searchString);
        mIntent.putExtra(Constants.KEY_OPEN_SEARCH, true);
        context.startActivity(mIntent);
    }

    public static void openVideoDetailFragment(FragmentManager fragmentManager, int serviceId, String url, String title) {
        openVideoDetailFragment(fragmentManager, serviceId, url, title, true, null);
    }

    public static void openVideoDetailFragment(FragmentManager fragmentManager, int serviceId, String url, String title, final boolean autoPlay, final PlayQueue playQueue) {

        final RunnableWithVideoDetailFragment onVideoDetailFragmentReady = detailFragment -> {
            expandMainPlayer(detailFragment.requireActivity());
            detailFragment.setAutoplay(autoPlay);
            detailFragment.selectAndLoadVideo(serviceId, url, title == null ? "" : title, playQueue);
            detailFragment.scrollToTop();
        };

        final Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_player_holder);
        if (fragment instanceof VideoDetailFragment && fragment.isVisible()) {
            onVideoDetailFragmentReady.run((VideoDetailFragment) fragment);
        } else {
            final VideoDetailFragment instance = VideoDetailFragment.getInstance(serviceId, url, title == null ? "" : title, playQueue);
            instance.setAutoplay(autoPlay);

            defaultTransaction(fragmentManager)
                    .replace(R.id.fragment_player_holder, instance)
                    .runOnCommit(() -> expandMainPlayer(instance.requireActivity()))
                    .commit();
        }
    }

    private interface RunnableWithVideoDetailFragment {
        void run(VideoDetailFragment detailFragment);
    }

    public static void expandMainPlayer(final Context context) {
        context.sendBroadcast(new Intent(VideoDetailFragment.ACTION_SHOW_MAIN_PLAYER));
    }

    public static void sendPlayerStartedEvent(final Context context) {
        context.sendBroadcast(new Intent(VideoDetailFragment.ACTION_PLAYER_STARTED));
    }

    public static void showMiniPlayer(final FragmentManager fragmentManager) {
        final VideoDetailFragment instance = VideoDetailFragment.getInstanceInCollapsedState();
        defaultTransaction(fragmentManager)
                .replace(R.id.fragment_player_holder, instance)
                .runOnCommit(() -> sendPlayerStartedEvent(instance.requireActivity()))
                .commitAllowingStateLoss();
    }

    public static void openChannelFragment(FragmentManager fragmentManager, int serviceId, String url, String name) {
        defaultTransaction(fragmentManager)
                .replace(R.id.fragment_holder, ChannelFragment.getInstance(serviceId, url, name == null ? "" : name))
                .addToBackStack(null)
                .commit();
    }

    public static void openPlaylistFragment(FragmentManager fragmentManager, int serviceId, String url, String name) {
        defaultTransaction(fragmentManager)
                .replace(R.id.fragment_holder, PlaylistFragment.getInstance(serviceId, url, name == null ? "" : name))
                .addToBackStack(null)
                .commit();
    }

    public static void openWhatsNewFragment(FragmentManager fragmentManager) {
        defaultTransaction(fragmentManager)
                .replace(R.id.fragment_holder, new FeedFragment())
                .addToBackStack(null)
                .commit();
    }

    public static void openLocalPlaylistFragment(FragmentManager fragmentManager, long playlistId, String name) {
        defaultTransaction(fragmentManager)
                .replace(R.id.fragment_holder, LocalPlaylistFragment.getInstance(playlistId, name == null ? "" : name))
                .addToBackStack(null)
                .commit();
    }

    public static void openVideoDetail(final Context context, final int serviceId, final String url) {
        openVideoDetail(context, serviceId, url, null);
    }

    public static void openVideoDetail(final Context context, final int serviceId, final String url, final String title) {
        final Intent openIntent = getOpenIntent(context, url, serviceId, StreamingService.LinkType.STREAM);
        if (title != null && !title.isEmpty()) {
            openIntent.putExtra(Constants.KEY_TITLE, title);
        }
        context.startActivity(openIntent);
    }

    public static void openDiscoverFragment(FragmentManager fragmentManager) {
        defaultTransaction(fragmentManager)
                .replace(R.id.fragment_holder, DiscoverFragment.getInstance())
                .addToBackStack(null)
                .commit();
    }

    public static void openLibraryFragment(FragmentManager fragmentManager) {
        defaultTransaction(fragmentManager)
                .replace(R.id.fragment_holder, LibraryFragment.getInstance())
                .addToBackStack(null)
                .commit();
    }

    public static void openSubscriptionFragment(final FragmentManager fragmentManager) {
        defaultTransaction(fragmentManager)
                .replace(R.id.fragment_holder, new SubscriptionFragment())
                .addToBackStack(null)
                .commit();
    }

    public static void openHistoryFragment(final FragmentManager fragmentManager) {
        defaultTransaction(fragmentManager)
                .replace(R.id.fragment_holder, new HistoryFragment())
                .addToBackStack(null)
                .commit();
    }

    public static void openSettings(Activity activity) {
        Intent intent = new Intent(activity, SettingsActivity.class);
        activity.startActivity(intent);
    }

    public static void openDownloads(Activity activity) {
        if (!PermissionHelper.checkStoragePermissions(activity, PermissionHelper.DOWNLOADS_REQUEST_CODE)) {
            return;
        }
        Intent intent = new Intent(activity, DownloadActivity.class);
        activity.startActivity(intent);
    }

    public static Intent getPopupPlayerActivityIntent(final Context context) {
        return getServicePlayerActivityIntent(context, PopupPlayerActivity.class);
    }

    private static Intent getServicePlayerActivityIntent(final Context context, final Class clazz) {
        Intent intent = new Intent(context, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    // Link handling
    private static Intent getOpenIntent(Context context, String url, int serviceId, StreamingService.LinkType type) {

        Intent mIntent = new Intent(context, MainActivity.class);
        mIntent.putExtra(Constants.KEY_SERVICE_ID, serviceId);
        mIntent.putExtra(Constants.KEY_URL, url);
        mIntent.putExtra(Constants.KEY_LINK_TYPE, type);
        return mIntent;
    }

    private static Intent getOpenIntent(Context context, String url, int serviceId, String thumbnailUrl, StreamingService.LinkType type) {

        Intent mIntent = new Intent(context, MainActivity.class);
        mIntent.putExtra(Constants.KEY_SERVICE_ID, serviceId);
        mIntent.putExtra(Constants.KEY_URL, url);
        mIntent.putExtra(Constants.KEY_LINK_TYPE, type);
        mIntent.putExtra(Constants.KEY_THUMBNAIL_URL, thumbnailUrl);
        return mIntent;
    }

    public static Intent getIntentByLink(Context context, String url) throws ExtractionException {
        return getIntentByLink(context, NewPipe.getServiceByUrl(url), url);
    }

    public static Intent getIntentByLink(Context context, StreamingService service, String url) throws ExtractionException {

        StreamingService.LinkType linkType = service.getLinkTypeByUrl(url);

        if (linkType == StreamingService.LinkType.NONE) {
            throw new ExtractionException("Url not known to service. service=" + service + " url=" + url);
        }

        Intent rIntent = getOpenIntent(context, url, service.getServiceId(), linkType);

        if (linkType == StreamingService.LinkType.STREAM) {
            rIntent.putExtra(AUTO_PLAY, PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.autoplay_through_intent_key), false));
        }

        return rIntent;
    }

    private static Uri openMarket(String packageName) {

        return Uri.parse("market://details").buildUpon()
                .appendQueryParameter("id", packageName)
                .build();
    }

    private static Uri getGooglePlay(String packageName) {

        return Uri.parse("https://play.google.com/store/apps/details").buildUpon()
                .appendQueryParameter("id", packageName)
                .build();
    }

    public static void openGooglePlayStore(Context context, String packageName) {

        try {
            // Try market:// scheme
            context.startActivity(new Intent(Intent.ACTION_VIEW, openMarket(packageName)));
        } catch (ActivityNotFoundException e) {
            // Fall back to google play URL (don't worry F-Droid can handle it :)
            context.startActivity(new Intent(Intent.ACTION_VIEW, getGooglePlay(packageName)));
        }
    }

    public static void composeEmail(Context context, String subject) {

        //String model = String.format("Model [%s]", Build.MODEL);
        //String os = String.format("OS [%s]", "Android");
        //String os_version = String.format("OS Version [%s]", Build.VERSION.RELEASE);
        //String emailBody = String.format("About Device:\n%s\n%s\n%s", model, os, os_version);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{Constants.FEEDBACK_EMAIL});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.feedback_message));

        // open mail apps
        try {
            context.startActivity(Intent.createChooser(intent, null));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(context, R.string.msg_no_apps, Toast.LENGTH_SHORT).show();
        }
    }

    public static void recreateActivity(Activity activity) {

        activity.finish();
        activity.overridePendingTransition(0, 0);
        activity.startActivity(activity.getIntent());
        activity.overridePendingTransition(0, 0);
    }
}