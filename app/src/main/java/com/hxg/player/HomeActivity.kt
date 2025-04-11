package com.hxg.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.hxg.player.ui.theme.NicePlayerTheme
import com.hxg.player.ui.widget.HomeView
import com.hxg.player.util.toast

class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NicePlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeView()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        permissionReadWrite()
    }

    /**
     *  申请普通读写权限
     */
    private fun permissionReadWrite() {
        if (XXPermissions.isGranted(this, Permission.READ_MEDIA_AUDIO)) {
            queryAudioFiles()
            return
        }

        XXPermissions.with(this)
            // 申请单个权限
            .permission(Permission.READ_MEDIA_AUDIO)
//            .permission(Permission.READ_MEDIA_VIDEO)
            // 设置权限请求拦截器（局部设置）
            // .interceptor(new PermissionInterceptor())
            // 设置不触发错误检测机制（局部设置）
            // .unchecked()
            .request(object : OnPermissionCallback {

                override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                    if (!allGranted) {
                        toast("获取部分权限成功，但部分权限未正常授予")
                        return
                    }
                    queryAudioFiles()
                }

                override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                    if (doNotAskAgain) {
                        toast("被永久拒绝授权，请手动授予权限")
                        // 如果是被永久拒绝就跳转到应用权限系统设置页面
                        XXPermissions.startPermissionActivity(this@HomeActivity, permissions)
                    } else {
                        toast("获取权限失败")
                    }
                }
            })
    }

    private fun queryAudioFiles() {
        FindAudioFiles.queryAudioFiles()
    }
}
