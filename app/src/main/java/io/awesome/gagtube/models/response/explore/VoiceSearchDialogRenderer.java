package io.awesome.gagtube.models.response.explore;

import com.google.gson.annotations.SerializedName;

public class VoiceSearchDialogRenderer{

	@SerializedName("microphoneButtonAriaLabel")
	private MicrophoneButtonAriaLabel microphoneButtonAriaLabel;

	@SerializedName("promptMicrophoneLabel")
	private PromptMicrophoneLabel promptMicrophoneLabel;

	@SerializedName("promptHeader")
	private PromptHeader promptHeader;

	@SerializedName("connectionErrorMicrophoneLabel")
	private ConnectionErrorMicrophoneLabel connectionErrorMicrophoneLabel;

	@SerializedName("disabledSubtext")
	private DisabledSubtext disabledSubtext;

	@SerializedName("permissionsHeader")
	private PermissionsHeader permissionsHeader;

	@SerializedName("exampleQuery1")
	private ExampleQuery1 exampleQuery1;

	@SerializedName("exitButton")
	private ExitButton exitButton;

	@SerializedName("trackingParams")
	private String trackingParams;

	@SerializedName("microphoneOffPromptHeader")
	private MicrophoneOffPromptHeader microphoneOffPromptHeader;

	@SerializedName("exampleQuery2")
	private ExampleQuery2 exampleQuery2;

	@SerializedName("connectionErrorHeader")
	private ConnectionErrorHeader connectionErrorHeader;

	@SerializedName("placeholderHeader")
	private PlaceholderHeader placeholderHeader;

	@SerializedName("disabledHeader")
	private DisabledHeader disabledHeader;

	@SerializedName("loadingHeader")
	private LoadingHeader loadingHeader;

	@SerializedName("permissionsSubtext")
	private PermissionsSubtext permissionsSubtext;

	public MicrophoneButtonAriaLabel getMicrophoneButtonAriaLabel(){
		return microphoneButtonAriaLabel;
	}

	public PromptMicrophoneLabel getPromptMicrophoneLabel(){
		return promptMicrophoneLabel;
	}

	public PromptHeader getPromptHeader(){
		return promptHeader;
	}

	public ConnectionErrorMicrophoneLabel getConnectionErrorMicrophoneLabel(){
		return connectionErrorMicrophoneLabel;
	}

	public DisabledSubtext getDisabledSubtext(){
		return disabledSubtext;
	}

	public PermissionsHeader getPermissionsHeader(){
		return permissionsHeader;
	}

	public ExampleQuery1 getExampleQuery1(){
		return exampleQuery1;
	}

	public ExitButton getExitButton(){
		return exitButton;
	}

	public String getTrackingParams(){
		return trackingParams;
	}

	public MicrophoneOffPromptHeader getMicrophoneOffPromptHeader(){
		return microphoneOffPromptHeader;
	}

	public ExampleQuery2 getExampleQuery2(){
		return exampleQuery2;
	}

	public ConnectionErrorHeader getConnectionErrorHeader(){
		return connectionErrorHeader;
	}

	public PlaceholderHeader getPlaceholderHeader(){
		return placeholderHeader;
	}

	public DisabledHeader getDisabledHeader(){
		return disabledHeader;
	}

	public LoadingHeader getLoadingHeader(){
		return loadingHeader;
	}

	public PermissionsSubtext getPermissionsSubtext(){
		return permissionsSubtext;
	}

	@Override
 	public String toString(){
		return 
			"VoiceSearchDialogRenderer{" + 
			"microphoneButtonAriaLabel = '" + microphoneButtonAriaLabel + '\'' + 
			",promptMicrophoneLabel = '" + promptMicrophoneLabel + '\'' + 
			",promptHeader = '" + promptHeader + '\'' + 
			",connectionErrorMicrophoneLabel = '" + connectionErrorMicrophoneLabel + '\'' + 
			",disabledSubtext = '" + disabledSubtext + '\'' + 
			",permissionsHeader = '" + permissionsHeader + '\'' + 
			",exampleQuery1 = '" + exampleQuery1 + '\'' + 
			",exitButton = '" + exitButton + '\'' + 
			",trackingParams = '" + trackingParams + '\'' + 
			",microphoneOffPromptHeader = '" + microphoneOffPromptHeader + '\'' + 
			",exampleQuery2 = '" + exampleQuery2 + '\'' + 
			",connectionErrorHeader = '" + connectionErrorHeader + '\'' + 
			",placeholderHeader = '" + placeholderHeader + '\'' + 
			",disabledHeader = '" + disabledHeader + '\'' + 
			",loadingHeader = '" + loadingHeader + '\'' + 
			",permissionsSubtext = '" + permissionsSubtext + '\'' + 
			"}";
		}
}