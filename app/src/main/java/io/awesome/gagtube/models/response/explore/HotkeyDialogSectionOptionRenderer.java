package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class HotkeyDialogSectionOptionRenderer{

	@SerializedName("hotkey")
	private String hotkey;

	@SerializedName("label")
	private Label label;

	@SerializedName("hotkeyAccessibilityLabel")
	private HotkeyAccessibilityLabel hotkeyAccessibilityLabel;

	public String getHotkey(){
		return hotkey;
	}

	public Label getLabel(){
		return label;
	}

	public HotkeyAccessibilityLabel getHotkeyAccessibilityLabel(){
		return hotkeyAccessibilityLabel;
	}

	@Override
 	public String toString(){
		return 
			"HotkeyDialogSectionOptionRenderer{" + 
			"hotkey = '" + hotkey + '\'' + 
			",label = '" + label + '\'' + 
			",hotkeyAccessibilityLabel = '" + hotkeyAccessibilityLabel + '\'' + 
			"}";
		}
}