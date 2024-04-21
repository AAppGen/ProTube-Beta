package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class NavigationEndpoint{

	@SerializedName("commandMetadata")
	private CommandMetadata commandMetadata;

	@SerializedName("clickTrackingParams")
	private String clickTrackingParams;

	@SerializedName("watchEndpoint")
	private WatchEndpoint watchEndpoint;

	@SerializedName("browseEndpoint")
	private BrowseEndpoint browseEndpoint;

	@SerializedName("signInEndpoint")
	private SignInEndpoint signInEndpoint;

	@SerializedName("urlEndpoint")
	private UrlEndpoint urlEndpoint;

	public CommandMetadata getCommandMetadata(){
		return commandMetadata;
	}

	public String getClickTrackingParams(){
		return clickTrackingParams;
	}

	public WatchEndpoint getWatchEndpoint(){
		return watchEndpoint;
	}

	public BrowseEndpoint getBrowseEndpoint(){
		return browseEndpoint;
	}

	public SignInEndpoint getSignInEndpoint(){
		return signInEndpoint;
	}

	public UrlEndpoint getUrlEndpoint(){
		return urlEndpoint;
	}

	@Override
 	public String toString(){
		return 
			"NavigationEndpoint{" + 
			"commandMetadata = '" + commandMetadata + '\'' + 
			",clickTrackingParams = '" + clickTrackingParams + '\'' + 
			",watchEndpoint = '" + watchEndpoint + '\'' + 
			",browseEndpoint = '" + browseEndpoint + '\'' + 
			",signInEndpoint = '" + signInEndpoint + '\'' + 
			",urlEndpoint = '" + urlEndpoint + '\'' + 
			"}";
		}
}