package io.awesome.gagtube.fragments.discover.adapter;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import io.awesome.gagtube.R;
import io.awesome.gagtube.fragments.discover.TopFragment;

public class TopViewPagerAdapter extends FragmentStatePagerAdapter {
	
	private final Context context;
	
	public TopViewPagerAdapter(FragmentManager fm, Context context) {
		super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
		this.context = context;
	}
	
	@NotNull
	@Override
	public Fragment getItem(int position) {
		
		switch (position) {
			
			case 0:
				return TopFragment.getInstance("4gINGgt5dG1hX2NoYXJ0cw", context.getString(R.string.music));
			
			case 1:
				return TopFragment.getInstance("4gIcGhpnYW1pbmdfY29ycHVzX21vc3RfcG9wdWxhcg", context.getString(R.string.gaming));
			
			case 2:
				return TopFragment.getInstance("4gIKGgh0cmFpbGVycw", context.getString(R.string.films));
			
			default:
				return new Fragment();
		}
	}
	
	@Override
	public int getCount() {
		return 3;
	}
	
	@Nullable
	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
			case 0:
				return context.getString(R.string.music);
			
			case 1:
				return context.getString(R.string.gaming);
			
			case 2:
				return context.getString(R.string.films);
			
			default:
				return "";
		}
	}
}
