package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class ToggledServiceEndpoint{

	@SerializedName("commandMetadata")
	private CommandMetadata commandMetadata;

	@SerializedName("playlistEditEndpoint")
	private PlaylistEditEndpoint playlistEditEndpoint;

	@SerializedName("clickTrackingParams")
	private String clickTrackingParams;

	public CommandMetadata getCommandMetadata(){
		return commandMetadata;
	}

	public PlaylistEditEndpoint getPlaylistEditEndpoint(){
		return playlistEditEndpoint;
	}

	public String getClickTrackingParams(){
		return clickTrackingParams;
	}

	@Override
 	public String toString(){
		return 
			"ToggledServiceEndpoint{" + 
			"commandMetadata = '" + commandMetadata + '\'' + 
			",playlistEditEndpoint = '" + playlistEditEndpoint + '\'' + 
			",clickTrackingParams = '" + clickTrackingParams + '\'' + 
			"}";
		}
}