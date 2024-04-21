package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class UrlEndpoint{

	@SerializedName("url")
	private String url;

	@SerializedName("target")
	private String target;

	public String getUrl(){
		return url;
	}

	public String getTarget(){
		return target;
	}

	@Override
 	public String toString(){
		return 
			"UrlEndpoint{" + 
			"url = '" + url + '\'' + 
			",target = '" + target + '\'' + 
			"}";
		}
}