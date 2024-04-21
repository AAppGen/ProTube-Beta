package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class OpenPopupAction{

	@SerializedName("popupType")
	private String popupType;

	@SerializedName("popup")
	private Popup popup;

	@SerializedName("beReused")
	private boolean beReused;

	public String getPopupType(){
		return popupType;
	}

	public Popup getPopup(){
		return popup;
	}

	public boolean isBeReused(){
		return beReused;
	}

	@Override
 	public String toString(){
		return 
			"OpenPopupAction{" + 
			"popupType = '" + popupType + '\'' + 
			",popup = '" + popup + '\'' + 
			",beReused = '" + beReused + '\'' + 
			"}";
		}
}