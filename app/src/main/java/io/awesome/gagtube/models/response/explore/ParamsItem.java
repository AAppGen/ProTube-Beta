package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class ParamsItem{

	@SerializedName("value")
	private String value;

	@SerializedName("key")
	private String key;

	public String getValue(){
		return value;
	}

	public String getKey(){
		return key;
	}

	@Override
 	public String toString(){
		return 
			"ParamsItem{" + 
			"value = '" + value + '\'' + 
			",key = '" + key + '\'' + 
			"}";
		}
}