package com.xmbest.floatmaster.ui.component

import androidx.compose.runtime.Composable
import com.xmbest.floatmaster.manager.WidgetConfigManager
import com.xmbest.floatmaster.factory.WidgetFactory
import com.xmbest.floatmaster.ui.dialog.TimeWidgetConfigDialog
import com.xmbest.floatmaster.ui.dialog.TimeWidgetConfig
import com.xmbest.floatmaster.ui.dialog.NetworkSpeedWidgetConfigDialog
import com.xmbest.floatmaster.ui.dialog.NetworkSpeedWidgetConfig
import com.xmbest.floatmaster.ui.dialog.MicMuteWidgetConfigDialog
import com.xmbest.floatmaster.ui.dialog.MicMuteWidgetConfig

/**
 * 统一的Widget配置对话框组件
 * 根据当前状态显示相应的配置对话框
 */
@Composable
fun WidgetConfigDialogs(
    configManager: WidgetConfigManager,
    widgetFactory: WidgetFactory,
    activeStates: Map<String, Boolean>
) {
    // 时间Widget配置对话框
    if (configManager.showTimeConfigDialog) {
        TimeWidgetConfigDialog(
            initialConfig = configManager.getConfig(configManager.currentConfigWidgetId) as? TimeWidgetConfig ?: TimeWidgetConfig(),
            onDismiss = {
                configManager.hideAllConfigDialogs()
            },
            onConfirm = { config ->
                val isActive = activeStates[configManager.currentConfigWidgetId] == true
                widgetFactory.handleConfigUpdate(configManager.currentConfigWidgetId, config, isActive)
                configManager.hideAllConfigDialogs()
            }
        )
    }
    
    // 网络速度Widget配置对话框
    if (configManager.showNetworkSpeedConfigDialog) {
        NetworkSpeedWidgetConfigDialog(
            initialConfig = configManager.getConfig(configManager.currentConfigWidgetId) as? NetworkSpeedWidgetConfig ?: NetworkSpeedWidgetConfig(),
            onDismiss = {
                configManager.hideAllConfigDialogs()
            },
            onConfirm = { config ->
                val isActive = activeStates[configManager.currentConfigWidgetId] == true
                widgetFactory.handleConfigUpdate(configManager.currentConfigWidgetId, config, isActive)
                configManager.hideAllConfigDialogs()
            }
        )
    }
    
    // 麦克风静音Widget配置对话框
    if (configManager.showMicMuteConfigDialog) {
        MicMuteWidgetConfigDialog(
            initialConfig = configManager.getConfig(configManager.currentConfigWidgetId) as? MicMuteWidgetConfig ?: MicMuteWidgetConfig(),
            onDismiss = {
                configManager.hideAllConfigDialogs()
            },
            onConfirm = { config ->
                val isActive = activeStates[configManager.currentConfigWidgetId] == true
                widgetFactory.handleConfigUpdate(configManager.currentConfigWidgetId, config, isActive)
                configManager.hideAllConfigDialogs()
            }
        )
    }
}