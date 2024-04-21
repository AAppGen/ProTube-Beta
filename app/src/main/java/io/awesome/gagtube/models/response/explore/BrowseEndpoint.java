package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class BrowseEndpoint{

	@SerializedName("browseId")
	private String browseId;

	@SerializedName("params")
	private String params;

	@SerializedName("canonicalBaseUrl")
	private String canonicalBaseUrl;

	public String getBrowseId(){
		return browseId;
	}

	public String getParams(){
		return params;
	}

	public String getCanonicalBaseUrl(){
		return canonicalBaseUrl;
	}

	@Override
 	public String toString(){
		return 
			"BrowseEndpoint{" + 
			"browseId = '" + browseId + '\'' + 
			",params = '" + params + '\'' + 
			",canonicalBaseUrl = '" + canonicalBaseUrl + '\'' + 
			"}";
		}
}