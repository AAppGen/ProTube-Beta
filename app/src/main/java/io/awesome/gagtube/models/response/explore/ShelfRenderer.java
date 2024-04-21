package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class ShelfRenderer{

	@SerializedName("trackingParams")
	private String trackingParams;

	@SerializedName("content")
	private Content content;

	public String getTrackingParams(){
		return trackingParams;
	}

	public Content getContent(){
		return content;
	}

	@Override
 	public String toString(){
		return 
			"ShelfRenderer{" + 
			"trackingParams = '" + trackingParams + '\'' + 
			",content = '" + content + '\'' + 
			"}";
		}
}