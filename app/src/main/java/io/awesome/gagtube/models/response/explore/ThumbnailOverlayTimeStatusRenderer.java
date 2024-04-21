package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class ThumbnailOverlayTimeStatusRenderer{

	@SerializedName("style")
	private String style;

	@SerializedName("text")
	private Text text;

	public String getStyle(){
		return style;
	}

	public Text getText(){
		return text;
	}

	@Override
 	public String toString(){
		return 
			"ThumbnailOverlayTimeStatusRenderer{" + 
			"style = '" + style + '\'' + 
			",text = '" + text + '\'' + 
			"}";
		}
}