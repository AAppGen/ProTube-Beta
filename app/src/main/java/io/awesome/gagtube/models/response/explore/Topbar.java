package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class Topbar{

	@SerializedName("desktopTopbarRenderer")
	private DesktopTopbarRenderer desktopTopbarRenderer;

	public DesktopTopbarRenderer getDesktopTopbarRenderer(){
		return desktopTopbarRenderer;
	}

	@Override
 	public String toString(){
		return 
			"Topbar{" + 
			"desktopTopbarRenderer = '" + desktopTopbarRenderer + '\'' + 
			"}";
		}
}