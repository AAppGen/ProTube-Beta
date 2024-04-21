package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class Popup{

	@SerializedName("multiPageMenuRenderer")
	private MultiPageMenuRenderer multiPageMenuRenderer;

	@SerializedName("voiceSearchDialogRenderer")
	private VoiceSearchDialogRenderer voiceSearchDialogRenderer;

	public MultiPageMenuRenderer getMultiPageMenuRenderer(){
		return multiPageMenuRenderer;
	}

	public VoiceSearchDialogRenderer getVoiceSearchDialogRenderer(){
		return voiceSearchDialogRenderer;
	}

	@Override
 	public String toString(){
		return 
			"Popup{" + 
			"multiPageMenuRenderer = '" + multiPageMenuRenderer + '\'' + 
			",voiceSearchDialogRenderer = '" + voiceSearchDialogRenderer + '\'' + 
			"}";
		}
}