package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class Searchbox{

	@SerializedName("fusionSearchboxRenderer")
	private FusionSearchboxRenderer fusionSearchboxRenderer;

	public FusionSearchboxRenderer getFusionSearchboxRenderer(){
		return fusionSearchboxRenderer;
	}

	@Override
 	public String toString(){
		return 
			"Searchbox{" + 
			"fusionSearchboxRenderer = '" + fusionSearchboxRenderer + '\'' + 
			"}";
		}
}