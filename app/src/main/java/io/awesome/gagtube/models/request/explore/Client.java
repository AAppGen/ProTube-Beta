package io.awesome.gagtube.models.request.explore;

import com.google.gson.annotations.SerializedName;

public class Client{

	@SerializedName("hl")
	public String hl;

	@SerializedName("gl")
	public String gl;

	@SerializedName("clientName")
	public String clientName;

	@SerializedName("mainAppWebInfo")
	public MainAppWebInfo mainAppWebInfo;

	@SerializedName("userAgent")
	public String userAgent;

	@SerializedName("clientVersion")
	public String clientVersion;
}