package io.awesome.gagtube.models.response.explore;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Title{

	@SerializedName("runs")
	private List<RunsItem> runs;

	@SerializedName("accessibility")
	private Accessibility accessibility;

	public List<RunsItem> getRuns(){
		return runs;
	}

	public Accessibility getAccessibility(){
		return accessibility;
	}

	@Override
 	public String toString(){
		return 
			"Title{" + 
			"runs = '" + runs + '\'' + 
			",accessibility = '" + accessibility + '\'' + 
			"}";
		}
}