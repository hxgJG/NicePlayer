package com.hxg.player.room

import kotlinx.coroutines.flow.Flow

interface AbsAudioInfosRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllItemsStream(): Flow<List<AudioInfo>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getItemStream(id: Int): Flow<AudioInfo?>

    /**
     * Insert item in the data source
     */
    suspend fun insertItem(item: AudioInfo)

    /**
     * Delete item from the data source
     */
    suspend fun deleteItem(item: AudioInfo)

    /**
     * Update item in the data source
     */
    suspend fun updateItem(item: AudioInfo)
}
