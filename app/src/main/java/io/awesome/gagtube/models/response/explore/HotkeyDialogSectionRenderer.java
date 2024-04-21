package io.awesome.gagtube.models.response.explore;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class HotkeyDialogSectionRenderer{

	@SerializedName("options")
	private List<OptionsItem> options;

	@SerializedName("title")
	private Title title;

	public List<OptionsItem> getOptions(){
		return options;
	}

	public Title getTitle(){
		return title;
	}

	@Override
 	public String toString(){
		return 
			"HotkeyDialogSectionRenderer{" + 
			"options = '" + options + '\'' + 
			",title = '" + title + '\'' + 
			"}";
		}
}