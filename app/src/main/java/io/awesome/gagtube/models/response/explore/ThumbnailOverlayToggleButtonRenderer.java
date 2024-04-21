package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class ThumbnailOverlayToggleButtonRenderer{

	@SerializedName("untoggledIcon")
	private UntoggledIcon untoggledIcon;

	@SerializedName("toggledIcon")
	private ToggledIcon toggledIcon;

	@SerializedName("toggledTooltip")
	private String toggledTooltip;

	@SerializedName("trackingParams")
	private String trackingParams;

	@SerializedName("toggledAccessibility")
	private ToggledAccessibility toggledAccessibility;

	@SerializedName("untoggledTooltip")
	private String untoggledTooltip;

	@SerializedName("untoggledAccessibility")
	private UntoggledAccessibility untoggledAccessibility;

	@SerializedName("untoggledServiceEndpoint")
	private UntoggledServiceEndpoint untoggledServiceEndpoint;

	@SerializedName("toggledServiceEndpoint")
	private ToggledServiceEndpoint toggledServiceEndpoint;

	@SerializedName("isToggled")
	private boolean isToggled;

	public UntoggledIcon getUntoggledIcon(){
		return untoggledIcon;
	}

	public ToggledIcon getToggledIcon(){
		return toggledIcon;
	}

	public String getToggledTooltip(){
		return toggledTooltip;
	}

	public String getTrackingParams(){
		return trackingParams;
	}

	public ToggledAccessibility getToggledAccessibility(){
		return toggledAccessibility;
	}

	public String getUntoggledTooltip(){
		return untoggledTooltip;
	}

	public UntoggledAccessibility getUntoggledAccessibility(){
		return untoggledAccessibility;
	}

	public UntoggledServiceEndpoint getUntoggledServiceEndpoint(){
		return untoggledServiceEndpoint;
	}

	public ToggledServiceEndpoint getToggledServiceEndpoint(){
		return toggledServiceEndpoint;
	}

	public boolean isIsToggled(){
		return isToggled;
	}

	@Override
 	public String toString(){
		return 
			"ThumbnailOverlayToggleButtonRenderer{" + 
			"untoggledIcon = '" + untoggledIcon + '\'' + 
			",toggledIcon = '" + toggledIcon + '\'' + 
			",toggledTooltip = '" + toggledTooltip + '\'' + 
			",trackingParams = '" + trackingParams + '\'' + 
			",toggledAccessibility = '" + toggledAccessibility + '\'' + 
			",untoggledTooltip = '" + untoggledTooltip + '\'' + 
			",untoggledAccessibility = '" + untoggledAccessibility + '\'' + 
			",untoggledServiceEndpoint = '" + untoggledServiceEndpoint + '\'' + 
			",toggledServiceEndpoint = '" + toggledServiceEndpoint + '\'' + 
			",isToggled = '" + isToggled + '\'' + 
			"}";
		}
}