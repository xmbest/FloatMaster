package com.xmbest.floatmaster.factory

import com.xmbest.floatmaster.constants.WidgetConstants
import com.xmbest.floatmaster.manager.FloatWindowManager
import com.xmbest.floatmaster.manager.WidgetConfigManager
import com.xmbest.floatmaster.model.FloatWidgetItem
import com.xmbest.floatmaster.ui.dialog.TimeWidgetConfig
import com.xmbest.floatmaster.ui.dialog.NetworkSpeedWidgetConfig
import com.xmbest.floatmaster.ui.dialog.MicMuteWidgetConfig
import com.xmbest.floatmaster.ui.widget.TimeWidget
import com.xmbest.floatmaster.ui.widget.NetworkSpeedWidget
import com.xmbest.floatmaster.ui.widget.MicMuteWidget

/**
 * Widget工厂类
 * 统一处理Widget的创建、激活、停用和重新创建逻辑
 */
class WidgetFactory(
    private val floatWindowManager: FloatWindowManager,
    private val configManager: WidgetConfigManager
) {
    
    /**
     * 激活Widget
     */
    fun activateWidget(widgetItem: FloatWidgetItem) {
        when (widgetItem.id) {
            WidgetConstants.WIDGET_ID_TIME_DISPLAY -> {
                val config = configManager.getConfig(widgetItem.id) as? TimeWidgetConfig ?: TimeWidgetConfig()
                floatWindowManager.addComposeView(
                    id = widgetItem.id,
                    content = {
                        TimeWidget(
                            refreshIntervalMs = config.refreshIntervalMs,
                            formatStr = config.formatStr,
                            timeZone = config.timeZone,
                            textProperties = config.textProperties
                        )
                    },
                    textProperties = config.textProperties
                )
            }
            WidgetConstants.WIDGET_ID_NETWORK_SPEED -> {
                val config = configManager.getConfig(widgetItem.id) as? NetworkSpeedWidgetConfig ?: NetworkSpeedWidgetConfig()
                floatWindowManager.addComposeView(
                    id = widgetItem.id,
                    content = {
                        NetworkSpeedWidget(
                            refreshIntervalMs = config.refreshIntervalMs,
                            mode = config.mode,
                            textProperties = config.textProperties
                        )
                    },
                    textProperties = config.textProperties
                )
            }
            WidgetConstants.WIDGET_ID_MIC_MUTE -> {
                val config = configManager.getConfig(widgetItem.id) as? MicMuteWidgetConfig ?: MicMuteWidgetConfig()
                floatWindowManager.addComposeView(
                    id = widgetItem.id,
                    content = {
                        MicMuteWidget(
                            imageProperties = config.imageProperties
                        )
                    },
                    imageProperties = config.imageProperties
                )
            }
            else -> {
                floatWindowManager.addComposeView(
                    id = widgetItem.id,
                    content = widgetItem.widgetComposer,
                    startX = 100,
                    startY = 100
                )
            }
        }
    }
    
    /**
     * 停用Widget
     */
    fun deactivateWidget(widgetId: String) {
        floatWindowManager.removeViewById(widgetId)
    }
    
    /**
     * 重新创建Widget（用于配置更新后）
     */
    fun recreateWidget(widgetId: String) {
        // 先移除现有的Widget
        floatWindowManager.removeViewById(widgetId)
        
        // 根据ID重新创建Widget
        when (widgetId) {
            WidgetConstants.WIDGET_ID_TIME_DISPLAY -> {
                val config = configManager.getConfig(widgetId) as? TimeWidgetConfig ?: TimeWidgetConfig()
                floatWindowManager.addComposeView(
                    id = widgetId,
                    content = {
                        TimeWidget(
                            refreshIntervalMs = config.refreshIntervalMs,
                            formatStr = config.formatStr,
                            timeZone = config.timeZone,
                            textProperties = config.textProperties
                        )
                    },
                    textProperties = config.textProperties
                )
            }
            WidgetConstants.WIDGET_ID_NETWORK_SPEED -> {
                val config = configManager.getConfig(widgetId) as? NetworkSpeedWidgetConfig ?: NetworkSpeedWidgetConfig()
                floatWindowManager.addComposeView(
                    id = widgetId,
                    content = {
                        NetworkSpeedWidget(
                            refreshIntervalMs = config.refreshIntervalMs,
                            mode = config.mode,
                            textProperties = config.textProperties
                        )
                    },
                    textProperties = config.textProperties
                )
            }
            WidgetConstants.WIDGET_ID_MIC_MUTE -> {
                val config = configManager.getConfig(widgetId) as? MicMuteWidgetConfig ?: MicMuteWidgetConfig()
                floatWindowManager.addComposeView(
                    id = widgetId,
                    content = {
                        MicMuteWidget(
                            imageProperties = config.imageProperties
                        )
                    },
                    imageProperties = config.imageProperties
                )
            }
        }
    }
    
    /**
     * 处理Widget配置更新
     */
    fun handleConfigUpdate(widgetId: String, config: Any, isActive: Boolean) {
        // 更新配置
        configManager.updateConfig(widgetId, config)
        
        // 如果Widget正在运行，重新创建以应用新配置
        if (isActive) {
            recreateWidget(widgetId)
        }
    }
}