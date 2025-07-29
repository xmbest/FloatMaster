package com.xmbest.floatmaster.model

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.xmbest.floatmaster.ui.widget.MicMuteWidget
import com.xmbest.floatmaster.ui.widget.NetworkSpeedWidget
import com.xmbest.floatmaster.ui.widget.TimeWidget

/**
 * 悬浮功能项数据类
 * @param id 唯一标识
 * @param title 标题
 * @param description 描述
 * @param icon 图标
 * @param permissionChecker 权限检查函数
 * @param widgetComposer 悬浮组件构建函数
 */
data class FloatWidgetItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val permissionChecker: @Composable () -> Boolean,
    val widgetComposer: @Composable () -> Unit
)

/**
 * 预定义的悬浮功能项列表
 */
@Composable
fun getFloatWidgetItems(): List<FloatWidgetItem> {
    val context = LocalContext.current
    
    return listOf(
        FloatWidgetItem(
            id = "mic_mute",
            title = "麦克风控制",
            description = "快速静音/取消静音麦克风",
            icon = Icons.Default.Mic,
            permissionChecker = { 
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            },
            widgetComposer = {
                MicMuteWidget()
            }
        ),
        FloatWidgetItem(
            id = "network_speed",
            title = "网速监控",
            description = "实时显示网络上下行速度",
            icon = Icons.Default.NetworkCheck,
            permissionChecker = { true }, // 网速监控不需要特殊权限
            widgetComposer = {
                NetworkSpeedWidget()
            }
        ),
        FloatWidgetItem(
            id = "time_display",
            title = "时间显示",
            description = "显示当前时间",
            icon = Icons.Default.AccessTime,
            permissionChecker = { true }, // 时间显示不需要特殊权限
            widgetComposer = {
                TimeWidget()
            }
        )
    )
}