package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class ContentsItem{

	@SerializedName("itemSectionRenderer")
	private ItemSectionRenderer itemSectionRenderer;

	@SerializedName("shelfRenderer")
	private ShelfRenderer shelfRenderer;

	public ItemSectionRenderer getItemSectionRenderer(){
		return itemSectionRenderer;
	}

	public ShelfRenderer getShelfRenderer(){
		return shelfRenderer;
	}

	@Override
 	public String toString(){
		return 
			"ContentsItem{" + 
			"itemSectionRenderer = '" + itemSectionRenderer + '\'' + 
			",shelfRenderer = '" + shelfRenderer + '\'' + 
			"}";
		}
}