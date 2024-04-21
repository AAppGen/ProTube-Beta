package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class Endpoint{

	@SerializedName("commandMetadata")
	private CommandMetadata commandMetadata;

	@SerializedName("clickTrackingParams")
	private String clickTrackingParams;

	@SerializedName("browseEndpoint")
	private BrowseEndpoint browseEndpoint;

	public CommandMetadata getCommandMetadata(){
		return commandMetadata;
	}

	public String getClickTrackingParams(){
		return clickTrackingParams;
	}

	public BrowseEndpoint getBrowseEndpoint(){
		return browseEndpoint;
	}

	@Override
 	public String toString(){
		return 
			"Endpoint{" + 
			"commandMetadata = '" + commandMetadata + '\'' + 
			",clickTrackingParams = '" + clickTrackingParams + '\'' + 
			",browseEndpoint = '" + browseEndpoint + '\'' + 
			"}";
		}
}