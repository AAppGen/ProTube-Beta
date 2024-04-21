package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class VoiceSearchButton{

	@SerializedName("buttonRenderer")
	private ButtonRenderer buttonRenderer;

	public ButtonRenderer getButtonRenderer(){
		return buttonRenderer;
	}

	@Override
 	public String toString(){
		return 
			"VoiceSearchButton{" + 
			"buttonRenderer = '" + buttonRenderer + '\'' + 
			"}";
		}
}