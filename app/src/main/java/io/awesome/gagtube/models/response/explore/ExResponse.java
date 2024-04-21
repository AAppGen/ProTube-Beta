package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class ExResponse{

	@SerializedName("trackingParams")
	private String trackingParams;

	@SerializedName("contents")
	private Contents contents;

	@SerializedName("responseContext")
	private ResponseContext responseContext;

	@SerializedName("header")
	private Header header;

	@SerializedName("topbar")
	private Topbar topbar;

	public String getTrackingParams(){
		return trackingParams;
	}

	public Contents getContents(){
		return contents;
	}

	public ResponseContext getResponseContext(){
		return responseContext;
	}

	public Header getHeader(){
		return header;
	}

	public Topbar getTopbar(){
		return topbar;
	}

	@Override
 	public String toString(){
		return 
			"ExResponse{" + 
			"trackingParams = '" + trackingParams + '\'' + 
			",contents = '" + contents + '\'' + 
			",responseContext = '" + responseContext + '\'' + 
			",header = '" + header + '\'' + 
			",topbar = '" + topbar + '\'' + 
			"}";
		}
}