package io.awesome.gagtube.database.stream.dao;

import java.util.ArrayList;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import io.awesome.gagtube.database.BasicDAO;
import io.awesome.gagtube.database.history.model.StreamHistoryEntity;
import io.awesome.gagtube.database.playlist.model.PlaylistStreamEntity;
import io.awesome.gagtube.database.stream.model.StreamEntity;
import io.reactivex.Flowable;

@Dao
public abstract class StreamDAO implements BasicDAO<StreamEntity> {
    @Override
    @Query("SELECT * FROM " + StreamEntity.STREAM_TABLE)
    public abstract Flowable<List<StreamEntity>> getAll();

    @Override
    @Query("DELETE FROM " + StreamEntity.STREAM_TABLE)
    public abstract int deleteAll();

    @Override
    @Query("SELECT * FROM " + StreamEntity.STREAM_TABLE + " WHERE " + StreamEntity.STREAM_SERVICE_ID + " = :serviceId")
    public abstract Flowable<List<StreamEntity>> listByService(int serviceId);

    @Query("SELECT * FROM " + StreamEntity.STREAM_TABLE + " WHERE " +
            StreamEntity.STREAM_URL + " = :url AND " +
            StreamEntity.STREAM_SERVICE_ID + " = :serviceId")
    public abstract Flowable<List<StreamEntity>> getStream(long serviceId, String url);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract void silentInsertAllInternal(final List<StreamEntity> streams);

    @Query("SELECT " + StreamEntity.STREAM_ID + " FROM " + StreamEntity.STREAM_TABLE + " WHERE " +
            StreamEntity.STREAM_URL + " = :url AND " +
            StreamEntity.STREAM_SERVICE_ID + " = :serviceId")
    abstract Long getStreamIdInternal(long serviceId, String url);

    @Transaction
    public long upsert(StreamEntity stream) {
        final Long streamIdCandidate = getStreamIdInternal(stream.getServiceId(), stream.getUrl());

        if (streamIdCandidate == null) {
            return insert(stream);
        } else {
            stream.setUid(streamIdCandidate);
            update(stream);
            return streamIdCandidate;
        }
    }

    @Transaction
    public List<Long> upsertAll(List<StreamEntity> streams) {
        silentInsertAllInternal(streams);

        final List<Long> streamIds = new ArrayList<>(streams.size());
        for (StreamEntity stream : streams) {
            final Long streamId = getStreamIdInternal(stream.getServiceId(), stream.getUrl());
            if (streamId == null) {
                throw new IllegalStateException("StreamID cannot be null just after insertion.");
            }

            streamIds.add(streamId);
            stream.setUid(streamId);
        }

        update(streams);
        return streamIds;
    }

    @Query("DELETE FROM " + StreamEntity.STREAM_TABLE + " WHERE " + StreamEntity.STREAM_ID +
            " NOT IN " +
            "(SELECT DISTINCT " + StreamEntity.STREAM_ID + " FROM " + StreamEntity.STREAM_TABLE +

            " LEFT JOIN " + StreamHistoryEntity.STREAM_HISTORY_TABLE +
            " ON " + StreamEntity.STREAM_ID + " = " +
            StreamHistoryEntity.STREAM_HISTORY_TABLE + "." + StreamHistoryEntity.JOIN_STREAM_ID +

            " LEFT JOIN " + PlaylistStreamEntity.PLAYLIST_STREAM_JOIN_TABLE +
            " ON " + StreamEntity.STREAM_ID + " = " +
            PlaylistStreamEntity.PLAYLIST_STREAM_JOIN_TABLE + "." + PlaylistStreamEntity.JOIN_STREAM_ID +
            ")")
    public abstract int deleteOrphans();
}
