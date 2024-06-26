package io.awesome.gagtube.database.playlist.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;
import io.awesome.gagtube.database.BasicDAO;
import io.awesome.gagtube.database.playlist.model.PlaylistRemoteEntity;
import io.reactivex.Flowable;

@Dao
public abstract class PlaylistRemoteDAO implements BasicDAO<PlaylistRemoteEntity> {
    @Override
    @Query("SELECT * FROM " + PlaylistRemoteEntity.REMOTE_PLAYLIST_TABLE)
    public abstract Flowable<List<PlaylistRemoteEntity>> getAll();

    @Override
    @Query("DELETE FROM " + PlaylistRemoteEntity.REMOTE_PLAYLIST_TABLE)
    public abstract int deleteAll();

    @Override
    @Query("SELECT * FROM " + PlaylistRemoteEntity.REMOTE_PLAYLIST_TABLE +
            " WHERE " + PlaylistRemoteEntity.REMOTE_PLAYLIST_SERVICE_ID + " = :serviceId")
    public abstract Flowable<List<PlaylistRemoteEntity>> listByService(int serviceId);

    @Query("SELECT * FROM " + PlaylistRemoteEntity.REMOTE_PLAYLIST_TABLE + " WHERE " +
            PlaylistRemoteEntity.REMOTE_PLAYLIST_URL + " = :url AND " +
            PlaylistRemoteEntity.REMOTE_PLAYLIST_SERVICE_ID + " = :serviceId")
    public abstract Flowable<List<PlaylistRemoteEntity>> getPlaylist(long serviceId, String url);

    @Query("SELECT " + PlaylistRemoteEntity.REMOTE_PLAYLIST_ID + " FROM " + PlaylistRemoteEntity.REMOTE_PLAYLIST_TABLE +
            " WHERE " +
            PlaylistRemoteEntity.REMOTE_PLAYLIST_URL + " = :url AND " + PlaylistRemoteEntity.REMOTE_PLAYLIST_SERVICE_ID + " = :serviceId")
    abstract Long getPlaylistIdInternal(long serviceId, String url);

    @Transaction
    public long upsert(PlaylistRemoteEntity playlist) {
        final Long playlistId = getPlaylistIdInternal(playlist.getServiceId(), playlist.getUrl());

        if (playlistId == null) {
            return insert(playlist);
        } else {
            playlist.setUid(playlistId);
            update(playlist);
            return playlistId;
        }
    }

    @Query("DELETE FROM " + PlaylistRemoteEntity.REMOTE_PLAYLIST_TABLE +
            " WHERE " + PlaylistRemoteEntity.REMOTE_PLAYLIST_ID + " = :playlistId")
    public abstract int deletePlaylist(final long playlistId);
}
