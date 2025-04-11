@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.hxg.player.ui.widget

import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxg.player.AudioPlayerActivity
import com.hxg.player.FindAudioFiles
import com.hxg.player.entity.AudioFile
import com.hxg.player.util.Constants

@Composable
fun MyListScreen() {
    val data = FindAudioFiles.getAudioFiles()

    var isLoading = remember { true }
    var isRefreshing = remember { false }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        val scrollState = rememberLazyListState()

        LaunchedEffect(scrollState) {
            snapshotFlow { scrollState.firstVisibleItemIndex }
                .collect { index ->
                    println(
                        "[hxg] 滑动 index:$index, offset:${scrollState.firstVisibleItemScrollOffset}, canScrollForward:${scrollState.canScrollForward}, canScrollBackward:${scrollState.canScrollBackward}"
                    )
                    if (index == 0 && scrollState.firstVisibleItemScrollOffset == 0) {
                        // 滑动到顶部
                        println("[hxg] 滑动到顶部")
                    }
                    if (index > 0 && scrollState.layoutInfo.totalItemsCount - scrollState.layoutInfo.visibleItemsInfo.size <= scrollState.firstVisibleItemIndex) {
                        // 滑动到底部
                        println("[hxg] 滑动到底部")
                    }
                }
        }

        PullToRefreshBox(isRefreshing = isRefreshing, onRefresh = {
            isRefreshing = true
            println("[hxg] 开始刷新")
            FindAudioFiles.queryAudioFiles()
            isRefreshing = false
            println("[hxg] 刷新完成")
        }, modifier = Modifier) {
            LazyColumn(state = scrollState, modifier = Modifier.fillMaxSize()) {
//            stickyHeader {
//                Text(
//                    "Sticky Header", modifier = Modifier
//                        .padding(16.dp)
//                        .fillMaxSize()
//                        .background(Color.Gray)
//                )
//            }

                items(data) { item ->
                    if (item.isValid()) {
                        ItemCard(item)
                    }
                    isLoading = false
                }

                println("[hxg] 完成数据填充")
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun ItemCard(item: AudioFile) {
    val ctx = LocalContext.current
    Card(
        modifier = Modifier
            .padding(6.dp)
            .fillMaxSize()
            .clickable {
                println("[hxg] 点击了 $item")
                try {
                    val intent = Intent(ctx, AudioPlayerActivity::class.java)
                    val bundle = Bundle().apply {
                        putParcelable(Constants.EXTRA_INTENT_DATA, item)
                    }
                    intent.putExtras(bundle)
                    ctx.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    println("[hxg] err: ${e.message}")
                }
            }
    ) {
        Text(
            text = item.title,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
            fontSize = 16.sp
        )
        Text(
            text = item.artist,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 12.sp
        )
        Text(
            text = item.path,
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp, end = 16.dp),
            fontSize = 12.sp
        )

        Text(
            text = "id:${item.id}",
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp, end = 16.dp),
            fontSize = 12.sp
        )
    }
}
