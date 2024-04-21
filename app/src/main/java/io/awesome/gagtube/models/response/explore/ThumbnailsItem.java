package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class ThumbnailsItem{

	@SerializedName("width")
	private int width;

	@SerializedName("url")
	private String url;

	@SerializedName("height")
	private int height;

	public int getWidth(){
		return width;
	}

	public String getUrl(){
		return url;
	}

	public int getHeight(){
		return height;
	}

	@Override
 	public String toString(){
		return 
			"ThumbnailsItem{" + 
			"width = '" + width + '\'' + 
			",url = '" + url + '\'' + 
			",height = '" + height + '\'' + 
			"}";
		}
}