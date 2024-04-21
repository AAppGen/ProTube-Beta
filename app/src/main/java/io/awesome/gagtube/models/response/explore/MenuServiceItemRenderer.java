package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class MenuServiceItemRenderer{

	@SerializedName("trackingParams")
	private String trackingParams;

	@SerializedName("icon")
	private Icon icon;

	@SerializedName("text")
	private Text text;

	@SerializedName("serviceEndpoint")
	private ServiceEndpoint serviceEndpoint;

	public String getTrackingParams(){
		return trackingParams;
	}

	public Icon getIcon(){
		return icon;
	}

	public Text getText(){
		return text;
	}

	public ServiceEndpoint getServiceEndpoint(){
		return serviceEndpoint;
	}

	@Override
 	public String toString(){
		return 
			"MenuServiceItemRenderer{" + 
			"trackingParams = '" + trackingParams + '\'' + 
			",icon = '" + icon + '\'' + 
			",text = '" + text + '\'' + 
			",serviceEndpoint = '" + serviceEndpoint + '\'' + 
			"}";
		}
}