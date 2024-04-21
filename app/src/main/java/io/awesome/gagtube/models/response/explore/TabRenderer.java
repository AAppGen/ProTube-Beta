package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class TabRenderer{

	@SerializedName("endpoint")
	private Endpoint endpoint;

	@SerializedName("trackingParams")
	private String trackingParams;

	@SerializedName("accessibility")
	private Accessibility accessibility;

	@SerializedName("tabIdentifier")
	private String tabIdentifier;

	@SerializedName("title")
	private String title;

	@SerializedName("selected")
	private boolean selected;

	@SerializedName("content")
	private Content content;

	public Endpoint getEndpoint(){
		return endpoint;
	}

	public String getTrackingParams(){
		return trackingParams;
	}

	public Accessibility getAccessibility(){
		return accessibility;
	}

	public String getTabIdentifier(){
		return tabIdentifier;
	}

	public String getTitle(){
		return title;
	}

	public boolean isSelected(){
		return selected;
	}

	public Content getContent(){
		return content;
	}

	@Override
 	public String toString(){
		return 
			"TabRenderer{" + 
			"endpoint = '" + endpoint + '\'' + 
			",trackingParams = '" + trackingParams + '\'' + 
			",accessibility = '" + accessibility + '\'' + 
			",tabIdentifier = '" + tabIdentifier + '\'' + 
			",title = '" + title + '\'' + 
			",selected = '" + selected + '\'' + 
			",content = '" + content + '\'' + 
			"}";
		}
}