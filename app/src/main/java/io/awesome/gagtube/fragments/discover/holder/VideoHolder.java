package io.awesome.gagtube.fragments.discover.holder;

import android.annotation.SuppressLint;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.awesome.gagtube.App;
import io.awesome.gagtube.R;
import io.awesome.gagtube.fragments.discover.adapter.VideoListAdapter;
import io.awesome.gagtube.models.response.explore.ItemsItem;
import io.awesome.gagtube.util.Constants;
import io.awesome.gagtube.util.GlideUtils;
import io.awesome.gagtube.util.Localization;
import io.awesome.gagtube.util.NavigationHelper;
import io.awesome.gagtube.util.recyclerview.AbstractViewHolder;

public class VideoHolder extends AbstractViewHolder {

    @BindView(R.id.itemThumbnailView)
    ImageView thumbnails;
    @BindView(R.id.itemDurationView)
    TextView duration;
    @BindView(R.id.itemVideoTitleView)
    TextView title;
    @BindView(R.id.itemUploaderThumbnailView)
    CircleImageView itemUploaderThumbnailView;
    @BindView(R.id.itemAdditionalDetails)
    TextView additionalInfo;
    @BindView(R.id.btn_action)
    ImageButton btnMoreOptions;

    public VideoHolder(ViewGroup parent, VideoListAdapter.Listener listener) {

        super(parent, R.layout.list_stream_item_medium);

        itemView.setOnClickListener(view -> listener.onVideoClicked(getBindingAdapterPosition()));
        btnMoreOptions.setOnClickListener(view -> listener.onMoreOption(getBindingAdapterPosition(), view));
    }

    @SuppressLint("CheckResult")
    public void set(ItemsItem item) {

        title.setText(item.getVideoRenderer().getTitle().getRuns().get(0).getText());

        duration.setText(item.getVideoRenderer().getLengthText().getSimpleText());

        additionalInfo.setText(getAdditionalInfo(item));

        GlideUtils.loadAvatar(App.getAppContext(), itemUploaderThumbnailView, item.getVideoRenderer().getChannelThumbnailSupportedRenderers().getChannelThumbnailWithLinkRenderer().getThumbnail().getThumbnails().get(0).getUrl());

        // default thumbnail is shown on error, while loading and if the url is empty
        String thumbnailUrl = item.getVideoRenderer().getThumbnail().getThumbnails().get(0).getUrl().contains("hqdefault") ? item.getVideoRenderer().getThumbnail().getThumbnails().get(0).getUrl().split("hqdefault.jpg")[0] + "hqdefault.jpg" : item.getVideoRenderer().getThumbnail().getThumbnails().get(0).getUrl();
        GlideUtils.loadThumbnail(App.getAppContext(), thumbnails, thumbnailUrl);

        itemUploaderThumbnailView.setOnClickListener(v -> {
            final AppCompatActivity activity = (AppCompatActivity) itemView.getContext();
            try {
                String uploaderUrl = Constants.YOUTUBE_URL + item.getVideoRenderer().getOwnerText().getRuns().get(0).getNavigationEndpoint().getCommandMetadata().getWebCommandMetadata().getUrl();
                String uploaderName = item.getVideoRenderer().getOwnerText().getRuns().get(0).getText();
                NavigationHelper.openChannelFragment(activity.getSupportFragmentManager(), Constants.YOUTUBE_SERVICE_ID, uploaderUrl, uploaderName);
            } catch (final Exception ignored) {
            }
        });
    }

    private String getAdditionalInfo(ItemsItem item) {
        String detailInfo = item.getVideoRenderer().getOwnerText().getRuns().get(0).getText();
        detailInfo += Localization.DOT_SEPARATOR + item.getVideoRenderer().getShortViewCountText().getSimpleText();
        detailInfo += Localization.DOT_SEPARATOR + item.getVideoRenderer().getPublishedTimeText().getSimpleText();
        return detailInfo;
    }
}
