package com.hxg.player.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.hxg.player.R
import kotlinx.coroutines.launch

val homeTabs = listOf(
    TabItem("歌曲", TabType.MUSIC, R.mipmap.icon_music_256),
    TabItem("歌单", TabType.LIST, R.mipmap.icon_misic_list_256),
    TabItem("我的", TabType.ME, R.mipmap.icon_me_256)
)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeView() {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            count = 3, // 页面数量
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> MyListScreen()
                1 -> MusicListPage()
                2 -> FrameLayoutPage()
            }
        }

        TabRow(
            selectedTabIndex = pagerState.currentPage
        ) {
            homeTabs.forEachIndexed { index, item ->
                Tab(
                    text = { Text(item.title) },
                    icon = {
                        if (item.icon != -1) {
                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = null,
                                tint = if (pagerState.currentPage == index) Color.Red else Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            null
                        }
                    },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun MusicListPage() {
    var text by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        TextField(
            modifier = Modifier.weight(1f),
            value = text,
            onValueChange = { content ->
                text = content
            },
            placeholder = {
                Text("input music")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null
                )
            }
        )

        Image(
            modifier = Modifier
                .width(50.dp)
                .height(50.dp)
                .border(
                    width = 1.dp,
                    color = Color(0xFFDA8C1A),
                    shape = CircleShape
                )
                .padding(2.dp)
                .clip(shape = CircleShape)
                .clickable {
                    // TODO
                },
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
    }
}

@Composable
fun FrameLayoutPage() {
    AndroidView(
        factory = { context ->
            android.widget.FrameLayout(context).apply {
                setBackgroundColor(Color.Green.toArgb())
                layoutParams = android.widget.FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT
                )
                // 可以在这里添加更多的 FrameLayout 子视图
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    )
}

data class TabItem(val title: String, val type: TabType, val icon: Int = -1)

enum class TabType {
    MUSIC, // 歌曲
    LIST, // 歌单
    ME // 我的
}
