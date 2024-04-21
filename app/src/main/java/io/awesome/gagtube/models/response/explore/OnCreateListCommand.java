package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class OnCreateListCommand{

	@SerializedName("commandMetadata")
	private CommandMetadata commandMetadata;

	@SerializedName("createPlaylistServiceEndpoint")
	private CreatePlaylistServiceEndpoint createPlaylistServiceEndpoint;

	@SerializedName("clickTrackingParams")
	private String clickTrackingParams;

	public CommandMetadata getCommandMetadata(){
		return commandMetadata;
	}

	public CreatePlaylistServiceEndpoint getCreatePlaylistServiceEndpoint(){
		return createPlaylistServiceEndpoint;
	}

	public String getClickTrackingParams(){
		return clickTrackingParams;
	}

	@Override
 	public String toString(){
		return 
			"OnCreateListCommand{" + 
			"commandMetadata = '" + commandMetadata + '\'' + 
			",createPlaylistServiceEndpoint = '" + createPlaylistServiceEndpoint + '\'' + 
			",clickTrackingParams = '" + clickTrackingParams + '\'' + 
			"}";
		}
}