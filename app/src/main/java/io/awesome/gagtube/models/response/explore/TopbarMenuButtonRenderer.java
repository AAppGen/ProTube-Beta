package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class TopbarMenuButtonRenderer{

	@SerializedName("menuRequest")
	private MenuRequest menuRequest;

	@SerializedName("trackingParams")
	private String trackingParams;

	@SerializedName("accessibility")
	private Accessibility accessibility;

	@SerializedName("icon")
	private Icon icon;

	@SerializedName("tooltip")
	private String tooltip;

	@SerializedName("style")
	private String style;

	@SerializedName("targetId")
	private String targetId;

	@SerializedName("menuRenderer")
	private MenuRenderer menuRenderer;

	public MenuRequest getMenuRequest(){
		return menuRequest;
	}

	public String getTrackingParams(){
		return trackingParams;
	}

	public Accessibility getAccessibility(){
		return accessibility;
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

	public String getTargetId(){
		return targetId;
	}

	public MenuRenderer getMenuRenderer(){
		return menuRenderer;
	}

	@Override
 	public String toString(){
		return 
			"TopbarMenuButtonRenderer{" + 
			"menuRequest = '" + menuRequest + '\'' + 
			",trackingParams = '" + trackingParams + '\'' + 
			",accessibility = '" + accessibility + '\'' + 
			",icon = '" + icon + '\'' + 
			",tooltip = '" + tooltip + '\'' + 
			",style = '" + style + '\'' + 
			",targetId = '" + targetId + '\'' + 
			",menuRenderer = '" + menuRenderer + '\'' + 
			"}";
		}
}