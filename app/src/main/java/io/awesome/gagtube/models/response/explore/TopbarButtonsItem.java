package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class TopbarButtonsItem{

	@SerializedName("buttonRenderer")
	private ButtonRenderer buttonRenderer;

	@SerializedName("topbarMenuButtonRenderer")
	private TopbarMenuButtonRenderer topbarMenuButtonRenderer;

	public ButtonRenderer getButtonRenderer(){
		return buttonRenderer;
	}

	public TopbarMenuButtonRenderer getTopbarMenuButtonRenderer(){
		return topbarMenuButtonRenderer;
	}

	@Override
 	public String toString(){
		return 
			"TopbarButtonsItem{" + 
			"buttonRenderer = '" + buttonRenderer + '\'' + 
			",topbarMenuButtonRenderer = '" + topbarMenuButtonRenderer + '\'' + 
			"}";
		}
}