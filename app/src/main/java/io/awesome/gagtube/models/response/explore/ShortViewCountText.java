package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class ShortViewCountText{

	@SerializedName("simpleText")
	private String simpleText;

	@SerializedName("accessibility")
	private Accessibility accessibility;

	public String getSimpleText(){
		return simpleText;
	}

	public Accessibility getAccessibility(){
		return accessibility;
	}

	@Override
 	public String toString(){
		return 
			"ShortViewCountText{" + 
			"simpleText = '" + simpleText + '\'' + 
			",accessibility = '" + accessibility + '\'' + 
			"}";
		}
}