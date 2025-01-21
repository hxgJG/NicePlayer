package com.hxg.player.util

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.annotation.StyleableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.hxg.player.App
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun View?.show(show: Boolean = true) {
    this?.visibility = if (show) View.VISIBLE else View.GONE
}

fun View.isShow(): Boolean {
    return visibility == View.VISIBLE
}

fun View.isHide(): Boolean {
    return !isShow()
}


@Composable
fun colorTheme(@StyleableRes attrResId: Int): Color {
    val context = LocalContext.current
    val ta = context.obtainStyledAttributes(intArrayOf(attrResId))
    val color = ta.getColor(0, 0)
    ta.recycle()
    return Color(color)
}

@Composable
fun px2dp(px: Int): Dp {
    return with(LocalDensity.current) { px.toDp() }
}

fun toast(message: String, context: Context = App.context) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun toast(strId: Int) {
    toast(message = App.context.getString(strId))
}

fun longToast(message: String, context: Context = App.context) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

fun longToast(strId: Int) {
    longToast(App.context.getString(strId))
}

@Composable
fun Float.toDp(): Dp {
    return with(LocalDensity.current) { this@toDp.toDp() }
}

fun Any?.toJson(): String {
    this ?: return ""
    return Json.encodeToString(this)
}

inline fun <reified T> String.decodeJson(): T? {
    return try {
        Json.decodeFromString<T>(this)
    } catch (e: Exception) {
        null
    }
}
