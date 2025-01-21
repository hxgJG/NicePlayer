package com.hxg.player.util

import android.media.MediaFormat
import java.io.File

/**
 * Created by Remix on 2015/11/30.
 */
/**
 * 通用工具类
 */
object Util {

    /**
     * 获得目录大小
     */
    fun getFolderSize(file: File?): Long {
        var size: Long = 0
        try {
            val fileList = file?.listFiles() ?: return size
            for (i in fileList.indices) {
                // 如果下面还有文件
                size = if (fileList[i].isDirectory) {
                    size + getFolderSize(fileList[i])
                } else {
                    size + fileList[i].length()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size
    }

    /**
     * 删除某个目录
     */
    fun deleteFilesByDirectory(directory: File?) {
        if (directory == null) {
            return
        }
        if (directory.isFile) {
            deleteFileSafely(directory)
            return
        }
        if (directory.isDirectory) {
            val childFile = directory.listFiles()
            if (childFile == null || childFile.isEmpty()) {
                deleteFileSafely(directory)
                return
            }
            for (f in childFile) {
                deleteFilesByDirectory(f)
            }
            deleteFileSafely(directory)
        }
    }

    /**
     * 安全删除文件 小米、华为等手机极有可能在删除一个文件后再创建同名文件出现bug
     */
    fun deleteFileSafely(file: File?): Boolean {
        if (file != null) {
            val tmpPath =
                (file.parent ?: return false) + File.separator + System.currentTimeMillis()
            val tmp = File(tmpPath)
            return file.renameTo(tmp) && tmp.delete()
        }
        return false
    }

    /**
     * 获得歌曲格式
     */
    fun getType(mimeType: String): String {
        return when {
            mimeType == MediaFormat.MIMETYPE_AUDIO_MPEG -> "mp3"
            mimeType == MediaFormat.MIMETYPE_AUDIO_FLAC -> "flac"
            mimeType == MediaFormat.MIMETYPE_AUDIO_AAC -> "aac"
            mimeType.contains("ape") -> "ape"
            else -> {
                try {
                    if (mimeType.contains("audio/")) {
                        mimeType.substring(6, mimeType.length - 1)
                    } else {
                        mimeType
                    }
                } catch (e: Exception) {
                    mimeType
                }
            }
        }
    }

    /**
     * 转换时间
     *
     * @return 00:00格式的时间
     */
    fun getTime(duration: Long): String {
        val minute = duration.toInt() / 1000 / 60
        val second = (duration / 1000).toInt() % 60
        //如果分钟数小于10
        return if (minute < 10) {
            if (second < 10) {
                "0$minute:0$second"
            } else {
                "0$minute:$second"
            }
        } else {
            if (second < 10) {
                "$minute:0$second"
            } else {
                "$minute:$second"
            }
        }
    }
}