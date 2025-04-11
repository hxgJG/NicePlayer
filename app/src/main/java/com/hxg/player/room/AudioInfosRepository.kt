package com.hxg.player.room

import kotlinx.coroutines.flow.Flow

class AudioInfosRepository(private val dao: AudioInfoDao) : AbsAudioInfosRepository {
    override fun getAllItemsStream(): Flow<List<AudioInfo>> {
        return dao.getAllItems()
    }

    override fun getItemStream(id: Int): Flow<AudioInfo?> {
        return dao.getItem(id)
    }

    override suspend fun insertItem(item: AudioInfo) {
        dao.insert(item)
    }

    override suspend fun deleteItem(item: AudioInfo) {
        dao.delete(item)
    }

    override suspend fun updateItem(item: AudioInfo) {
        dao.update(item)
    }
}
