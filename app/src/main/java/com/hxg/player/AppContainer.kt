package com.hxg.player

import android.annotation.SuppressLint
import android.content.Context
import com.hxg.player.room.AbsAudioInfosRepository
import com.hxg.player.room.AudioInfosRepository
import com.hxg.player.room.InventoryDatabase

interface AppContainer {
    val audioInfosRepository: AbsAudioInfosRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [AbsAudioInfosRepository]
     */
    override val audioInfosRepository: AbsAudioInfosRepository by lazy {
        AudioInfosRepository(InventoryDatabase.getDatabase(context).audioInfoDao())
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: AppDataContainer? = null

        fun getInstance(context: Context): AppDataContainer {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppDataContainer(context.applicationContext)
                    .also { INSTANCE = it }
            }
        }
    }
}
