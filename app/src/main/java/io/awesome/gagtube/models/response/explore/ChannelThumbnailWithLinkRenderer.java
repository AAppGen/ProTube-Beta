package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class ChannelThumbnailWithLinkRenderer{

	@SerializedName("thumbnail")
	private Thumbnail thumbnail;

	@SerializedName("accessibility")
	private Accessibility accessibility;

	@SerializedName("navigationEndpoint")
	private NavigationEndpoint navigationEndpoint;

	public Thumbnail getThumbnail(){
		return thumbnail;
	}

	public Accessibility getAccessibility(){
		return accessibility;
	}

	public NavigationEndpoint getNavigationEndpoint(){
		return navigationEndpoint;
	}

	@Override
 	public String toString(){
		return 
			"ChannelThumbnailWithLinkRenderer{" + 
			"thumbnail = '" + thumbnail + '\'' + 
			",accessibility = '" + accessibility + '\'' + 
			",navigationEndpoint = '" + navigationEndpoint + '\'' + 
			"}";
		}
}