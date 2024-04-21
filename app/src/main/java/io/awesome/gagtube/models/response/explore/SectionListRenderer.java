package io.awesome.gagtube.models.response.explore;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class SectionListRenderer{

	@SerializedName("trackingParams")
	private String trackingParams;

	@SerializedName("targetId")
	private String targetId;

	@SerializedName("contents")
	private List<ContentsItem> contents;

	public String getTrackingParams(){
		return trackingParams;
	}

	public String getTargetId(){
		return targetId;
	}

	public List<ContentsItem> getContents(){
		return contents;
	}

	@Override
 	public String toString(){
		return 
			"SectionListRenderer{" + 
			"trackingParams = '" + trackingParams + '\'' + 
			",targetId = '" + targetId + '\'' + 
			",contents = '" + contents + '\'' + 
			"}";
		}
}