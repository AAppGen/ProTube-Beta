package io.awesome.gagtube.models.response.explore;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class AddToPlaylistCommand{

	@SerializedName("onCreateListCommand")
	private OnCreateListCommand onCreateListCommand;

	@SerializedName("videoId")
	private String videoId;

	@SerializedName("openMiniplayer")
	private boolean openMiniplayer;

	@SerializedName("listType")
	private String listType;

	@SerializedName("videoIds")
	private List<String> videoIds;

	public OnCreateListCommand getOnCreateListCommand(){
		return onCreateListCommand;
	}

	public String getVideoId(){
		return videoId;
	}

	public boolean isOpenMiniplayer(){
		return openMiniplayer;
	}

	public String getListType(){
		return listType;
	}

	public List<String> getVideoIds(){
		return videoIds;
	}

	@Override
 	public String toString(){
		return 
			"AddToPlaylistCommand{" + 
			"onCreateListCommand = '" + onCreateListCommand + '\'' + 
			",videoId = '" + videoId + '\'' + 
			",openMiniplayer = '" + openMiniplayer + '\'' + 
			",listType = '" + listType + '\'' + 
			",videoIds = '" + videoIds + '\'' + 
			"}";
		}
}