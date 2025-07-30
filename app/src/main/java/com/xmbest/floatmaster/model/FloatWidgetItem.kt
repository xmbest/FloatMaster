package com.xmbest.floatmaster.model

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.xmbest.floatmaster.constants.WidgetConstants
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.xmbest.floatmaster.R
import com.xmbest.floatmaster.model.Permission
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
            id = WidgetConstants.WIDGET_ID_MIC_MUTE,
            title = stringResource(R.string.widget_mic_mute),
            description = "快速静音/取消静音麦克风",
            icon = Icons.Default.Mic,
            permissionChecker = { 
                // 悬浮窗权限是必需的，同时检查麦克风权限
                Permission.OVERLAY.isGranted(context) && 
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
            id = WidgetConstants.WIDGET_ID_NETWORK_SPEED,
            title = stringResource(R.string.widget_network_speed),
            description = context.getString(R.string.widget_network_speed_desc),
            icon = Icons.Default.NetworkCheck,
            permissionChecker = { 
                // 所有悬浮窗都需要悬浮窗权限
                Permission.OVERLAY.isGranted(context)
            },
            widgetComposer = {
                NetworkSpeedWidget()
            }
        ),
        FloatWidgetItem(
            id = WidgetConstants.WIDGET_ID_TIME_DISPLAY,
            title = stringResource(R.string.widget_time_display),
            description = context.getString(R.string.widget_time_display_desc),
            icon = Icons.Default.AccessTime,
            permissionChecker = { 
                // 所有悬浮窗都需要悬浮窗权限
                Permission.OVERLAY.isGranted(context)
            },
            widgetComposer = {
                TimeWidget()
            }
        )
    )
}