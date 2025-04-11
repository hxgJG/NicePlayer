package com.hxg.player.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioInfoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: AudioInfo)

    @Update
    suspend fun update(item: AudioInfo)

    @Delete
    suspend fun delete(item: AudioInfo)

    @Query("SELECT * from audio_infos WHERE id = :id")
    fun getItem(id: Int): Flow<AudioInfo>

    @Query("SELECT * from audio_infos ORDER BY title ASC")
    fun getAllItems(): Flow<List<AudioInfo>>
}
