package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class BackButton{

	@SerializedName("buttonRenderer")
	private ButtonRenderer buttonRenderer;

	public ButtonRenderer getButtonRenderer(){
		return buttonRenderer;
	}

	@Override
 	public String toString(){
		return 
			"BackButton{" + 
			"buttonRenderer = '" + buttonRenderer + '\'' + 
			"}";
		}
}