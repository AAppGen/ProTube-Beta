package io.awesome.gagtube.player.event;

import io.awesome.gagtube.player.MainPlayer;
import io.awesome.gagtube.player.VideoPlayerImpl;

public interface PlayerServiceExtendedEventListener extends PlayerServiceEventListener {

    void onServiceConnected(VideoPlayerImpl player, MainPlayer playerService, boolean playAfterConnect);

    void onServiceDisconnected();
}
