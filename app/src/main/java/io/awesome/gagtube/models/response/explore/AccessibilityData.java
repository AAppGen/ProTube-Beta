package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class AccessibilityData{

	@SerializedName("accessibilityData")
	private AccessibilityData accessibilityData;

	@SerializedName("label")
	private String label;

	public AccessibilityData getAccessibilityData(){
		return accessibilityData;
	}

	public String getLabel(){
		return label;
	}

	@Override
 	public String toString(){
		return 
			"AccessibilityData{" + 
			"accessibilityData = '" + accessibilityData + '\'' + 
			",label = '" + label + '\'' + 
			"}";
		}
}