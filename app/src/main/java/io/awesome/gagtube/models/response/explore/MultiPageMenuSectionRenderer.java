package io.awesome.gagtube.models.response.explore;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class MultiPageMenuSectionRenderer{

	@SerializedName("trackingParams")
	private String trackingParams;

	@SerializedName("items")
	private List<ItemsItem> items;

	public String getTrackingParams(){
		return trackingParams;
	}

	public List<ItemsItem> getItems(){
		return items;
	}

	@Override
 	public String toString(){
		return 
			"MultiPageMenuSectionRenderer{" + 
			"trackingParams = '" + trackingParams + '\'' + 
			",items = '" + items + '\'' + 
			"}";
		}
}