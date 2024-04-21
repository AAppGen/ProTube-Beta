package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class SearchEndpoint{

	@SerializedName("commandMetadata")
	private CommandMetadata commandMetadata;

	@SerializedName("searchEndpoint")
	private SearchEndpoint searchEndpoint;

	@SerializedName("clickTrackingParams")
	private String clickTrackingParams;

	@SerializedName("query")
	private String query;

	public CommandMetadata getCommandMetadata(){
		return commandMetadata;
	}

	public SearchEndpoint getSearchEndpoint(){
		return searchEndpoint;
	}

	public String getClickTrackingParams(){
		return clickTrackingParams;
	}

	public String getQuery(){
		return query;
	}

	@Override
 	public String toString(){
		return 
			"SearchEndpoint{" + 
			"commandMetadata = '" + commandMetadata + '\'' + 
			",searchEndpoint = '" + searchEndpoint + '\'' + 
			",clickTrackingParams = '" + clickTrackingParams + '\'' + 
			",query = '" + query + '\'' + 
			"}";
		}
}