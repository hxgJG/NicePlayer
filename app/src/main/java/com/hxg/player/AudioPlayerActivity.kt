package com.hxg.player

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import androidx.media3.common.Player.Listener
import com.hxg.media.player.XPlayer
import com.hxg.player.entity.AudioFile
import com.hxg.player.ui.theme.NicePlayerTheme
import com.hxg.player.util.Constants

class AudioPlayerActivity : ComponentActivity() {
    private var audioFile: AudioFile? = null
    private var player: XPlayer? = null
    private var isPlaying by mutableStateOf(false)
    private var mProgress by mutableLongStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NicePlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PlayerView(audioFile, isPlaying) { newState ->
                        isPlaying = newState
                        play()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        audioFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.extras?.getParcelable(Constants.EXTRA_INTENT_DATA, AudioFile::class.java)
        } else {
            intent.extras?.getParcelable(Constants.EXTRA_INTENT_DATA)
        }
        playAfterPageLoad()
    }

    private fun play() {
        val data = audioFile ?: return
        if (!data.isValid()) {
            return
        }

        if (player == null) {
            initPlayer()
        }

        when {
            player?.isPlaying() == true -> {
                pause()
            }

            else -> {
                if (mProgress > 0) {
                    resume()
                } else {
                    realPlay()
                }
            }
        }
    }

    private fun playAfterPageLoad() {
        val data = audioFile ?: return
        if (!data.isValid()) {
            return
        }

        if (player == null) {
            initPlayer()
        }

        if (player?.isPlaying() == true) {
            return
        }
        player?.setData(Uri.parse(data.path))
    }

    private fun initPlayer() {
        if (player == null) {
            player = XPlayer(this)
            player!!.addListener(object : Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
//                if (playbackState == ExoPlayer.STATE_ENDED) {
//                    player.seekTo(0)
//                    player.play()
//                }
                    println("[hxg] playbackState: $playbackState")
                }

                override fun onPositionDiscontinuity(
                    oldPosition: Player.PositionInfo,
                    newPosition: Player.PositionInfo,
                    reason: Int
                ) {
                    super.onPositionDiscontinuity(oldPosition, newPosition, reason)
                    println("[hxg] oldPosition: $oldPosition, newPosition: $newPosition, reason: $reason")
                }
            })

            player!!.setProgressListener(object : XPlayer.ProgressChangedListener {
                override fun onProgressChanged(progress: Long, duration: Long) {
                    mProgress = progress
                }
            })
        }
    }

    private fun realPlay() {
        player?.play()
    }

    private fun resume() {
        player?.resume()
    }

    private fun pause() {
        player?.pause()
    }

    private fun stop() {
        player?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }

    @Composable
    private fun PlayerView(
        audioFile: AudioFile?,
        isPlaying: Boolean,
        onPlayPauseClick: (Boolean) -> Unit
    ) {
        val data = remember { audioFile }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp)
            ) {
                Image(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp),
                    bitmap = loadBitmapFromUri(audioFile?.getAlbumArt()),
                    contentScale = ContentScale.Crop,
                    contentDescription = "封面"
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = data?.title ?: "未知歌曲",
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = data?.artist ?: "未知歌手",
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = data?.path ?: "...",
                modifier = Modifier.padding(start = 16.dp, bottom = 16.dp, end = 16.dp),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "${formatTime(mProgress.toInt())}/${formatTime(data?.duration ?: 0)}",
                modifier = Modifier.padding(start = 16.dp, bottom = 16.dp, end = 16.dp),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                onPlayPauseClick(!isPlaying)
            }) {
                Text(
                    text = if (isPlaying) "暂停" else (if (mProgress > 0) "继续播放" else "播放"),
                    fontSize = 16.sp
                )
            }
        }
    }

    @Composable
    private fun formatTime(time: Int?): String {
        time ?: return "00:00"
        val minutes = time / 1000 / 60
        val seconds = time / 1000 % 60
        return if (seconds < 10) {
            "$minutes:0$seconds"
        } else {
            "$minutes:$seconds"
        }
    }

    @Composable
    fun loadBitmapFromUri(uri: Uri?): ImageBitmap {
        val context = LocalContext.current

        uri ?: return getDefaultBitmap()

        val inputStream = try {
            context.contentResolver.openInputStream(uri)
        } catch (e: Exception) {
            null
        }

        return inputStream?.let {
            BitmapFactory.decodeStream(it).asImageBitmap()
        } ?: getDefaultBitmap()
    }

    private fun getDefaultBitmap(): ImageBitmap {
        return BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_round).asImageBitmap()
    }
}
