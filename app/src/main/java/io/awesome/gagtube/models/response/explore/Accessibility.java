package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class Accessibility{

	@SerializedName("accessibilityData")
	private AccessibilityData accessibilityData;

	public AccessibilityData getAccessibilityData(){
		return accessibilityData;
	}

	@Override
 	public String toString(){
		return 
			"Accessibility{" + 
			"accessibilityData = '" + accessibilityData + '\'' + 
			"}";
		}
}