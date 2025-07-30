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
import com.xmbest.floatmaster.model.WindowPosition

/**
 * Widget工厂类
 * 统一处理Widget的创建、激活、停用和重新创建逻辑
 */
class WidgetFactory(
    val floatWindowManager: FloatWindowManager,
    private val configManager: WidgetConfigManager
) {
    
    init {
        // 设置位置变化监听器，当悬浮窗拖拽结束时保存新位置
        floatWindowManager.onPositionChanged = { id, x, y, width, height ->
            configManager.updateWidgetPosition(id, x, y, width, height)
        }
    }
    
    /**
     * 激活Widget
     */
    fun activateWidget(widgetItem: FloatWidgetItem) {
        when (widgetItem.id) {
            WidgetConstants.WIDGET_ID_TIME_DISPLAY -> {
                val config = configManager.getConfig(widgetItem.id) as? TimeWidgetConfig ?: TimeWidgetConfig()
                val position = WindowPosition(
                    x = config.textProperties.x,
                    y = config.textProperties.y,
                    width = config.textProperties.width.value.toInt(),
                    height = config.textProperties.height.value.toInt()
                )
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
                    position = position
                )
            }
            WidgetConstants.WIDGET_ID_NETWORK_SPEED -> {
                val config = configManager.getConfig(widgetItem.id) as? NetworkSpeedWidgetConfig ?: NetworkSpeedWidgetConfig()
                val position = WindowPosition(
                    x = config.textProperties.x,
                    y = config.textProperties.y,
                    width = config.textProperties.width.value.toInt(),
                    height = config.textProperties.height.value.toInt()
                )
                floatWindowManager.addComposeView(
                    id = widgetItem.id,
                    content = {
                        NetworkSpeedWidget(
                            refreshIntervalMs = config.refreshIntervalMs,
                            mode = config.mode,
                            textProperties = config.textProperties
                        )
                    },
                    position = position
                )
            }
            WidgetConstants.WIDGET_ID_MIC_MUTE -> {
                val config = configManager.getConfig(widgetItem.id) as? MicMuteWidgetConfig ?: MicMuteWidgetConfig()
                val position = WindowPosition(
                    x = config.imageProperties.x,
                    y = config.imageProperties.y,
                    width = config.imageProperties.width.toInt(),
                    height = config.imageProperties.height.toInt()
                )
                floatWindowManager.addComposeView(
                    id = widgetItem.id,
                    content = {
                        MicMuteWidget(
                            imageProperties = config.imageProperties
                        )
                    },
                    position = position
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
                val position = WindowPosition(
                    x = config.textProperties.x,
                    y = config.textProperties.y,
                    width = config.textProperties.width.value.toInt(),
                    height = config.textProperties.height.value.toInt()
                )
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
                    position = position
                )
            }
            WidgetConstants.WIDGET_ID_NETWORK_SPEED -> {
                val config = configManager.getConfig(widgetId) as? NetworkSpeedWidgetConfig ?: NetworkSpeedWidgetConfig()
                val position = WindowPosition(
                    x = config.textProperties.x,
                    y = config.textProperties.y,
                    width = config.textProperties.width.value.toInt(),
                    height = config.textProperties.height.value.toInt()
                )
                floatWindowManager.addComposeView(
                    id = widgetId,
                    content = {
                        NetworkSpeedWidget(
                            refreshIntervalMs = config.refreshIntervalMs,
                            mode = config.mode,
                            textProperties = config.textProperties
                        )
                    },
                    position = position
                )
            }
            WidgetConstants.WIDGET_ID_MIC_MUTE -> {
                val config = configManager.getConfig(widgetId) as? MicMuteWidgetConfig ?: MicMuteWidgetConfig()
                val position = WindowPosition(
                    x = config.imageProperties.x,
                    y = config.imageProperties.y,
                    width = config.imageProperties.width.toInt(),
                    height = config.imageProperties.height.toInt()
                )
                floatWindowManager.addComposeView(
                    id = widgetId,
                    content = {
                        MicMuteWidget(
                            imageProperties = config.imageProperties
                        )
                    },
                    position = position
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
        
        // 同步更新位置信息
        when (config) {
            is TimeWidgetConfig -> {
                configManager.updateWidgetPosition(
                    widgetId, 
                    config.textProperties.x, 
                    config.textProperties.y,
                    config.textProperties.width.value,
                    config.textProperties.height.value
                )
            }
            is NetworkSpeedWidgetConfig -> {
                configManager.updateWidgetPosition(
                    widgetId, 
                    config.textProperties.x, 
                    config.textProperties.y,
                    config.textProperties.width.value,
                    config.textProperties.height.value
                )
            }
            is MicMuteWidgetConfig -> {
                configManager.updateWidgetPosition(
                    widgetId, 
                    config.imageProperties.x, 
                    config.imageProperties.y,
                    config.imageProperties.width,
                    config.imageProperties.height
                )
            }
        }
        
        // 如果Widget正在运行，重新创建以应用新配置
        if (isActive) {
            recreateWidget(widgetId)
        }
    }
}