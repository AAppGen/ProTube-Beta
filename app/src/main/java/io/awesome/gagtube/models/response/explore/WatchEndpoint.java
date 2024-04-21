package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class WatchEndpoint{

	@SerializedName("watchEndpointSupportedOnesieConfig")
	private WatchEndpointSupportedOnesieConfig watchEndpointSupportedOnesieConfig;

	@SerializedName("videoId")
	private String videoId;

	public WatchEndpointSupportedOnesieConfig getWatchEndpointSupportedOnesieConfig(){
		return watchEndpointSupportedOnesieConfig;
	}

	public String getVideoId(){
		return videoId;
	}

	@Override
 	public String toString(){
		return 
			"WatchEndpoint{" + 
			"watchEndpointSupportedOnesieConfig = '" + watchEndpointSupportedOnesieConfig + '\'' + 
			",videoId = '" + videoId + '\'' + 
			"}";
		}
}