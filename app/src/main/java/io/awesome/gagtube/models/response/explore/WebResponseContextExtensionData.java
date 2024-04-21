package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class WebResponseContextExtensionData{

	@SerializedName("hasDecorated")
	private boolean hasDecorated;

	public boolean isHasDecorated(){
		return hasDecorated;
	}

	@Override
 	public String toString(){
		return 
			"WebResponseContextExtensionData{" + 
			"hasDecorated = '" + hasDecorated + '\'' + 
			"}";
		}
}