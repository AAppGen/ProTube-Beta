package io.awesome.gagtube.models.response.explore;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Text{

	@SerializedName("runs")
	private List<RunsItem> runs;

	@SerializedName("simpleText")
	private String simpleText;

	@SerializedName("accessibility")
	private Accessibility accessibility;

	public List<RunsItem> getRuns(){
		return runs;
	}

	public String getSimpleText(){
		return simpleText;
	}

	public Accessibility getAccessibility(){
		return accessibility;
	}

	@Override
 	public String toString(){
		return 
			"Text{" + 
			"runs = '" + runs + '\'' + 
			",simpleText = '" + simpleText + '\'' + 
			",accessibility = '" + accessibility + '\'' + 
			"}";
		}
}