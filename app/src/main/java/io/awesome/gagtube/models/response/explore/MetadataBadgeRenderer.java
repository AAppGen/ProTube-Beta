package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class MetadataBadgeRenderer{

	@SerializedName("trackingParams")
	private String trackingParams;

	@SerializedName("accessibilityData")
	private AccessibilityData accessibilityData;

	@SerializedName("icon")
	private Icon icon;

	@SerializedName("tooltip")
	private String tooltip;

	@SerializedName("style")
	private String style;

	public String getTrackingParams(){
		return trackingParams;
	}

	public AccessibilityData getAccessibilityData(){
		return accessibilityData;
	}

	public Icon getIcon(){
		return icon;
	}

	public String getTooltip(){
		return tooltip;
	}

	public String getStyle(){
		return style;
	}

	@Override
 	public String toString(){
		return 
			"MetadataBadgeRenderer{" + 
			"trackingParams = '" + trackingParams + '\'' + 
			",accessibilityData = '" + accessibilityData + '\'' + 
			",icon = '" + icon + '\'' + 
			",tooltip = '" + tooltip + '\'' + 
			",style = '" + style + '\'' + 
			"}";
		}
}