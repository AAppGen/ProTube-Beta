package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class Config{

	@SerializedName("webSearchboxConfig")
	private WebSearchboxConfig webSearchboxConfig;

	public WebSearchboxConfig getWebSearchboxConfig(){
		return webSearchboxConfig;
	}

	@Override
 	public String toString(){
		return 
			"Config{" + 
			"webSearchboxConfig = '" + webSearchboxConfig + '\'' + 
			"}";
		}
}