package com.hxg.player

import android.content.Context
import android.database.Cursor
import android.media.MediaScannerConnection
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.runtime.mutableStateListOf
import com.hxg.player.entity.AudioFile
import com.hxg.player.util.mainScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object FindAudioFiles {
    private var mAudioFiles: MutableList<AudioFile> = mutableStateListOf()

    fun getAudioFiles(): MutableList<AudioFile> {
        return mAudioFiles
    }

    fun queryAudioFiles() {
        CoroutineScope(Dispatchers.IO).launch {
            val context = App.context
            val dataContainer = AppDataContainer.getInstance(context)
            dataContainer.audioInfosRepository.getAllItemsStream().collect { list ->
                mAudioFiles.clear()
                mAudioFiles.addAll(
                    list.map { info ->
                        info.toAudioFile()
                    }
                )
                if (mAudioFiles.isEmpty()) {
                    queryAudioFiles(context, dataContainer, false)
                }
            }
        }
    }

    private suspend fun queryAudioFiles(context: Context, dataContainer: AppDataContainer, isAfterScan: Boolean) {
        // 获取 ContentResolver 实例
        val contentResolver = context.contentResolver

        // 定义查询的 URI 和列
        val audioUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )

        // 执行查询
        val cursor: Cursor? = contentResolver.query(audioUri, projection, null, null, null)

        cursor?.use {
            // 获取列索引
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val albumIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            val list = mutableListOf<AudioFile>()
            // 遍历 Cursor
            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val title = it.getString(titleColumn)
                val artist = it.getString(artistColumn)
                val album = it.getString(albumColumn)
                val duration = it.getInt(durationColumn)
                val size = it.getLong(sizeColumn)
                val data = it.getString(dataColumn)
                val albumId = it.getLong(albumIdColumn)

                // 音频文件信息
                val audioFile = AudioFile(id, title, artist, album, duration, size, data, albumId)
                list.add(audioFile)
                dataContainer.audioInfosRepository.insertItem(audioFile.toAudioInfo())
            }

            if (mAudioFiles.isEmpty() && !isAfterScan) {
                MediaScannerConnection.scanFile(context, arrayOf(audioUri.path), null) { _, _ ->
                    println("[hxg] scanFile -- thread: ${Thread.currentThread().name}")
                    CoroutineScope(Dispatchers.IO).launch {
                        queryAudioFiles(context, dataContainer, true)
                    }
                }
            } else {
                mainScope.launch {
                    mAudioFiles.clear()
                    mAudioFiles.addAll(list)
                }
                println("[hxg] 查询完成: ${mAudioFiles.size} thread: ${Thread.currentThread().name}")
            }
        }
    }
}
