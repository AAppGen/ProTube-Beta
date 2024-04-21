package io.awesome.gagtube.models.response.explore;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ResponseContext{

	@SerializedName("serviceTrackingParams")
	private List<ServiceTrackingParamsItem> serviceTrackingParams;

	@SerializedName("webResponseContextExtensionData")
	private WebResponseContextExtensionData webResponseContextExtensionData;

	@SerializedName("maxAgeSeconds")
	private int maxAgeSeconds;

	@SerializedName("visitorData")
	private String visitorData;

	@SerializedName("mainAppWebResponseContext")
	private MainAppWebResponseContext mainAppWebResponseContext;

	public List<ServiceTrackingParamsItem> getServiceTrackingParams(){
		return serviceTrackingParams;
	}

	public WebResponseContextExtensionData getWebResponseContextExtensionData(){
		return webResponseContextExtensionData;
	}

	public int getMaxAgeSeconds(){
		return maxAgeSeconds;
	}

	public String getVisitorData(){
		return visitorData;
	}

	public MainAppWebResponseContext getMainAppWebResponseContext(){
		return mainAppWebResponseContext;
	}

	@Override
 	public String toString(){
		return 
			"ResponseContext{" + 
			"serviceTrackingParams = '" + serviceTrackingParams + '\'' + 
			",webResponseContextExtensionData = '" + webResponseContextExtensionData + '\'' + 
			",maxAgeSeconds = '" + maxAgeSeconds + '\'' + 
			",visitorData = '" + visitorData + '\'' + 
			",mainAppWebResponseContext = '" + mainAppWebResponseContext + '\'' + 
			"}";
		}
}