package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class UntoggledServiceEndpoint{

	@SerializedName("commandMetadata")
	private CommandMetadata commandMetadata;

	@SerializedName("clickTrackingParams")
	private String clickTrackingParams;

	@SerializedName("signalServiceEndpoint")
	private SignalServiceEndpoint signalServiceEndpoint;

	@SerializedName("playlistEditEndpoint")
	private PlaylistEditEndpoint playlistEditEndpoint;

	public CommandMetadata getCommandMetadata(){
		return commandMetadata;
	}

	public String getClickTrackingParams(){
		return clickTrackingParams;
	}

	public SignalServiceEndpoint getSignalServiceEndpoint(){
		return signalServiceEndpoint;
	}

	public PlaylistEditEndpoint getPlaylistEditEndpoint(){
		return playlistEditEndpoint;
	}

	@Override
 	public String toString(){
		return 
			"UntoggledServiceEndpoint{" + 
			"commandMetadata = '" + commandMetadata + '\'' + 
			",clickTrackingParams = '" + clickTrackingParams + '\'' + 
			",signalServiceEndpoint = '" + signalServiceEndpoint + '\'' + 
			",playlistEditEndpoint = '" + playlistEditEndpoint + '\'' + 
			"}";
		}
}