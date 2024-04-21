package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class HotkeyAccessibilityLabel{

	@SerializedName("accessibilityData")
	private AccessibilityData accessibilityData;

	public AccessibilityData getAccessibilityData(){
		return accessibilityData;
	}

	@Override
 	public String toString(){
		return 
			"HotkeyAccessibilityLabel{" + 
			"accessibilityData = '" + accessibilityData + '\'' + 
			"}";
		}
}