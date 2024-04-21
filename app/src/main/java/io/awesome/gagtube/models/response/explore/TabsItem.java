package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class TabsItem{

	@SerializedName("tabRenderer")
	private TabRenderer tabRenderer;

	public TabRenderer getTabRenderer(){
		return tabRenderer;
	}

	@Override
 	public String toString(){
		return 
			"TabsItem{" + 
			"tabRenderer = '" + tabRenderer + '\'' + 
			"}";
		}
}