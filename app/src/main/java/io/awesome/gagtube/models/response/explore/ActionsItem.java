package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class ActionsItem{

	@SerializedName("clickTrackingParams")
	private String clickTrackingParams;

	@SerializedName("addToPlaylistCommand")
	private AddToPlaylistCommand addToPlaylistCommand;

	@SerializedName("addedVideoId")
	private String addedVideoId;

	@SerializedName("action")
	private String action;

	@SerializedName("removedVideoId")
	private String removedVideoId;

	@SerializedName("openPopupAction")
	private OpenPopupAction openPopupAction;

	@SerializedName("signalAction")
	private SignalAction signalAction;

	public String getClickTrackingParams(){
		return clickTrackingParams;
	}

	public AddToPlaylistCommand getAddToPlaylistCommand(){
		return addToPlaylistCommand;
	}

	public String getAddedVideoId(){
		return addedVideoId;
	}

	public String getAction(){
		return action;
	}

	public String getRemovedVideoId(){
		return removedVideoId;
	}

	public OpenPopupAction getOpenPopupAction(){
		return openPopupAction;
	}

	public SignalAction getSignalAction(){
		return signalAction;
	}

	@Override
 	public String toString(){
		return 
			"ActionsItem{" + 
			"clickTrackingParams = '" + clickTrackingParams + '\'' + 
			",addToPlaylistCommand = '" + addToPlaylistCommand + '\'' + 
			",addedVideoId = '" + addedVideoId + '\'' + 
			",action = '" + action + '\'' + 
			",removedVideoId = '" + removedVideoId + '\'' + 
			",openPopupAction = '" + openPopupAction + '\'' + 
			",signalAction = '" + signalAction + '\'' + 
			"}";
		}
}