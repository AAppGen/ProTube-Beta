package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class SignalAction{

	@SerializedName("signal")
	private String signal;

	public String getSignal(){
		return signal;
	}

	@Override
 	public String toString(){
		return 
			"SignalAction{" + 
			"signal = '" + signal + '\'' + 
			"}";
		}
}