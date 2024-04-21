package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class WebCommandMetadata{

	@SerializedName("rootVe")
	private int rootVe;

	@SerializedName("webPageType")
	private String webPageType;

	@SerializedName("url")
	private String url;

	@SerializedName("apiUrl")
	private String apiUrl;

	@SerializedName("sendPost")
	private boolean sendPost;

	public int getRootVe(){
		return rootVe;
	}

	public String getWebPageType(){
		return webPageType;
	}

	public String getUrl(){
		return url;
	}

	public String getApiUrl(){
		return apiUrl;
	}

	public boolean isSendPost(){
		return sendPost;
	}

	@Override
 	public String toString(){
		return 
			"WebCommandMetadata{" + 
			"rootVe = '" + rootVe + '\'' + 
			",webPageType = '" + webPageType + '\'' + 
			",url = '" + url + '\'' + 
			",apiUrl = '" + apiUrl + '\'' + 
			",sendPost = '" + sendPost + '\'' + 
			"}";
		}
}