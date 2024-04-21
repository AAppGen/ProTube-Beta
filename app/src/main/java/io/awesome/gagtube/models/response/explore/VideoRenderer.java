package io.awesome.gagtube.models.response.explore;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class VideoRenderer{

	@SerializedName("lengthText")
	private LengthText lengthText;

	@SerializedName("thumbnail")
	private Thumbnail thumbnail;

	@SerializedName("videoId")
	private String videoId;

	@SerializedName("title")
	private Title title;

	@SerializedName("shortBylineText")
	private ShortBylineText shortBylineText;

	@SerializedName("menu")
	private Menu menu;

	@SerializedName("thumbnailOverlays")
	private List<ThumbnailOverlaysItem> thumbnailOverlays;

	@SerializedName("ownerText")
	private OwnerText ownerText;

	@SerializedName("longBylineText")
	private LongBylineText longBylineText;

	@SerializedName("trackingParams")
	private String trackingParams;

	@SerializedName("showActionMenu")
	private boolean showActionMenu;

	@SerializedName("publishedTimeText")
	private PublishedTimeText publishedTimeText;

	@SerializedName("viewCountText")
	private ViewCountText viewCountText;

	@SerializedName("ownerBadges")
	private List<OwnerBadgesItem> ownerBadges;

	@SerializedName("shortViewCountText")
	private ShortViewCountText shortViewCountText;

	@SerializedName("channelThumbnailSupportedRenderers")
	private ChannelThumbnailSupportedRenderers channelThumbnailSupportedRenderers;

	@SerializedName("descriptionSnippet")
	private DescriptionSnippet descriptionSnippet;

	@SerializedName("navigationEndpoint")
	private NavigationEndpoint navigationEndpoint;

	public LengthText getLengthText(){
		return lengthText;
	}

	public Thumbnail getThumbnail(){
		return thumbnail;
	}

	public String getVideoId(){
		return videoId;
	}

	public Title getTitle(){
		return title;
	}

	public ShortBylineText getShortBylineText(){
		return shortBylineText;
	}

	public Menu getMenu(){
		return menu;
	}

	public List<ThumbnailOverlaysItem> getThumbnailOverlays(){
		return thumbnailOverlays;
	}

	public OwnerText getOwnerText(){
		return ownerText;
	}

	public LongBylineText getLongBylineText(){
		return longBylineText;
	}

	public String getTrackingParams(){
		return trackingParams;
	}

	public boolean isShowActionMenu(){
		return showActionMenu;
	}

	public PublishedTimeText getPublishedTimeText(){
		return publishedTimeText;
	}

	public ViewCountText getViewCountText(){
		return viewCountText;
	}

	public List<OwnerBadgesItem> getOwnerBadges(){
		return ownerBadges;
	}

	public ShortViewCountText getShortViewCountText(){
		return shortViewCountText;
	}

	public ChannelThumbnailSupportedRenderers getChannelThumbnailSupportedRenderers(){
		return channelThumbnailSupportedRenderers;
	}

	public DescriptionSnippet getDescriptionSnippet(){
		return descriptionSnippet;
	}

	public NavigationEndpoint getNavigationEndpoint(){
		return navigationEndpoint;
	}

	@Override
 	public String toString(){
		return 
			"VideoRenderer{" + 
			"lengthText = '" + lengthText + '\'' + 
			",thumbnail = '" + thumbnail + '\'' + 
			",videoId = '" + videoId + '\'' + 
			",title = '" + title + '\'' + 
			",shortBylineText = '" + shortBylineText + '\'' + 
			",menu = '" + menu + '\'' + 
			",thumbnailOverlays = '" + thumbnailOverlays + '\'' + 
			",ownerText = '" + ownerText + '\'' + 
			",longBylineText = '" + longBylineText + '\'' + 
			",trackingParams = '" + trackingParams + '\'' + 
			",showActionMenu = '" + showActionMenu + '\'' + 
			",publishedTimeText = '" + publishedTimeText + '\'' + 
			",viewCountText = '" + viewCountText + '\'' + 
			",ownerBadges = '" + ownerBadges + '\'' + 
			",shortViewCountText = '" + shortViewCountText + '\'' + 
			",channelThumbnailSupportedRenderers = '" + channelThumbnailSupportedRenderers + '\'' + 
			",descriptionSnippet = '" + descriptionSnippet + '\'' + 
			",navigationEndpoint = '" + navigationEndpoint + '\'' + 
			"}";
		}
}