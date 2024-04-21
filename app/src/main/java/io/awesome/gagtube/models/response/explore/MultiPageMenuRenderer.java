package io.awesome.gagtube.models.response.explore;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class MultiPageMenuRenderer{

	@SerializedName("trackingParams")
	private String trackingParams;

	@SerializedName("style")
	private String style;

	@SerializedName("sections")
	private List<SectionsItem> sections;

	@SerializedName("showLoadingSpinner")
	private boolean showLoadingSpinner;

	public String getTrackingParams(){
		return trackingParams;
	}

	public String getStyle(){
		return style;
	}

	public List<SectionsItem> getSections(){
		return sections;
	}

	public boolean isShowLoadingSpinner(){
		return showLoadingSpinner;
	}

	@Override
 	public String toString(){
		return 
			"MultiPageMenuRenderer{" + 
			"trackingParams = '" + trackingParams + '\'' + 
			",style = '" + style + '\'' + 
			",sections = '" + sections + '\'' + 
			",showLoadingSpinner = '" + showLoadingSpinner + '\'' + 
			"}";
		}
}