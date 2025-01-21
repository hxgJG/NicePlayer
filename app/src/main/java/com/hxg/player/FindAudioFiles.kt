package com.hxg.player

import android.database.Cursor
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore

object FindAudioFiles {
    private var mAudioFiles: MutableList<AudioFile> = mutableListOf()

    fun getAudioFiles(): MutableList<AudioFile> {
        return mAudioFiles
    }

    fun queryAudioFiles() {
        // 获取 ContentResolver 实例
        val contentResolver = App.context.contentResolver

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
                mAudioFiles.add(AudioFile(id, title, artist, album, duration, size, data, albumId))
            }
        }
    }
}

data class AudioFile(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Int,
    val size: Long,
    val path: String,
    val albumArtId: Long
) : Parcelable {
    private val baseAlbumArtUri = Uri.parse("content://media/external/audio/albumart")

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readLong()
    )

    override fun toString(): String {
        return "AudioFile(id=$id, title=$title, artist=$artist, album=$album, duration=$duration, size=$size, path=$path, albumArtId=$albumArtId)"
    }

    override fun describeContents(): Int {
        return 0
    }

    fun getAlbumArt(): Uri {
        val albumArtUri = Uri.withAppendedPath(baseAlbumArtUri, albumArtId.toString())
        return albumArtUri
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(title)
        dest.writeString(artist)
        dest.writeString(album)
        dest.writeInt(duration)
        dest.writeLong(size)
        dest.writeString(path)
        dest.writeLong(albumArtId)
    }

    fun isValid(): Boolean {
        return path.isNotEmpty() && size > 0
    }

    companion object CREATOR : Parcelable.Creator<AudioFile> {
        override fun createFromParcel(parcel: Parcel): AudioFile {
            return AudioFile(parcel)
        }

        override fun newArray(size: Int): Array<AudioFile?> {
            return arrayOfNulls(size)
        }
    }
}