package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class CommonConfig{

	@SerializedName("url")
	private String url;

	public String getUrl(){
		return url;
	}

	@Override
 	public String toString(){
		return 
			"CommonConfig{" + 
			"url = '" + url + '\'' + 
			"}";
		}
}