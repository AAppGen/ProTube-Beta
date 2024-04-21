package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class ChannelThumbnailSupportedRenderers{

	@SerializedName("channelThumbnailWithLinkRenderer")
	private ChannelThumbnailWithLinkRenderer channelThumbnailWithLinkRenderer;

	public ChannelThumbnailWithLinkRenderer getChannelThumbnailWithLinkRenderer(){
		return channelThumbnailWithLinkRenderer;
	}

	@Override
 	public String toString(){
		return 
			"ChannelThumbnailSupportedRenderers{" + 
			"channelThumbnailWithLinkRenderer = '" + channelThumbnailWithLinkRenderer + '\'' + 
			"}";
		}
}