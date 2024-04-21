package io.awesome.gagtube.models.request.explore;

import com.google.gson.annotations.SerializedName;

import io.awesome.gagtube.models.request.explore.Context;

public class ExploreRequest {

	@SerializedName("browseId")
	public String browseId;

	@SerializedName("context")
	public Context context;

	@SerializedName("params")
	public String params;
}