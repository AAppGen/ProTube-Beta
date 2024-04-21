package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class Header{

	@SerializedName("c4TabbedHeaderRenderer")
	private C4TabbedHeaderRenderer c4TabbedHeaderRenderer;

	public C4TabbedHeaderRenderer getC4TabbedHeaderRenderer(){
		return c4TabbedHeaderRenderer;
	}

	@Override
 	public String toString(){
		return 
			"Header{" + 
			"c4TabbedHeaderRenderer = '" + c4TabbedHeaderRenderer + '\'' + 
			"}";
		}
}