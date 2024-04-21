package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class Html5PlaybackOnesieConfig{

	@SerializedName("commonConfig")
	private CommonConfig commonConfig;

	public CommonConfig getCommonConfig(){
		return commonConfig;
	}

	@Override
 	public String toString(){
		return 
			"Html5PlaybackOnesieConfig{" + 
			"commonConfig = '" + commonConfig + '\'' + 
			"}";
		}
}