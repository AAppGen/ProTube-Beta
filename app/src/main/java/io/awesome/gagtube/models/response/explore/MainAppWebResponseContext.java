package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class MainAppWebResponseContext{

	@SerializedName("loggedOut")
	private boolean loggedOut;

	public boolean isLoggedOut(){
		return loggedOut;
	}

	@Override
 	public String toString(){
		return 
			"MainAppWebResponseContext{" + 
			"loggedOut = '" + loggedOut + '\'' + 
			"}";
		}
}