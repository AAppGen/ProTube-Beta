package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class ButtonRenderer{

	@SerializedName("trackingParams")
	private String trackingParams;

	@SerializedName("size")
	private String size;

	@SerializedName("accessibilityData")
	private AccessibilityData accessibilityData;

	@SerializedName("icon")
	private Icon icon;

	@SerializedName("style")
	private String style;

	@SerializedName("isDisabled")
	private boolean isDisabled;

	@SerializedName("tooltip")
	private String tooltip;

	@SerializedName("serviceEndpoint")
	private ServiceEndpoint serviceEndpoint;

	@SerializedName("command")
	private Command command;

	@SerializedName("text")
	private Text text;

	@SerializedName("targetId")
	private String targetId;

	@SerializedName("navigationEndpoint")
	private NavigationEndpoint navigationEndpoint;

	public String getTrackingParams(){
		return trackingParams;
	}

	public String getSize(){
		return size;
	}

	public AccessibilityData getAccessibilityData(){
		return accessibilityData;
	}

	public Icon getIcon(){
		return icon;
	}

	public String getStyle(){
		return style;
	}

	public boolean isIsDisabled(){
		return isDisabled;
	}

	public String getTooltip(){
		return tooltip;
	}

	public ServiceEndpoint getServiceEndpoint(){
		return serviceEndpoint;
	}

	public Command getCommand(){
		return command;
	}

	public Text getText(){
		return text;
	}

	public String getTargetId(){
		return targetId;
	}

	public NavigationEndpoint getNavigationEndpoint(){
		return navigationEndpoint;
	}

	@Override
 	public String toString(){
		return 
			"ButtonRenderer{" + 
			"trackingParams = '" + trackingParams + '\'' + 
			",size = '" + size + '\'' + 
			",accessibilityData = '" + accessibilityData + '\'' + 
			",icon = '" + icon + '\'' + 
			",style = '" + style + '\'' + 
			",isDisabled = '" + isDisabled + '\'' + 
			",tooltip = '" + tooltip + '\'' + 
			",serviceEndpoint = '" + serviceEndpoint + '\'' + 
			",command = '" + command + '\'' + 
			",text = '" + text + '\'' + 
			",targetId = '" + targetId + '\'' + 
			",navigationEndpoint = '" + navigationEndpoint + '\'' + 
			"}";
		}
}