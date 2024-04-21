package io.awesome.gagtube.models.response.explore;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class CreatePlaylistServiceEndpoint{

	@SerializedName("params")
	private String params;

	@SerializedName("videoIds")
	private List<String> videoIds;

	public String getParams(){
		return params;
	}

	public List<String> getVideoIds(){
		return videoIds;
	}

	@Override
 	public String toString(){
		return 
			"CreatePlaylistServiceEndpoint{" + 
			"params = '" + params + '\'' + 
			",videoIds = '" + videoIds + '\'' + 
			"}";
		}
}