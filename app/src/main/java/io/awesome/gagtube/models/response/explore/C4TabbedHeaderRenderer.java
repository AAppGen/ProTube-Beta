package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class C4TabbedHeaderRenderer{

	@SerializedName("trackingParams")
	private String trackingParams;

	@SerializedName("avatar")
	private Avatar avatar;

	@SerializedName("title")
	private String title;

	public String getTrackingParams(){
		return trackingParams;
	}

	public Avatar getAvatar(){
		return avatar;
	}

	public String getTitle(){
		return title;
	}

	@Override
 	public String toString(){
		return 
			"C4TabbedHeaderRenderer{" + 
			"trackingParams = '" + trackingParams + '\'' + 
			",avatar = '" + avatar + '\'' + 
			",title = '" + title + '\'' + 
			"}";
		}
}