package io.awesome.gagtube.fragments.detail;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.facebook.ads.AdSize;
import com.facebook.ads.NativeAd;

import org.schabi.newpipe.extractor.ListExtractor;
import org.schabi.newpipe.extractor.stream.StreamInfo;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.awesome.gagtube.R;
import io.awesome.gagtube.adsmanager.AdUtils;
import io.awesome.gagtube.adsmanager.admob.AdMobConfig;
import io.awesome.gagtube.adsmanager.applovin.AppLovinConfig;
import io.awesome.gagtube.adsmanager.facebook.FacebookConfig;
import io.awesome.gagtube.adsmanager.nativead.AppNativeAdView;
import io.awesome.gagtube.databinding.RelatedVideosHeaderBinding;
import io.awesome.gagtube.fragments.list.BaseListInfoFragment;
import io.reactivex.Single;

public class RelatedVideosFragment extends BaseListInfoFragment<RelatedItemInfo>
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String INFO_KEY = "related_info_key";

    private RelatedItemInfo relatedItemInfo;

    private RelatedVideosHeaderBinding headerBinding;
    // AdMob
    AppNativeAdView nativeAdView;

    // Facebook
    private NativeAd fbNativeAd;
    // AppLovin
    private MaxNativeAdLoader nativeAdLoader;

    public static RelatedVideosFragment getInstance(final StreamInfo info) {
        final RelatedVideosFragment instance = new RelatedVideosFragment();
        instance.setInitialData(info);
        return instance;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_related_videos, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initViews(View rootView, Bundle savedInstanceState) {
        super.initViews(rootView, savedInstanceState);
        infoListAdapter.useMiniItemVariants(true);
    }

    @Override
    public void onDestroyView() {
        if (nativeAdView != null) {
            nativeAdView.destroyNativeAd();
        }
        AdMobConfig.destroyNativeAd(nativeAdView);
        headerBinding = null;
        super.onDestroyView();
    }

    @Nullable
    @Override
    protected View getListHeader() {
        if (relatedItemInfo == null || relatedItemInfo.getRelatedItems() == null) {
            return null;
        }

        headerBinding = RelatedVideosHeaderBinding.inflate(activity.getLayoutInflater(), itemsList, false);
        if (AdUtils.ENABLE_ADMOB()) {
            nativeAdView = headerBinding.getRoot().findViewById(R.id.template_view);
            AdMobConfig.showNativeAd(activity, nativeAdView);
        } else if (AdUtils.ENABLE_FACEBOOK()) {
            fbNativeAd = new NativeAd(activity, getString(R.string.facebook_ad_native));
            FacebookConfig.showNativeAd(activity, headerBinding.getRoot(), fbNativeAd, FacebookConfig.NativeAdType.MEDIUM);
        } else if (AdUtils.ENABLE_APPLOVIN()) {
            nativeAdLoader = new MaxNativeAdLoader(getString(R.string.applovin_native_manual_ad), activity);
            AppLovinConfig.showApplovinNativeAd(activity, headerBinding.getRoot(), nativeAdLoader, true);
        }

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(requireContext());
        final boolean autoplay = pref.getBoolean(getString(R.string.auto_queue_key), false);
        headerBinding.switchAutoPlay.setChecked(autoplay);
        headerBinding.switchAutoPlay.setOnCheckedChangeListener((compoundButton, b) ->
                PreferenceManager.getDefaultSharedPreferences(requireContext()).edit().putBoolean(getString(R.string.auto_queue_key), b).apply());

        return headerBinding.getRoot();
    }

    @Override
    protected Single<ListExtractor.InfoItemsPage> loadMoreItemsLogic() {
        return Single.fromCallable(ListExtractor.InfoItemsPage::emptyPage);
    }

    @Override
    protected Single<RelatedItemInfo> loadResult(final boolean forceLoad) {
        return Single.fromCallable(() -> relatedItemInfo);
    }

    @Override
    public void showLoading() {
        super.showLoading();
        if (headerBinding != null) {
            headerBinding.getRoot().setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void handleResult(@NonNull final RelatedItemInfo result) {
        super.handleResult(result);
        if (headerBinding != null) {
            headerBinding.getRoot().setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setTitle(final String title) {
        // Nothing to do - override parent
    }

    @Override
    public void onCreateOptionsMenu(@NonNull final Menu menu, @NonNull final MenuInflater inflater) {
        // Nothing to do - override parent
    }

    private void setInitialData(final StreamInfo info) {
        super.setInitialData(info.getServiceId(), info.getUrl(), info.getName());
        if (this.relatedItemInfo == null) {
            this.relatedItemInfo = RelatedItemInfo.getInfo(info);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(INFO_KEY, relatedItemInfo);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull final Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        final Serializable serializable = savedState.getSerializable(INFO_KEY);
        if (serializable instanceof RelatedItemInfo) {
            this.relatedItemInfo = (RelatedItemInfo) serializable;
        }
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        if (headerBinding != null && getString(R.string.auto_queue_key).equals(key)) {
            headerBinding.switchAutoPlay.setChecked(sharedPreferences.getBoolean(key, false));
        }
    }

    @Override
    public void onDestroy() {
        AppLovinConfig.destroyApplovinNativeAd(nativeAdLoader);
        super.onDestroy();
    }
}
