package io.awesome.gagtube.player;

import android.content.Intent;
import android.view.MenuItem;

import io.awesome.gagtube.R;

public final class PopupPlayerActivity extends ServicePlayerActivity {

    @Override
    public String getTag() {
        return "PopupVideoPlayerActivity";
    }

    @Override
    public String getSupportActionTitle() {
        return getResources().getString(R.string.title_activity_popup_player);
    }

    @Override
    public Intent getBindIntent() {
        return new Intent(this, MainPlayer.class);
    }

    @Override
    public void startPlayerListener() {
        if (player instanceof VideoPlayerImpl) {
            ((VideoPlayerImpl) player).setActivityListener(this);
        }
    }

    @Override
    public void stopPlayerListener() {
        if (player instanceof VideoPlayerImpl) {
            ((VideoPlayerImpl) player).removeActivityListener(this);
        }
    }

    @Override
    public boolean onPlayerOptionSelected(MenuItem item) {
        return false;
    }
}
