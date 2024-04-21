package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class ToggledAccessibility{

	@SerializedName("accessibilityData")
	private AccessibilityData accessibilityData;

	public AccessibilityData getAccessibilityData(){
		return accessibilityData;
	}

	@Override
 	public String toString(){
		return 
			"ToggledAccessibility{" + 
			"accessibilityData = '" + accessibilityData + '\'' + 
			"}";
		}
}