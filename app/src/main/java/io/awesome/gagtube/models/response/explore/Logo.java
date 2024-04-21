package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class Logo{

	@SerializedName("topbarLogoRenderer")
	private TopbarLogoRenderer topbarLogoRenderer;

	public TopbarLogoRenderer getTopbarLogoRenderer(){
		return topbarLogoRenderer;
	}

	@Override
 	public String toString(){
		return 
			"Logo{" + 
			"topbarLogoRenderer = '" + topbarLogoRenderer + '\'' + 
			"}";
		}
}