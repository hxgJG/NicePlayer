package com.hxg.player.ui.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.hxg.player.R
import kotlinx.coroutines.launch


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
                1 -> ConstraintLayoutPage()
                2 -> FrameLayoutPage()
            }
        }

        TabRow(
            selectedTabIndex = pagerState.currentPage
        ) {
            listOf(TabItem("歌曲", R.mipmap.icon_music_256), TabItem("歌单", R.mipmap.icon_misic_list_256), TabItem("我的", R.mipmap.icon_me_256))
                .forEachIndexed { index, item ->
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
fun ConstraintLayoutPage() {
    AndroidView(
        factory = { context ->
            androidx.constraintlayout.widget.ConstraintLayout(context).apply {
                setBackgroundColor(Color.Blue.toArgb())
                layoutParams = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                    androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_PARENT,
                    androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_PARENT
                )
                // 可以在这里添加更多的 ConstraintLayout 子视图
            }
        },
        modifier = Modifier.fillMaxSize().statusBarsPadding()
    )
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
        modifier = Modifier.fillMaxSize().statusBarsPadding()
    )
}


data class TabItem(val title: String, val icon: Int = -1)