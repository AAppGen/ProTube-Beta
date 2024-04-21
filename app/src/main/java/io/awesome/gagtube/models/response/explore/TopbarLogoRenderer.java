package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class TopbarLogoRenderer{

	@SerializedName("endpoint")
	private Endpoint endpoint;

	@SerializedName("trackingParams")
	private String trackingParams;

	@SerializedName("iconImage")
	private IconImage iconImage;

	@SerializedName("tooltipText")
	private TooltipText tooltipText;

	@SerializedName("overrideEntityKey")
	private String overrideEntityKey;

	public Endpoint getEndpoint(){
		return endpoint;
	}

	public String getTrackingParams(){
		return trackingParams;
	}

	public IconImage getIconImage(){
		return iconImage;
	}

	public TooltipText getTooltipText(){
		return tooltipText;
	}

	public String getOverrideEntityKey(){
		return overrideEntityKey;
	}

	@Override
 	public String toString(){
		return 
			"TopbarLogoRenderer{" + 
			"endpoint = '" + endpoint + '\'' + 
			",trackingParams = '" + trackingParams + '\'' + 
			",iconImage = '" + iconImage + '\'' + 
			",tooltipText = '" + tooltipText + '\'' + 
			",overrideEntityKey = '" + overrideEntityKey + '\'' + 
			"}";
		}
}