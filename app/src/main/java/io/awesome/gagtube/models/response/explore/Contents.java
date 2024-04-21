package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class Contents{

	@SerializedName("twoColumnBrowseResultsRenderer")
	private TwoColumnBrowseResultsRenderer twoColumnBrowseResultsRenderer;

	public TwoColumnBrowseResultsRenderer getTwoColumnBrowseResultsRenderer(){
		return twoColumnBrowseResultsRenderer;
	}

	@Override
 	public String toString(){
		return 
			"Contents{" + 
			"twoColumnBrowseResultsRenderer = '" + twoColumnBrowseResultsRenderer + '\'' + 
			"}";
		}
}