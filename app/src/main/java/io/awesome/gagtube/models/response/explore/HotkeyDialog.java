package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class HotkeyDialog{

	@SerializedName("hotkeyDialogRenderer")
	private HotkeyDialogRenderer hotkeyDialogRenderer;

	public HotkeyDialogRenderer getHotkeyDialogRenderer(){
		return hotkeyDialogRenderer;
	}

	@Override
 	public String toString(){
		return 
			"HotkeyDialog{" + 
			"hotkeyDialogRenderer = '" + hotkeyDialogRenderer + '\'' + 
			"}";
		}
}