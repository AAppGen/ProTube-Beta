package io.awesome.gagtube.models.response.explore;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ServiceTrackingParamsItem{

	@SerializedName("service")
	private String service;

	@SerializedName("params")
	private List<ParamsItem> params;

	public String getService(){
		return service;
	}

	public List<ParamsItem> getParams(){
		return params;
	}

	@Override
 	public String toString(){
		return 
			"ServiceTrackingParamsItem{" + 
			"service = '" + service + '\'' + 
			",params = '" + params + '\'' + 
			"}";
		}
}