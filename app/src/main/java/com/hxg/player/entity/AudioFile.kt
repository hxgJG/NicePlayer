package com.hxg.player.entity

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.hxg.player.room.AudioInfo

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

    fun toAudioInfo(): AudioInfo {
        return AudioInfo(id, title, artist, album, duration, size, path, albumArtId)
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
