package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class CompactLinkRenderer{

	@SerializedName("trackingParams")
	private String trackingParams;

	@SerializedName("icon")
	private Icon icon;

	@SerializedName("title")
	private Title title;

	@SerializedName("navigationEndpoint")
	private NavigationEndpoint navigationEndpoint;

	public String getTrackingParams(){
		return trackingParams;
	}

	public Icon getIcon(){
		return icon;
	}

	public Title getTitle(){
		return title;
	}

	public NavigationEndpoint getNavigationEndpoint(){
		return navigationEndpoint;
	}

	@Override
 	public String toString(){
		return 
			"CompactLinkRenderer{" + 
			"trackingParams = '" + trackingParams + '\'' + 
			",icon = '" + icon + '\'' + 
			",title = '" + title + '\'' + 
			",navigationEndpoint = '" + navigationEndpoint + '\'' + 
			"}";
		}
}