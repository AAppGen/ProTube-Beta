package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class ThumbnailOverlaysItem{

	@SerializedName("thumbnailOverlayNowPlayingRenderer")
	private ThumbnailOverlayNowPlayingRenderer thumbnailOverlayNowPlayingRenderer;

	@SerializedName("thumbnailOverlayToggleButtonRenderer")
	private ThumbnailOverlayToggleButtonRenderer thumbnailOverlayToggleButtonRenderer;

	@SerializedName("thumbnailOverlayTimeStatusRenderer")
	private ThumbnailOverlayTimeStatusRenderer thumbnailOverlayTimeStatusRenderer;

	public ThumbnailOverlayNowPlayingRenderer getThumbnailOverlayNowPlayingRenderer(){
		return thumbnailOverlayNowPlayingRenderer;
	}

	public ThumbnailOverlayToggleButtonRenderer getThumbnailOverlayToggleButtonRenderer(){
		return thumbnailOverlayToggleButtonRenderer;
	}

	public ThumbnailOverlayTimeStatusRenderer getThumbnailOverlayTimeStatusRenderer(){
		return thumbnailOverlayTimeStatusRenderer;
	}

	@Override
 	public String toString(){
		return 
			"ThumbnailOverlaysItem{" + 
			"thumbnailOverlayNowPlayingRenderer = '" + thumbnailOverlayNowPlayingRenderer + '\'' + 
			",thumbnailOverlayToggleButtonRenderer = '" + thumbnailOverlayToggleButtonRenderer + '\'' + 
			",thumbnailOverlayTimeStatusRenderer = '" + thumbnailOverlayTimeStatusRenderer + '\'' + 
			"}";
		}
}