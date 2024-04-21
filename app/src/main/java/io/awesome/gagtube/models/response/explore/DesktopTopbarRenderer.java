package io.awesome.gagtube.models.response.explore;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class DesktopTopbarRenderer{

	@SerializedName("trackingParams")
	private String trackingParams;

	@SerializedName("topbarButtons")
	private List<TopbarButtonsItem> topbarButtons;

	@SerializedName("a11ySkipNavigationButton")
	private A11ySkipNavigationButton a11ySkipNavigationButton;

	@SerializedName("countryCode")
	private String countryCode;

	@SerializedName("hotkeyDialog")
	private HotkeyDialog hotkeyDialog;

	@SerializedName("forwardButton")
	private ForwardButton forwardButton;

	@SerializedName("backButton")
	private BackButton backButton;

	@SerializedName("voiceSearchButton")
	private VoiceSearchButton voiceSearchButton;

	@SerializedName("logo")
	private Logo logo;

	@SerializedName("searchbox")
	private Searchbox searchbox;

	public String getTrackingParams(){
		return trackingParams;
	}

	public List<TopbarButtonsItem> getTopbarButtons(){
		return topbarButtons;
	}

	public A11ySkipNavigationButton getA11ySkipNavigationButton(){
		return a11ySkipNavigationButton;
	}

	public String getCountryCode(){
		return countryCode;
	}

	public HotkeyDialog getHotkeyDialog(){
		return hotkeyDialog;
	}

	public ForwardButton getForwardButton(){
		return forwardButton;
	}

	public BackButton getBackButton(){
		return backButton;
	}

	public VoiceSearchButton getVoiceSearchButton(){
		return voiceSearchButton;
	}

	public Logo getLogo(){
		return logo;
	}

	public Searchbox getSearchbox(){
		return searchbox;
	}

	@Override
 	public String toString(){
		return 
			"DesktopTopbarRenderer{" + 
			"trackingParams = '" + trackingParams + '\'' + 
			",topbarButtons = '" + topbarButtons + '\'' + 
			",a11ySkipNavigationButton = '" + a11ySkipNavigationButton + '\'' + 
			",countryCode = '" + countryCode + '\'' + 
			",hotkeyDialog = '" + hotkeyDialog + '\'' + 
			",forwardButton = '" + forwardButton + '\'' + 
			",backButton = '" + backButton + '\'' + 
			",voiceSearchButton = '" + voiceSearchButton + '\'' + 
			",logo = '" + logo + '\'' + 
			",searchbox = '" + searchbox + '\'' + 
			"}";
		}
}