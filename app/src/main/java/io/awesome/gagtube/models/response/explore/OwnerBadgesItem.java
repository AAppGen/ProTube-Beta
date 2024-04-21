package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class OwnerBadgesItem{

	@SerializedName("metadataBadgeRenderer")
	private MetadataBadgeRenderer metadataBadgeRenderer;

	public MetadataBadgeRenderer getMetadataBadgeRenderer(){
		return metadataBadgeRenderer;
	}

	@Override
 	public String toString(){
		return 
			"OwnerBadgesItem{" + 
			"metadataBadgeRenderer = '" + metadataBadgeRenderer + '\'' + 
			"}";
		}
}