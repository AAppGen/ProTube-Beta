package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class WebSearchboxConfig{

	@SerializedName("hasOnscreenKeyboard")
	private boolean hasOnscreenKeyboard;

	@SerializedName("requestLanguage")
	private String requestLanguage;

	@SerializedName("requestDomain")
	private String requestDomain;

	@SerializedName("focusSearchbox")
	private boolean focusSearchbox;

	public boolean isHasOnscreenKeyboard(){
		return hasOnscreenKeyboard;
	}

	public String getRequestLanguage(){
		return requestLanguage;
	}

	public String getRequestDomain(){
		return requestDomain;
	}

	public boolean isFocusSearchbox(){
		return focusSearchbox;
	}

	@Override
 	public String toString(){
		return 
			"WebSearchboxConfig{" + 
			"hasOnscreenKeyboard = '" + hasOnscreenKeyboard + '\'' + 
			",requestLanguage = '" + requestLanguage + '\'' + 
			",requestDomain = '" + requestDomain + '\'' + 
			",focusSearchbox = '" + focusSearchbox + '\'' + 
			"}";
		}
}