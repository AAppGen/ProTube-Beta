package io.awesome.gagtube.models.response.explore;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ConnectionErrorMicrophoneLabel{

	@SerializedName("runs")
	private List<RunsItem> runs;

	public List<RunsItem> getRuns(){
		return runs;
	}

	@Override
 	public String toString(){
		return 
			"ConnectionErrorMicrophoneLabel{" + 
			"runs = '" + runs + '\'' + 
			"}";
		}
}