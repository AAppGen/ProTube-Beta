package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class RunsItem{

	@SerializedName("text")
	private String text;

	@SerializedName("navigationEndpoint")
	private NavigationEndpoint navigationEndpoint;

	public String getText(){
		return text;
	}

	public NavigationEndpoint getNavigationEndpoint(){
		return navigationEndpoint;
	}

	@Override
 	public String toString(){
		return 
			"RunsItem{" + 
			"text = '" + text + '\'' + 
			",navigationEndpoint = '" + navigationEndpoint + '\'' + 
			"}";
		}
}