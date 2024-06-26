package io.awesome.gagtube.local.feed;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.facebook.ads.NativeAd;

import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.ServiceList;
import org.schabi.newpipe.extractor.channel.ChannelInfo;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.awesome.gagtube.R;
import io.awesome.gagtube.adsmanager.AdUtils;
import io.awesome.gagtube.adsmanager.admob.AdMobConfig;
import io.awesome.gagtube.adsmanager.applovin.AppLovinConfig;
import io.awesome.gagtube.adsmanager.facebook.FacebookConfig;
import io.awesome.gagtube.adsmanager.nativead.AppNativeAdView;
import io.awesome.gagtube.database.subscription.SubscriptionEntity;
import io.awesome.gagtube.fragments.list.BaseListFragment;
import io.awesome.gagtube.local.subscription.SubscriptionService;
import io.awesome.gagtube.report.UserAction;
import io.reactivex.Flowable;
import io.reactivex.MaybeObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class FeedFragment extends BaseListFragment<List<SubscriptionEntity>, Void> {
	
	@BindView(R.id.empty_message) TextView emptyMessage;
	
	private static final int OFF_SCREEN_ITEMS_COUNT = 3;
	private static final int MIN_ITEMS_INITIAL_LOAD = 8;
	private int FEED_LOAD_COUNT = MIN_ITEMS_INITIAL_LOAD;
	
	private int subscriptionPoolSize;
	
	private SubscriptionService subscriptionService;
	
	private AtomicBoolean allItemsLoaded = new AtomicBoolean(false);
	private HashSet<String> itemsLoaded = new HashSet<>();
	private final AtomicInteger requestLoadedAtomic = new AtomicInteger();
	
	private CompositeDisposable compositeDisposable = new CompositeDisposable();
	private Disposable subscriptionObserver;
	private Subscription feedSubscriber;

	/* AdMob */
	private AppNativeAdView nativeAdView;

	/* Facebook */
	private NativeAd fbNativeAd;

	/* AppLovin */
	private MaxNativeAdLoader nativeAdLoader;
	
	// Fragment LifeCycle
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		subscriptionService = SubscriptionService.getInstance(activity);
		
		FEED_LOAD_COUNT = howManyItemsToLoad();
	}
	
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_feed, container, false);
		ButterKnife.bind(this, view);
		
		return view;
	}
	
	@Override
	protected void initViews(View rootView, Bundle savedInstanceState) {
		
		super.initViews(rootView, savedInstanceState);
		
		Toolbar mToolbar = rootView.findViewById(R.id.default_toolbar);
		activity.getDelegate().setSupportActionBar(mToolbar);
		mToolbar.setTitle(R.string.fragment_whats_new);
		
		// for empty message
		emptyMessage.setText(R.string.no_results);

		if (AdUtils.ENABLE_ADMOB()) {
			View headerView = getLayoutInflater().inflate(R.layout.admob_native_ad_list_header, itemsList, false);
			nativeAdView = headerView.findViewById(R.id.template_view);
			// Native
			AdMobConfig.showNativeAd(activity, nativeAdView, infoListAdapter, headerView);
		} else if (AdUtils.ENABLE_FACEBOOK()) {
			View headerView = getLayoutInflater().inflate(R.layout.fb_native_ad_list_header, itemsList, false);
			// Native
			fbNativeAd = new NativeAd(activity, getString(R.string.facebook_ad_native));
			FacebookConfig.showNativeAd(activity, fbNativeAd, FacebookConfig.NativeAdType.MEDIUM, infoListAdapter, headerView);
		} else if (AdUtils.ENABLE_APPLOVIN()) {
			View headerView = getLayoutInflater().inflate(R.layout.applovin_native_ad, itemsList, false);
			nativeAdLoader = new MaxNativeAdLoader(getString(R.string.applovin_native_manual_ad), activity);
			AppLovinConfig.showApplovinNativeAd(activity, headerView, infoListAdapter, nativeAdLoader, true);
		}
	}
	
	@Override
	public void onPause() {
		
		super.onPause();
		
		disposeEverything();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (wasLoading.get()) doInitialLoadLogic();
	}
	
	@Override
	public void onDestroy() {
		AppLovinConfig.destroyApplovinNativeAd(nativeAdLoader);
		AdMobConfig.destroyNativeAd(nativeAdView);
		FacebookConfig.destroyNativeAd(fbNativeAd);
		super.onDestroy();
		
		disposeEverything();
		subscriptionService = null;
		compositeDisposable = null;
		subscriptionObserver = null;
		feedSubscriber = null;
	}
	
	@Override
	public void onDestroyView() {
		
		// Do not monitor for updates when user is not viewing the feed fragment.
		// This is a waste of bandwidth.
		disposeEverything();
		super.onDestroyView();
	}
	
	@Override
	public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
		
		super.onCreateOptionsMenu(menu, inflater);
		
		ActionBar supportActionBar = activity.getDelegate().getSupportActionBar();
		if (supportActionBar != null) {
			supportActionBar.setTitle(R.string.fragment_whats_new);
			supportActionBar.setDisplayShowTitleEnabled(true);
			supportActionBar.setDisplayShowTitleEnabled(true);
		}
	}
	
	@Override
	public void reloadContent() {
		
		resetFragment();
		
		super.reloadContent();
	}
	
	// StateSaving
	@Override
	public void writeTo(Queue<Object> objectsToSave) {
		
		super.writeTo(objectsToSave);
		
		objectsToSave.add(allItemsLoaded);
		objectsToSave.add(itemsLoaded);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void readFrom(@NonNull Queue<Object> savedObjects) throws Exception {
		
		super.readFrom(savedObjects);
		
		allItemsLoaded = (AtomicBoolean) savedObjects.poll();
		itemsLoaded = (HashSet<String>) savedObjects.poll();
	}
	
	// Feed Loader
	@Override
	public void startLoading(boolean forceLoad) {
		
		if (subscriptionObserver != null) subscriptionObserver.dispose();
		
		if (allItemsLoaded.get()) {
			if (infoListAdapter.getItemsList().size() == 0) {
				showEmptyState();
			}
			else {
				showListFooter(false);
				hideLoading();
			}
			
			isLoading.set(false);
			return;
		}
		
		isLoading.set(true);
		showLoading();
		showListFooter(true);
		subscriptionObserver = subscriptionService.getSubscription()
				.onErrorReturnItem(Collections.emptyList())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(this::handleResult, this::onError);
	}
	
	@Override
	public void handleResult(@NonNull List<SubscriptionEntity> result) {
		
		super.handleResult(result);
		
		if (result.isEmpty()) {
			infoListAdapter.clearStreamItemList();
			showEmptyState();
			return;
		}
		
		subscriptionPoolSize = result.size();
		Flowable.fromIterable(result).observeOn(AndroidSchedulers.mainThread())
				.subscribe(getSubscriptionObserver());
	}
	
	/**
	 * Responsible for reacting to user pulling request and starting a request for new feed stream.
	 * <p>
	 * On initialization, it automatically requests the amount of feed needed to display
	 * a minimum amount required (FEED_LOAD_SIZE).
	 * <p>
	 * Upon receiving a user pull, it creates a Single Observer to fetch the ChannelInfo
	 * containing the feed streams.
	 **/
	private Subscriber<SubscriptionEntity> getSubscriptionObserver() {
		
		return new Subscriber<SubscriptionEntity>() {
			
			@Override
			public void onSubscribe(Subscription s) {
				
				if (feedSubscriber != null) feedSubscriber.cancel();
				feedSubscriber = s;
				
				int requestSize = FEED_LOAD_COUNT - infoListAdapter.getItemsList().size();
				if (wasLoading.getAndSet(false)) requestSize = FEED_LOAD_COUNT;
				
				boolean hasToLoad = requestSize > 0;
				if (hasToLoad) {
					requestLoadedAtomic.set(infoListAdapter.getItemsList().size());
					requestFeed(requestSize);
				}
				isLoading.set(hasToLoad);
			}
			
			@Override
			public void onNext(SubscriptionEntity subscriptionEntity) {
				
				if (!itemsLoaded.contains(subscriptionEntity.getServiceId() + subscriptionEntity.getUrl())) {
					subscriptionService.getChannelInfo(subscriptionEntity)
							.observeOn(AndroidSchedulers.mainThread())
							.onErrorComplete(throwable -> FeedFragment.super.onError(throwable))
							.subscribe(getChannelInfoObserver(subscriptionEntity.getServiceId(), subscriptionEntity.getUrl()));
				}
				else {
					requestFeed(1);
				}
			}
			
			@Override
			public void onError(Throwable exception) {
				FeedFragment.this.onError(exception);
			}
			
			@Override
			public void onComplete() {
			}
		};
	}
	
	/**
	 * On each request, a subscription item from the updated table is transformed
	 * into a ChannelInfo, containing the latest streams from the channel.
	 * <p>
	 * Currently, the feed uses the first into from the list of streams.
	 * <p>
	 * If chosen feed already displayed, then we request another feed from another
	 * subscription, until the subscription table runs out of new items.
	 * <p>
	 * This Observer is self-contained and will dispose itself when complete. However, this
	 * does not obey the fragment lifecycle and may continue running in the background
	 * until it is complete. This is done due to RxJava2 no longer propagate errors once
	 * an observer is unsubscribed while the thread process is still running.
	 * <p>
	 * To solve the above issue, we can either set a global RxJava Error Handler, or
	 * manage exceptions case by case. This should be done if the current implementation is
	 * too costly when dealing with larger subscription sets.
	 *
	 * @param url + serviceId to put in {@link #allItemsLoaded} to signal that this specific entity has been loaded.
	 */
	private MaybeObserver<ChannelInfo> getChannelInfoObserver(final int serviceId, final String url) {
		
		return new MaybeObserver<ChannelInfo>() {
			
			private Disposable observer;
			
			@Override
			public void onSubscribe(Disposable d) {
				
				observer = d;
				compositeDisposable.add(d);
				isLoading.set(true);
			}
			
			// Called only when response is non-empty
			@Override
			public void onSuccess(final ChannelInfo channelInfo) {
				
				if (infoListAdapter == null || channelInfo.getRelatedItems().isEmpty()) {
					onDone();
					return;
				}
				
				final InfoItem item = channelInfo.getRelatedItems().get(0);
				// Keep requesting new items if the current one already exists
				boolean itemExists = doesItemExist(infoListAdapter.getItemsList(), item);
				if (!itemExists) {
					infoListAdapter.addInfoItem(item);
				}
				else {
					requestFeed(1);
				}
				onDone();
			}
			
			@Override
			public void onError(Throwable exception) {
				
				showSnackBarError(exception, UserAction.SUBSCRIPTION, ServiceList.YouTube.getServiceInfo().getName(), url, 0);
				requestFeed(1);
				onDone();
			}
			
			// Called only when response is empty
			@Override
			public void onComplete() {
				onDone();
			}
			
			private void onDone() {
				
				if (observer.isDisposed()) {
					return;
				}
				
				itemsLoaded.add(serviceId + url);
				compositeDisposable.remove(observer);
				
				int loaded = requestLoadedAtomic.incrementAndGet();
				if (loaded >= Math.min(FEED_LOAD_COUNT, subscriptionPoolSize)) {
					requestLoadedAtomic.set(0);
					isLoading.set(false);
				}
				
				if (itemsLoaded.size() == subscriptionPoolSize) {
					allItemsLoaded.set(true);
					showListFooter(false);
					isLoading.set(false);
					hideLoading();
					if (infoListAdapter.getItemsList().size() == 0) {
						showEmptyState();
					}
				}
			}
		};
	}
	
	@Override
	protected void loadMoreItems() {
		
		isLoading.set(true);
		delayHandler.removeCallbacksAndMessages(null);
		// Add a little of a delay when requesting more items because the cache is so fast,
		// that the view seems stuck to the user when he scroll to the bottom
		delayHandler.postDelayed(() -> requestFeed(FEED_LOAD_COUNT), 200);
	}
	
	@Override
	protected boolean hasMoreItems() {
		return !allItemsLoaded.get();
	}
	
	private final Handler delayHandler = new Handler();
	
	private void requestFeed(final int count) {
		
		if (feedSubscriber == null) return;
		
		isLoading.set(true);
		delayHandler.removeCallbacksAndMessages(null);
		feedSubscriber.request(count);
	}
	
	// Utils
	private void resetFragment() {
		
		if (subscriptionObserver != null) subscriptionObserver.dispose();
		if (compositeDisposable != null) compositeDisposable.clear();
		if (infoListAdapter != null) infoListAdapter.clearStreamItemList();
		
		delayHandler.removeCallbacksAndMessages(null);
		requestLoadedAtomic.set(0);
		allItemsLoaded.set(false);
		showListFooter(false);
		itemsLoaded.clear();
	}
	
	private void disposeEverything() {
		
		if (subscriptionObserver != null) subscriptionObserver.dispose();
		if (compositeDisposable != null) compositeDisposable.clear();
		if (feedSubscriber != null) feedSubscriber.cancel();
		delayHandler.removeCallbacksAndMessages(null);
	}
	
	private boolean doesItemExist(final List<InfoItem> items, final InfoItem item) {
		for (final InfoItem existingItem : items) {
			if (existingItem.getInfoType() == item.getInfoType() &&
					existingItem.getServiceId() == item.getServiceId() &&
					existingItem.getName().equals(item.getName()) &&
					existingItem.getUrl().equals(item.getUrl())) return true;
		}
		return false;
	}
	
	private int howManyItemsToLoad() {
		
		int heightPixels = getResources().getDisplayMetrics().heightPixels;
		int itemHeightPixels = activity.getResources().getDimensionPixelSize(R.dimen.video_item_search_height);
		
		int items = itemHeightPixels > 0 ? heightPixels / itemHeightPixels + OFF_SCREEN_ITEMS_COUNT : MIN_ITEMS_INITIAL_LOAD;
		return Math.max(MIN_ITEMS_INITIAL_LOAD, items);
	}
	
	// Fragment Error Handling
	@Override
	public void showError(String message, boolean showRetryButton) {
		
		resetFragment();
		super.showError(message, showRetryButton);
	}
	
	@Override
	protected boolean onError(Throwable exception) {
		
		if (super.onError(exception)) return true;
		
		int errorId = exception instanceof ExtractionException ? R.string.parsing_error : R.string.general_error;
		onUnrecoverableError(exception, UserAction.SOMETHING_ELSE, "none", "Requesting feed", errorId);
		return true;
	}
}
