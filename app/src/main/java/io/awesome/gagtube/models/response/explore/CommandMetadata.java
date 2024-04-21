package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class CommandMetadata{

	@SerializedName("webCommandMetadata")
	private WebCommandMetadata webCommandMetadata;

	public WebCommandMetadata getWebCommandMetadata(){
		return webCommandMetadata;
	}

	@Override
 	public String toString(){
		return 
			"CommandMetadata{" + 
			"webCommandMetadata = '" + webCommandMetadata + '\'' + 
			"}";
		}
}