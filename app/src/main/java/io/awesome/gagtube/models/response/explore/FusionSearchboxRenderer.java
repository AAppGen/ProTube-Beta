package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class FusionSearchboxRenderer{

	@SerializedName("trackingParams")
	private String trackingParams;

	@SerializedName("searchEndpoint")
	private SearchEndpoint searchEndpoint;

	@SerializedName("clearButton")
	private ClearButton clearButton;

	@SerializedName("icon")
	private Icon icon;

	@SerializedName("placeholderText")
	private PlaceholderText placeholderText;

	@SerializedName("config")
	private Config config;

	public String getTrackingParams(){
		return trackingParams;
	}

	public SearchEndpoint getSearchEndpoint(){
		return searchEndpoint;
	}

	public ClearButton getClearButton(){
		return clearButton;
	}

	public Icon getIcon(){
		return icon;
	}

	public PlaceholderText getPlaceholderText(){
		return placeholderText;
	}

	public Config getConfig(){
		return config;
	}

	@Override
 	public String toString(){
		return 
			"FusionSearchboxRenderer{" + 
			"trackingParams = '" + trackingParams + '\'' + 
			",searchEndpoint = '" + searchEndpoint + '\'' + 
			",clearButton = '" + clearButton + '\'' + 
			",icon = '" + icon + '\'' + 
			",placeholderText = '" + placeholderText + '\'' + 
			",config = '" + config + '\'' + 
			"}";
		}
}