package io.awesome.gagtube.fragments.list.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.facebook.ads.NativeAd;

import org.jetbrains.annotations.NotNull;
import org.schabi.newpipe.extractor.ListExtractor;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.ServiceList;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.kiosk.KioskInfo;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandlerFactory;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.awesome.gagtube.R;
import io.awesome.gagtube.adsmanager.AdUtils;
import io.awesome.gagtube.adsmanager.admob.AdMobConfig;
import io.awesome.gagtube.adsmanager.applovin.AppLovinConfig;
import io.awesome.gagtube.adsmanager.facebook.FacebookConfig;
import io.awesome.gagtube.adsmanager.nativead.AppNativeAdView;
import io.awesome.gagtube.fragments.list.BaseListInfoFragment;
import io.awesome.gagtube.report.UserAction;
import io.awesome.gagtube.util.AnimationUtils;
import io.awesome.gagtube.util.ExtractorHelper;
import io.awesome.gagtube.util.NavigationHelper;
import io.awesome.gagtube.util.ServiceHelper;
import io.reactivex.Single;

public class TrendingFragment extends BaseListInfoFragment<KioskInfo> {
	
	/* AdMob */
	private AppNativeAdView nativeAdView;

	/* Facebook */
	private NativeAd fbNativeAd;

	/* AppLovin */
	private MaxNativeAdLoader nativeAdLoader;

	@NonNull
	public static TrendingFragment getInstance(int serviceId) {
		
		try {
			return getInstance(serviceId, NewPipe.getService(serviceId).getKioskList().getDefaultKioskId());
		}
		catch (ExtractionException e) {
			return new TrendingFragment();
		}
	}
	
	@NonNull
	public static TrendingFragment getInstance(int serviceId, String kioskId) {
		
		try {
			TrendingFragment instance = new TrendingFragment();
			StreamingService service = NewPipe.getService(serviceId);
			
			ListLinkHandlerFactory kioskLinkHandlerFactory = service.getKioskList().getListLinkHandlerFactoryByType(kioskId);
			instance.setInitialData(serviceId, kioskLinkHandlerFactory.fromId(kioskId).getUrl(), kioskId);
			
			return instance;
		}
		catch (ExtractionException e) {
			return new TrendingFragment();
		}
	}
	
	// LifeCycle
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		name = getString(R.string.trending);
	}
	
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_trending, container, false);
		ButterKnife.bind(this, view);
		
		return view;
	}
	
	@Override
	protected void initViews(View rootView, Bundle savedInstanceState) {
		super.initViews(rootView, savedInstanceState);

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
	public void onResume() {
		super.onResume();
	}
	
	// Menu
	@Override
	public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
		
		super.onCreateOptionsMenu(menu, inflater);
		
		ActionBar supportActionBar = activity.getSupportActionBar();
		if (supportActionBar != null) {
			supportActionBar.setDisplayHomeAsUpEnabled(false);
		}
	}
	
	// Load and handle
	@Override
	public Single<KioskInfo> loadResult(boolean forceReload) {
		
		return ExtractorHelper.getKioskInfo(serviceId, url, forceReload);
	}
	
	@Override
	public Single<ListExtractor.InfoItemsPage> loadMoreItemsLogic() {
		
		return ExtractorHelper.getMoreKioskItems(serviceId, url, currentNextPage);
	}
	
	// Contract
	@Override
	public void showLoading() {
		
		super.showLoading();
		
		AnimationUtils.animateView(itemsList, false, 100);
	}
	
	@Override
	public void handleResult(@NonNull final KioskInfo result) {
		
		super.handleResult(result);
		
		if (!result.getErrors().isEmpty()) {
			showSnackBarError(result.getErrors(), UserAction.REQUESTED_MAIN_CONTENT, ServiceList.YouTube.getServiceInfo().getName(), result.getUrl(), 0);
		}
	}
	
	@Override
	public void handleNextItems(ListExtractor.InfoItemsPage result) {
		
		super.handleNextItems(result);
		
		if (!result.getErrors().isEmpty()) {
			showSnackBarError(result.getErrors(), UserAction.REQUESTED_PLAYLIST, ServiceList.YouTube.getServiceInfo().getName(), "Get next page of: " + url, 0);
		}
	}
	
	@OnClick(R.id.action_search)
	void onSearch() {
		// open SearchFragment
		NavigationHelper.openSearchFragment(getFM(), ServiceHelper.getSelectedServiceId(activity), "");
	}
	
	@OnClick(R.id.action_settings)
	void onSettings() {
		// open Settings
		NavigationHelper.openSettings(activity);
	}
	
	@Override
	public void onDestroy() {
		AppLovinConfig.destroyApplovinNativeAd(nativeAdLoader);
		AdMobConfig.destroyNativeAd(nativeAdView);
		FacebookConfig.destroyNativeAd(fbNativeAd);
		super.onDestroy();
	}
}
