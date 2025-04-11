package com.hxg.player.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hxg.player.entity.AudioFile

@Entity(tableName = "audio_infos")
data class AudioInfo(
    @PrimaryKey
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Int,
    val size: Long,
    val path: String,
    val albumArtId: Long
) {
    fun toAudioFile(): AudioFile {
        return AudioFile(id, title, artist, album, duration, size, path, albumArtId)
    }
}
