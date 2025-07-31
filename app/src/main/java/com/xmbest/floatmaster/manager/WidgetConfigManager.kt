package com.xmbest.floatmaster.manager

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xmbest.floatmaster.constants.WidgetConstants
import com.xmbest.floatmaster.model.ImageProperties
import com.xmbest.floatmaster.model.TextProperties
import com.xmbest.floatmaster.module.DataStoreModule
import com.xmbest.floatmaster.ui.dialog.MicMuteWidgetConfig
import com.xmbest.floatmaster.ui.dialog.NetworkSpeedWidgetConfig
import com.xmbest.floatmaster.ui.dialog.TimeWidgetConfig
import com.xmbest.floatmaster.ui.widget.NetworkSpeedMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Widget配置管理器
 * 统一管理所有Widget的配置状态和对话框状态，支持数据持久化
 */
class WidgetConfigManager(
    private val context: Context,
    private val dataStore: DataStoreModule
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    
    // 配置状态
    var timeWidgetConfigMap by mutableStateOf(mapOf<String, TimeWidgetConfig>())
    var networkSpeedWidgetConfigMap by mutableStateOf(mapOf<String, NetworkSpeedWidgetConfig>())
    var micMuteWidgetConfigMap by mutableStateOf(mapOf<String, MicMuteWidgetConfig>())
    
    // 位置信息状态
    var widgetPositions by mutableStateOf(mapOf<String, Map<String, Float>>())
    
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
                    saveTimeWidgetConfig(widgetId, config)
                }
            }
            WidgetConstants.WIDGET_ID_NETWORK_SPEED -> {
                if (config is NetworkSpeedWidgetConfig) {
                    networkSpeedWidgetConfigMap = networkSpeedWidgetConfigMap + (widgetId to config)
                    saveNetworkSpeedWidgetConfig(widgetId, config)
                }
            }
            WidgetConstants.WIDGET_ID_MIC_MUTE -> {
                if (config is MicMuteWidgetConfig) {
                    micMuteWidgetConfigMap = micMuteWidgetConfigMap + (widgetId to config)
                    saveMicMuteWidgetConfig(widgetId, config)
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
    
    /**
     * 更新Widget位置信息
     */
    fun updateWidgetPosition(widgetId: String, x: Float, y: Float, width: Float, height: Float) {
        val position = mapOf(
            "x" to x,
            "y" to y,
            "width" to width,
            "height" to height
        )
        widgetPositions = widgetPositions + (widgetId to position)
        
        // 同时更新配置中的坐标
        when (widgetId) {
            WidgetConstants.WIDGET_ID_TIME_DISPLAY -> {
                timeWidgetConfigMap[widgetId]?.let { config ->
                    val updatedConfig = config.copy(
                        textProperties = config.textProperties.copy(x = x, y = y)
                    )
                    timeWidgetConfigMap = timeWidgetConfigMap + (widgetId to updatedConfig)
                    saveTimeWidgetConfig(widgetId, updatedConfig)
                }
            }
            WidgetConstants.WIDGET_ID_NETWORK_SPEED -> {
                networkSpeedWidgetConfigMap[widgetId]?.let { config ->
                    val updatedConfig = config.copy(
                        textProperties = config.textProperties.copy(x = x, y = y)
                    )
                    networkSpeedWidgetConfigMap = networkSpeedWidgetConfigMap + (widgetId to updatedConfig)
                    saveNetworkSpeedWidgetConfig(widgetId, updatedConfig)
                }
            }
            WidgetConstants.WIDGET_ID_MIC_MUTE -> {
                micMuteWidgetConfigMap[widgetId]?.let { config ->
                    val updatedConfig = config.copy(
                        imageProperties = config.imageProperties.copy(x = x, y = y)
                    )
                    micMuteWidgetConfigMap = micMuteWidgetConfigMap + (widgetId to updatedConfig)
                    saveMicMuteWidgetConfig(widgetId, updatedConfig)
                }
            }
        }
        
        // 保存到DataStore
        scope.launch {
            dataStore.saveWidgetPosition(widgetId, x, y, width, height)
        }
    }
    
    /**
     * 获取Widget位置信息
     */
    fun getWidgetPosition(widgetId: String): Map<String, Float> {
        return widgetPositions[widgetId] ?: mapOf(
            "x" to 100f,
            "y" to 100f,
            "width" to 300f,
            "height" to 120f
        )
    }
    
    /**
     * 从DataStore加载所有配置
     * 使用分批加载避免阻塞启动
     */
    fun loadAllConfigs() {
        scope.launch {
            // 加载所有已知的Widget配置
            val widgetIds = listOf(
                WidgetConstants.WIDGET_ID_TIME_DISPLAY,
                WidgetConstants.WIDGET_ID_NETWORK_SPEED,
                WidgetConstants.WIDGET_ID_MIC_MUTE
            )
            
            // 分批加载，避免阻塞
            widgetIds.chunked(2).forEach { batch ->
                batch.forEach { widgetId ->
                    loadWidgetPosition(widgetId)
                    loadWidgetConfig(widgetId)
                }
                kotlinx.coroutines.yield() // 让出执行权
            }
        }
    }
    
    private suspend fun loadWidgetPosition(widgetId: String) {
        val position = dataStore.getWidgetPosition(widgetId).first()
        widgetPositions = widgetPositions + (widgetId to position)
    }
    
    private suspend fun loadWidgetConfig(widgetId: String) {
        when (widgetId) {
            WidgetConstants.WIDGET_ID_TIME_DISPLAY -> loadTimeWidgetConfig(widgetId)
            WidgetConstants.WIDGET_ID_NETWORK_SPEED -> loadNetworkSpeedWidgetConfig(widgetId)
            WidgetConstants.WIDGET_ID_MIC_MUTE -> loadMicMuteWidgetConfig(widgetId)
        }
    }
    
    private suspend fun loadTimeWidgetConfig(widgetId: String) {
        val configKeys = listOf(
            "refresh_interval" to 1000L,
            "format" to "HH:mm:ss",
            "timezone" to "",
            "text_color" to Color.Black.toArgb().toLong(),
            "bg_color" to Color.Transparent.toArgb().toLong(),
            "text_size" to 16f,
            "width" to 300f,
            "height" to 120f,
            "x" to 0f,
            "y" to 0f
        )
        
        val configMap = dataStore.getWidgetConfig(widgetId, configKeys).first()
        val textProperties = TextProperties(
            textColor = Color(configMap["text_color"] as Long),
            backgroundColor = Color(configMap["bg_color"] as Long),
            textSize = (configMap["text_size"] as Float).sp,
            width = (configMap["width"] as Float).dp,
            height = (configMap["height"] as Float).dp,
            x = configMap["x"] as Float,
            y = configMap["y"] as Float
        )
        
        val config = TimeWidgetConfig(
            refreshIntervalMs = configMap["refresh_interval"] as Long,
            formatStr = configMap["format"] as String,
            timeZone = (configMap["timezone"] as String).takeIf { it.isNotBlank() },
            textProperties = textProperties
        )
        
        timeWidgetConfigMap = timeWidgetConfigMap + (widgetId to config)
    }
    
    private suspend fun loadNetworkSpeedWidgetConfig(widgetId: String) {
        val configKeys = listOf(
            "refresh_interval" to 1000L,
            "mode" to "DOWNLOAD_ONLY",
            "text_color" to Color.Black.toArgb().toLong(),
            "bg_color" to Color.Transparent.toArgb().toLong(),
            "text_size" to 16f,
            "width" to 300f,
            "height" to 120f,
            "x" to 0f,
            "y" to 0f
        )
        
        val configMap = dataStore.getWidgetConfig(widgetId, configKeys).first()
        val textProperties = TextProperties(
            textColor = Color(configMap["text_color"] as Long),
            backgroundColor = Color(configMap["bg_color"] as Long),
            textSize = (configMap["text_size"] as Float).sp,
            width = (configMap["width"] as Float).dp,
            height = (configMap["height"] as Float).dp,
            x = configMap["x"] as Float,
            y = configMap["y"] as Float
        )
        
        val mode = try {
            NetworkSpeedMode.valueOf(configMap["mode"] as String)
        } catch (e: Exception) {
            NetworkSpeedMode.DOWNLOAD_ONLY
        }
        
        val config = NetworkSpeedWidgetConfig(
            refreshIntervalMs = configMap["refresh_interval"] as Long,
            mode = mode,
            textProperties = textProperties
        )
        
        networkSpeedWidgetConfigMap = networkSpeedWidgetConfigMap + (widgetId to config)
    }
    
    private suspend fun loadMicMuteWidgetConfig(widgetId: String) {
        val configKeys = listOf(
            "color" to Color.Black.toArgb().toLong(),
            "bg_color" to Color.Transparent.toArgb().toLong(),
            "width" to 300f,
            "height" to 300f,
            "x" to 100f,
            "y" to 100f
        )
        
        val configMap = dataStore.getWidgetConfig(widgetId, configKeys).first()
        val imageProperties = ImageProperties(
            color = Color(configMap["color"] as Long),
            backgroundColor = Color(configMap["bg_color"] as Long),
            width = configMap["width"] as Float,
            height = configMap["height"] as Float,
            x = configMap["x"] as Float,
            y = configMap["y"] as Float
        )
        
        val config = MicMuteWidgetConfig(
            imageProperties = imageProperties
        )
        
        micMuteWidgetConfigMap = micMuteWidgetConfigMap + (widgetId to config)
    }
    
    private fun saveTimeWidgetConfig(widgetId: String, config: TimeWidgetConfig) {
        scope.launch {
            val configMap = mapOf(
                "refresh_interval" to config.refreshIntervalMs,
                "format" to config.formatStr,
                "timezone" to (config.timeZone ?: ""),
                "text_color" to config.textProperties.textColor.toArgb().toLong(),
                "bg_color" to config.textProperties.backgroundColor.toArgb().toLong(),
                "text_size" to config.textProperties.textSize.value,
                "width" to config.textProperties.width.value,
                "height" to config.textProperties.height.value,
                "x" to config.textProperties.x,
                "y" to config.textProperties.y
            )
            dataStore.saveWidgetConfig(widgetId, configMap)
        }
    }
    
    private fun saveNetworkSpeedWidgetConfig(widgetId: String, config: NetworkSpeedWidgetConfig) {
        scope.launch {
            val configMap = mapOf(
                "refresh_interval" to config.refreshIntervalMs,
                "mode" to config.mode.name,
                "text_color" to config.textProperties.textColor.toArgb().toLong(),
                "bg_color" to config.textProperties.backgroundColor.toArgb().toLong(),
                "text_size" to config.textProperties.textSize.value,
                "width" to config.textProperties.width.value,
                "height" to config.textProperties.height.value,
                "x" to config.textProperties.x,
                "y" to config.textProperties.y
            )
            dataStore.saveWidgetConfig(widgetId, configMap)
        }
    }
    
    private fun saveMicMuteWidgetConfig(widgetId: String, config: MicMuteWidgetConfig) {
        scope.launch {
            val configMap = mapOf(
                "color" to config.imageProperties.color.toArgb().toLong(),
                "bg_color" to config.imageProperties.backgroundColor.toArgb().toLong(),
                "width" to config.imageProperties.width,
                "height" to config.imageProperties.height,
                "x" to config.imageProperties.x,
                "y" to config.imageProperties.y
            )
            dataStore.saveWidgetConfig(widgetId, configMap)
        }
    }

}