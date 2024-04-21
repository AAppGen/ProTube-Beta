package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class WatchEndpointSupportedOnesieConfig{

	@SerializedName("html5PlaybackOnesieConfig")
	private Html5PlaybackOnesieConfig html5PlaybackOnesieConfig;

	public Html5PlaybackOnesieConfig getHtml5PlaybackOnesieConfig(){
		return html5PlaybackOnesieConfig;
	}

	@Override
 	public String toString(){
		return 
			"WatchEndpointSupportedOnesieConfig{" + 
			"html5PlaybackOnesieConfig = '" + html5PlaybackOnesieConfig + '\'' + 
			"}";
		}
}