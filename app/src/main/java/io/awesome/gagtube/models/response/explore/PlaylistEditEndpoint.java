package io.awesome.gagtube.models.response.explore;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PlaylistEditEndpoint{

	@SerializedName("playlistId")
	private String playlistId;

	@SerializedName("actions")
	private List<ActionsItem> actions;

	public String getPlaylistId(){
		return playlistId;
	}

	public List<ActionsItem> getActions(){
		return actions;
	}

	@Override
 	public String toString(){
		return 
			"PlaylistEditEndpoint{" + 
			"playlistId = '" + playlistId + '\'' + 
			",actions = '" + actions + '\'' + 
			"}";
		}
}