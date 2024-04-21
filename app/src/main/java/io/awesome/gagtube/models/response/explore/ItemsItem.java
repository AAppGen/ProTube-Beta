package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class ItemsItem{

	@SerializedName("videoRenderer")
	private VideoRenderer videoRenderer;

	@SerializedName("menuServiceItemRenderer")
	private MenuServiceItemRenderer menuServiceItemRenderer;

	@SerializedName("compactLinkRenderer")
	private CompactLinkRenderer compactLinkRenderer;

	public VideoRenderer getVideoRenderer(){
		return videoRenderer;
	}

	public MenuServiceItemRenderer getMenuServiceItemRenderer(){
		return menuServiceItemRenderer;
	}

	public CompactLinkRenderer getCompactLinkRenderer(){
		return compactLinkRenderer;
	}

	@Override
 	public String toString(){
		return 
			"ItemsItem{" + 
			"videoRenderer = '" + videoRenderer + '\'' + 
			",menuServiceItemRenderer = '" + menuServiceItemRenderer + '\'' + 
			",compactLinkRenderer = '" + compactLinkRenderer + '\'' + 
			"}";
		}
}