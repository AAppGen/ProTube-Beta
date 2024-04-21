package io.awesome.gagtube.models.response.explore;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class MenuRenderer{

	@SerializedName("trackingParams")
	private String trackingParams;

	@SerializedName("accessibility")
	private Accessibility accessibility;

	@SerializedName("items")
	private List<ItemsItem> items;

	@SerializedName("multiPageMenuRenderer")
	private MultiPageMenuRenderer multiPageMenuRenderer;

	public String getTrackingParams(){
		return trackingParams;
	}

	public Accessibility getAccessibility(){
		return accessibility;
	}

	public List<ItemsItem> getItems(){
		return items;
	}

	public MultiPageMenuRenderer getMultiPageMenuRenderer(){
		return multiPageMenuRenderer;
	}

	@Override
 	public String toString(){
		return 
			"MenuRenderer{" + 
			"trackingParams = '" + trackingParams + '\'' + 
			",accessibility = '" + accessibility + '\'' + 
			",items = '" + items + '\'' + 
			",multiPageMenuRenderer = '" + multiPageMenuRenderer + '\'' + 
			"}";
		}
}