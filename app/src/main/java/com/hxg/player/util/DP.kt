package com.hxg.player.util

import android.content.Context
import androidx.annotation.DimenRes
import com.hxg.player.App
import com.hxg.player.App.Companion.context

/**
 * Created by taeja on 16-1-29.
 */
object DP {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    @JvmStatic
    fun dp2px(context: Context?, dpValue: Float): Int {
        val scale = context?.resources?.displayMetrics?.density ?: 1f
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    fun dp2px(dpValue: Float): Int {
        return dp2px(context, dpValue)
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    fun px2dp(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    fun getDimensionPx(@DimenRes dimenResId: Int, context: Context? = null): Int {
        val ctx = context ?: App.context
        return ctx.resources.getDimensionPixelSize(dimenResId)
    }
}