package io.awesome.gagtube.models.response.explore;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Thumbnail{

	@SerializedName("thumbnails")
	private List<ThumbnailsItem> thumbnails;

	public List<ThumbnailsItem> getThumbnails(){
		return thumbnails;
	}

	@Override
 	public String toString(){
		return 
			"Thumbnail{" + 
			"thumbnails = '" + thumbnails + '\'' + 
			"}";
		}
}