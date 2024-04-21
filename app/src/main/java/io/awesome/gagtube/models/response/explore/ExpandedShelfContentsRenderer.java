package io.awesome.gagtube.models.response.explore;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ExpandedShelfContentsRenderer{

	@SerializedName("items")
	private List<ItemsItem> items;

	public List<ItemsItem> getItems(){
		return items;
	}

	@Override
 	public String toString(){
		return 
			"ExpandedShelfContentsRenderer{" + 
			"items = '" + items + '\'' + 
			"}";
		}
}