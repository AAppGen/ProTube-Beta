package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class LengthText{

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
			"LengthText{" + 
			"simpleText = '" + simpleText + '\'' + 
			",accessibility = '" + accessibility + '\'' + 
			"}";
		}
}