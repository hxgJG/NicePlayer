package com.hxg.media.player

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class XPlayer(context: Context) {
    private var player: ExoPlayer? = null
    private var progressJob: Job? = null
    private var progressListener: ProgressChangedListener? = null
    private val controlChannel = Channel<ControlSignal>(capacity = Channel.UNLIMITED)

    init {
        player = ExoPlayer
            .Builder(context)
            .build()
            .apply { playWhenReady = true }
    }

    fun play(uri: Uri) {
        val player = player ?: return
        player.setMediaItem(MediaItem.fromUri(uri))
        player.prepare()
        player.play()
        startProgressListener()
    }

    fun setProgressListener(listener: ProgressChangedListener) {
        progressListener = listener
    }

    private fun startProgressListener() {
        stopProgressListener() // 确保只有一个进度监听器在运行
        val listener = progressListener ?: return

        progressJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                if (isPlaying()) {
                    listener.onProgressChanged(getCurrentPosition(), getDuration())
                }
                delay(1000) // 每隔1秒回调一次
                // 检查控制信号
                when (controlChannel.tryReceive().getOrNull()) {
                    ControlSignal.Pause -> {
                        controlChannel.receive() // 等待恢复信号
                    }

                    ControlSignal.Stop -> {
                        // 释放资源
                        break
                    }

                    else -> {}
                }
            }
        }
    }

    private fun stopProgressListener() {
        progressJob?.cancel()
    }

    fun release() {
        val player = player ?: return
        player.release()
        stopProgressListener()
        progressJob = null
    }

    fun playList(list: List<Uri>) {
        val player = player ?: return
        for (uri in list) {
            player.addMediaItem(MediaItem.fromUri(uri))

        }
        player.prepare()
        player.play()
        startProgressListener()
    }

    fun pause() {
        val player = player ?: return
        player.pause()

        controlChannel.trySend(ControlSignal.Pause)
    }

    fun resume() {
        val player = player ?: return
        player.play()
        controlChannel.trySend(ControlSignal.Resume)
    }

    fun seekTo(position: Long) {
        val player = player ?: return
        player.seekTo(position)
    }

    fun getCurrentPosition(): Long {
        val player = player ?: return 0
        return player.currentPosition
    }

    fun getDuration(): Long {
        val player = player ?: return 0
        return player.duration
    }

    fun isPlaying(): Boolean {
        val player = player ?: return false
        return player.isPlaying
    }

    fun getPlaybackState(): Int {
        val player = player ?: return 0
        return player.playbackState
    }

    fun getPlaybackSpeed(): Float {
        val player = player ?: return 0f
        return player.playbackParameters.speed
    }

    fun stop() {
        val player = player ?: return
        player.stop()
        controlChannel.trySend(ControlSignal.Stop)
        stopProgressListener()
    }

    fun addListener(listener: Player.Listener) {
        listener.let { player?.addListener(it) }
    }

    interface ProgressChangedListener {
        fun onProgressChanged(progress: Long, duration: Long)
    }

    private enum class ControlSignal {
        Pause,
        Resume,
        Stop
    }
}