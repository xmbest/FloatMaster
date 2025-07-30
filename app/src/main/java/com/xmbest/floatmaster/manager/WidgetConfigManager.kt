package com.xmbest.floatmaster.manager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.xmbest.floatmaster.constants.WidgetConstants
import com.xmbest.floatmaster.ui.dialog.TimeWidgetConfig
import com.xmbest.floatmaster.ui.dialog.NetworkSpeedWidgetConfig
import com.xmbest.floatmaster.ui.dialog.MicMuteWidgetConfig

/**
 * Widget配置管理器
 * 统一管理所有Widget的配置状态和对话框状态
 */
class WidgetConfigManager {
    // 配置状态
    var timeWidgetConfigMap by mutableStateOf(mapOf<String, TimeWidgetConfig>())
    var networkSpeedWidgetConfigMap by mutableStateOf(mapOf<String, NetworkSpeedWidgetConfig>())
    var micMuteWidgetConfigMap by mutableStateOf(mapOf<String, MicMuteWidgetConfig>())
    
    // 对话框状态
    var showTimeConfigDialog by mutableStateOf(false)
    var showNetworkSpeedConfigDialog by mutableStateOf(false)
    var showMicMuteConfigDialog by mutableStateOf(false)
    var currentConfigWidgetId by mutableStateOf("")
    
    /**
     * 获取指定Widget的配置
     */
    fun getConfig(widgetId: String): Any? {
        return when (widgetId) {
            WidgetConstants.WIDGET_ID_TIME_DISPLAY -> timeWidgetConfigMap[widgetId] ?: TimeWidgetConfig()
            WidgetConstants.WIDGET_ID_NETWORK_SPEED -> networkSpeedWidgetConfigMap[widgetId] ?: NetworkSpeedWidgetConfig()
            WidgetConstants.WIDGET_ID_MIC_MUTE -> micMuteWidgetConfigMap[widgetId] ?: MicMuteWidgetConfig()
            else -> null
        }
    }
    
    /**
     * 更新指定Widget的配置
     */
    fun updateConfig(widgetId: String, config: Any) {
        when (widgetId) {
            WidgetConstants.WIDGET_ID_TIME_DISPLAY -> {
                if (config is TimeWidgetConfig) {
                    timeWidgetConfigMap = timeWidgetConfigMap + (widgetId to config)
                }
            }
            WidgetConstants.WIDGET_ID_NETWORK_SPEED -> {
                if (config is NetworkSpeedWidgetConfig) {
                    networkSpeedWidgetConfigMap = networkSpeedWidgetConfigMap + (widgetId to config)
                }
            }
            WidgetConstants.WIDGET_ID_MIC_MUTE -> {
                if (config is MicMuteWidgetConfig) {
                    micMuteWidgetConfigMap = micMuteWidgetConfigMap + (widgetId to config)
                }
            }
        }
    }
    
    /**
     * 显示配置对话框
     */
    fun showConfigDialog(widgetId: String) {
        currentConfigWidgetId = widgetId
        when (widgetId) {
            WidgetConstants.WIDGET_ID_TIME_DISPLAY -> showTimeConfigDialog = true
            WidgetConstants.WIDGET_ID_NETWORK_SPEED -> showNetworkSpeedConfigDialog = true
            WidgetConstants.WIDGET_ID_MIC_MUTE -> showMicMuteConfigDialog = true
        }
    }
    
    /**
     * 隐藏所有配置对话框
     */
    fun hideAllConfigDialogs() {
        showTimeConfigDialog = false
        showNetworkSpeedConfigDialog = false
        showMicMuteConfigDialog = false
    }
    

}

/**
 * 创建WidgetConfigManager的Composable函数
 */
@Composable
fun rememberWidgetConfigManager(): WidgetConfigManager {
    return remember { WidgetConfigManager() }
}