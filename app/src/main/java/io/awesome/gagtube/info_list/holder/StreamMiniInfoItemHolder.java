package io.awesome.gagtube.info_list.holder;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.stream.StreamType;

import de.hdodenhof.circleimageview.CircleImageView;
import io.awesome.gagtube.App;
import io.awesome.gagtube.R;
import io.awesome.gagtube.info_list.InfoItemBuilder;
import io.awesome.gagtube.util.AppUtils;
import io.awesome.gagtube.util.ExtractorHelper;
import io.awesome.gagtube.util.GlideUtils;
import io.awesome.gagtube.util.Localization;
import io.awesome.gagtube.util.NavigationHelper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class StreamMiniInfoItemHolder extends InfoItemHolder {
	
	public final ImageView itemThumbnailView;
	public final TextView itemVideoTitleView;
	public final CircleImageView itemUploaderThumbnailView;
	public final TextView itemDurationView;
	private final ImageButton itemMoreAction;
	private final TextView itemAdditionalDetails;
	
	StreamMiniInfoItemHolder(InfoItemBuilder infoItemBuilder, int layoutId, ViewGroup parent) {
		super(infoItemBuilder, layoutId, parent);
		
		itemThumbnailView = itemView.findViewById(R.id.itemThumbnailView);
		itemVideoTitleView = itemView.findViewById(R.id.itemVideoTitleView);
		itemUploaderThumbnailView = itemView.findViewById(R.id.itemUploaderThumbnailView);
		itemDurationView = itemView.findViewById(R.id.itemDurationView);
		itemMoreAction = itemView.findViewById(R.id.btn_action);
		itemAdditionalDetails = itemView.findViewById(R.id.itemAdditionalDetails);
	}
	
	public StreamMiniInfoItemHolder(InfoItemBuilder infoItemBuilder, ViewGroup parent) {
		this(infoItemBuilder, R.layout.list_stream_mini_item, parent);
	}
	
	@SuppressLint("CheckResult")
	@Override
	public void updateFromItem(final InfoItem infoItem) {
		if (!(infoItem instanceof StreamInfoItem)) return;
		final StreamInfoItem item = (StreamInfoItem) infoItem;
		
		itemVideoTitleView.setText(item.getName());
		itemAdditionalDetails.setText(getStreamInfoDetail(item));

		if (item.getUploaderAvatarUrl() != null) {
			GlideUtils.loadAvatar(App.getAppContext(), itemUploaderThumbnailView, item.getUploaderAvatarUrl());
		} else {
			ExtractorHelper.getChannelInfo(item.getServiceId(), item.getUploaderUrl(), true)
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread()).subscribe(
							// onNext
							channelInfo -> {
								String avatarUrl = TextUtils.isEmpty(channelInfo.getAvatarUrl()) ? channelInfo.getAvatarUrl() : channelInfo.getAvatarUrl().replace("s100", "s720");
								GlideUtils.loadAvatar(App.getAppContext(), itemUploaderThumbnailView, avatarUrl);
							},
							// onError
							throwable -> {
							});
		}

		if (item.getDuration() > 0) {
			itemDurationView.setText(Localization.getDurationString(item.getDuration()));
			itemDurationView.setBackgroundResource(R.drawable.duration_background);
			itemDurationView.setVisibility(View.VISIBLE);
		}
		else if (item.getStreamType() == StreamType.LIVE_STREAM) {
			itemDurationView.setText(R.string.duration_live);
			itemDurationView.setBackgroundResource(R.drawable.duration_background_live);
			itemDurationView.setVisibility(View.VISIBLE);
		}
		else {
			itemDurationView.setVisibility(View.GONE);
		}
		
		// Default thumbnail is shown on error, while loading and if the url is empty
		String thumbnailUrl = item.getThumbnailUrl().contains("hqdefault") ? item.getThumbnailUrl().split("hqdefault.jpg")[0] + "hqdefault.jpg" : item.getThumbnailUrl();
		GlideUtils.loadThumbnail(App.getAppContext(), itemThumbnailView, thumbnailUrl);
		
		itemView.setOnClickListener(view -> {
			if (itemBuilder.getOnStreamSelectedListener() != null) {
				itemBuilder.getOnStreamSelectedListener().selected(item);
			}
		});

		itemUploaderThumbnailView.setOnClickListener(v -> {
			final AppCompatActivity activity = (AppCompatActivity) itemBuilder.getContext();
			try {
				NavigationHelper.openChannelFragment(activity.getSupportFragmentManager(), item.getServiceId(), item.getUploaderUrl(), item.getUploaderName());
			} catch (final Exception ignored) {
			}
		});
		
		itemMoreAction.setOnClickListener(view -> {
			if (itemBuilder.getOnStreamSelectedListener() != null) {
				itemBuilder.getOnStreamSelectedListener().more(item, itemMoreAction);
			}
		});
	}

	private String getStreamInfoDetail(final StreamInfoItem infoItem) {
		String detailInfo = infoItem.getUploaderName();
		if (infoItem.getViewCount() >= 0) {
			detailInfo += Localization.DOT_SEPARATOR + Localization.shortViewCount(itemBuilder.getContext(), infoItem.getViewCount());
		}

		final String uploadDate = getFormattedRelativeUploadDate(infoItem);
		if (!TextUtils.isEmpty(uploadDate)) {
			return Localization.concatenateStrings(detailInfo, uploadDate);
		}
		return detailInfo;
	}

	private String getFormattedRelativeUploadDate(final StreamInfoItem infoItem) {
		if (infoItem.getUploadDate() != null) {
			return Localization.relativeTime(infoItem.getUploadDate().date());
		}
		else {
			return infoItem.getTextualUploadDate();
		}
	}
}
