package io.awesome.gagtube.models.response.explore;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ItemSectionRenderer{

	@SerializedName("trackingParams")
	private String trackingParams;

	@SerializedName("contents")
	private List<ContentsItem> contents;

	public String getTrackingParams(){
		return trackingParams;
	}

	public List<ContentsItem> getContents(){
		return contents;
	}

	@Override
 	public String toString(){
		return 
			"ItemSectionRenderer{" + 
			"trackingParams = '" + trackingParams + '\'' + 
			",contents = '" + contents + '\'' + 
			"}";
		}
}