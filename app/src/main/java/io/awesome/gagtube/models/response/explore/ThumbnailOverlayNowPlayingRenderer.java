package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class ThumbnailOverlayNowPlayingRenderer{

	@SerializedName("text")
	private Text text;

	public Text getText(){
		return text;
	}

	@Override
 	public String toString(){
		return 
			"ThumbnailOverlayNowPlayingRenderer{" + 
			"text = '" + text + '\'' + 
			"}";
		}
}