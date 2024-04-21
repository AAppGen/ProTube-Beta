package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class SignInEndpoint{

	@SerializedName("idamTag")
	private String idamTag;

	public String getIdamTag(){
		return idamTag;
	}

	@Override
 	public String toString(){
		return 
			"SignInEndpoint{" + 
			"idamTag = '" + idamTag + '\'' + 
			"}";
		}
}