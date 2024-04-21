package io.awesome.gagtube.models.response.explore;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class SignalServiceEndpoint{

	@SerializedName("signal")
	private String signal;

	@SerializedName("actions")
	private List<ActionsItem> actions;

	public String getSignal(){
		return signal;
	}

	public List<ActionsItem> getActions(){
		return actions;
	}

	@Override
 	public String toString(){
		return 
			"SignalServiceEndpoint{" + 
			"signal = '" + signal + '\'' + 
			",actions = '" + actions + '\'' + 
			"}";
		}
}