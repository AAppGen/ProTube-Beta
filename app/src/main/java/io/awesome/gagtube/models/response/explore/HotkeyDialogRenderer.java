package io.awesome.gagtube.models.response.explore;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class HotkeyDialogRenderer{

	@SerializedName("trackingParams")
	private String trackingParams;

	@SerializedName("dismissButton")
	private DismissButton dismissButton;

	@SerializedName("title")
	private Title title;

	@SerializedName("sections")
	private List<SectionsItem> sections;

	public String getTrackingParams(){
		return trackingParams;
	}

	public DismissButton getDismissButton(){
		return dismissButton;
	}

	public Title getTitle(){
		return title;
	}

	public List<SectionsItem> getSections(){
		return sections;
	}

	@Override
 	public String toString(){
		return 
			"HotkeyDialogRenderer{" + 
			"trackingParams = '" + trackingParams + '\'' + 
			",dismissButton = '" + dismissButton + '\'' + 
			",title = '" + title + '\'' + 
			",sections = '" + sections + '\'' + 
			"}";
		}
}