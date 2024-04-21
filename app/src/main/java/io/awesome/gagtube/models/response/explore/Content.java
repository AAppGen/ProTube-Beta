package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class Content{

	@SerializedName("sectionListRenderer")
	private SectionListRenderer sectionListRenderer;

	@SerializedName("expandedShelfContentsRenderer")
	private ExpandedShelfContentsRenderer expandedShelfContentsRenderer;

	public SectionListRenderer getSectionListRenderer(){
		return sectionListRenderer;
	}

	public ExpandedShelfContentsRenderer getExpandedShelfContentsRenderer(){
		return expandedShelfContentsRenderer;
	}

	@Override
 	public String toString(){
		return 
			"Content{" + 
			"sectionListRenderer = '" + sectionListRenderer + '\'' + 
			",expandedShelfContentsRenderer = '" + expandedShelfContentsRenderer + '\'' + 
			"}";
		}
}