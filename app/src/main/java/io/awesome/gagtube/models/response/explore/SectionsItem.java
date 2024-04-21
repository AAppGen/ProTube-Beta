package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class SectionsItem{

	@SerializedName("multiPageMenuSectionRenderer")
	private MultiPageMenuSectionRenderer multiPageMenuSectionRenderer;

	@SerializedName("hotkeyDialogSectionRenderer")
	private HotkeyDialogSectionRenderer hotkeyDialogSectionRenderer;

	public MultiPageMenuSectionRenderer getMultiPageMenuSectionRenderer(){
		return multiPageMenuSectionRenderer;
	}

	public HotkeyDialogSectionRenderer getHotkeyDialogSectionRenderer(){
		return hotkeyDialogSectionRenderer;
	}

	@Override
 	public String toString(){
		return 
			"SectionsItem{" + 
			"multiPageMenuSectionRenderer = '" + multiPageMenuSectionRenderer + '\'' + 
			",hotkeyDialogSectionRenderer = '" + hotkeyDialogSectionRenderer + '\'' + 
			"}";
		}
}