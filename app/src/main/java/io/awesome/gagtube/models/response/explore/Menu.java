package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class Menu{

	@SerializedName("menuRenderer")
	private MenuRenderer menuRenderer;

	public MenuRenderer getMenuRenderer(){
		return menuRenderer;
	}

	@Override
 	public String toString(){
		return 
			"Menu{" + 
			"menuRenderer = '" + menuRenderer + '\'' + 
			"}";
		}
}