package com.xmbest.floatmaster.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.xmbest.floatmaster.manager.FloatWindowManager
import com.xmbest.floatmaster.model.getFloatWidgetItems
import com.xmbest.floatmaster.ui.component.ItemCard
import com.xmbest.floatmaster.ui.dialog.TimeWidgetConfigDialog
import com.xmbest.floatmaster.ui.dialog.TimeWidgetConfig
import com.xmbest.floatmaster.ui.dialog.NetworkSpeedWidgetConfigDialog
import com.xmbest.floatmaster.ui.dialog.NetworkSpeedWidgetConfig
import com.xmbest.floatmaster.ui.dialog.MicMuteWidgetConfigDialog
import com.xmbest.floatmaster.ui.dialog.MicMuteWidgetConfig
import com.xmbest.floatmaster.ui.widget.MicMuteWidget
import com.xmbest.floatmaster.ui.widget.NetworkSpeedWidget
import com.xmbest.floatmaster.ui.widget.TimeWidget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val floatWindowManager = remember { FloatWindowManager(context) }
    val floatWidgetItems = getFloatWidgetItems()

    // 记录每个功能项的激活状态
    val activeStates = remember { mutableStateMapOf<String, Boolean>() }
    
    // 专用配置状态管理
    var timeWidgetConfigMap by remember { mutableStateOf(mapOf<String, TimeWidgetConfig>()) }
    var networkSpeedWidgetConfigMap by remember { mutableStateOf(mapOf<String, NetworkSpeedWidgetConfig>()) }
    var micMuteWidgetConfigMap by remember { mutableStateOf(mapOf<String, MicMuteWidgetConfig>()) }
    
    // 配置对话框状态
    var showTimeConfigDialog by remember { mutableStateOf(false) }
    var showNetworkSpeedConfigDialog by remember { mutableStateOf(false) }
    var showMicMuteConfigDialog by remember { mutableStateOf(false) }
    var currentConfigWidgetId by remember { mutableStateOf("") }

    Scaffold { paddingValues ->
        FlowRow(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            floatWidgetItems.fastForEach {
                val isActive = activeStates[it.id] ?: false
                ItemCard(
                    item = it,
                    isActive = isActive,
                    onToggle = { widgetItem, shouldActivate ->
                        if (shouldActivate) {
                            // 根据组件类型使用不同的属性添加悬浮窗
                            when (widgetItem.id) {
                                "time_display" -> {
                                    val config = timeWidgetConfigMap[widgetItem.id] ?: TimeWidgetConfig()
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
                                "network_speed" -> {
                                    val config = networkSpeedWidgetConfigMap[widgetItem.id] ?: NetworkSpeedWidgetConfig()
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
                                "mic_mute" -> {
                                    val config = micMuteWidgetConfigMap[widgetItem.id] ?: MicMuteWidgetConfig()
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
                            activeStates[widgetItem.id] = true
                        } else {
                            // 移除悬浮窗
                            floatWindowManager.removeViewById(widgetItem.id)
                            activeStates[widgetItem.id] = false
                        }
                    },
                    onEditClick = {
                            currentConfigWidgetId = it.id
                            when (it.id) {
                                "time_display" -> showTimeConfigDialog = true
                                "network_speed" -> showNetworkSpeedConfigDialog = true
                                "mic_mute" -> showMicMuteConfigDialog = true
                            }
                        }
                )
            }
        }
        
        // 配置对话框
        if (showTimeConfigDialog) {
            TimeWidgetConfigDialog(
                 initialConfig = timeWidgetConfigMap[currentConfigWidgetId] ?: TimeWidgetConfig(),
                onDismiss = {
                    showTimeConfigDialog = false
                },
                onConfirm = { config ->
                    timeWidgetConfigMap = timeWidgetConfigMap + (currentConfigWidgetId to config)
                    
                    // 如果悬浮窗正在运行，重新创建以应用新配置
                    if (activeStates[currentConfigWidgetId] == true) {
                        floatWindowManager.removeViewById(currentConfigWidgetId)
                        floatWindowManager.addComposeView(
                            id = currentConfigWidgetId,
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
                    
                    showTimeConfigDialog = false
                }
            )
        }
        
        if (showNetworkSpeedConfigDialog) {
            NetworkSpeedWidgetConfigDialog(
                 initialConfig = networkSpeedWidgetConfigMap[currentConfigWidgetId] ?: NetworkSpeedWidgetConfig(),
                onDismiss = {
                    showNetworkSpeedConfigDialog = false
                },
                onConfirm = { config ->
                    networkSpeedWidgetConfigMap = networkSpeedWidgetConfigMap + (currentConfigWidgetId to config)
                    
                    // 如果悬浮窗正在运行，重新创建以应用新配置
                    if (activeStates[currentConfigWidgetId] == true) {
                        floatWindowManager.removeViewById(currentConfigWidgetId)
                        floatWindowManager.addComposeView(
                            id = currentConfigWidgetId,
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
                    
                    showNetworkSpeedConfigDialog = false
                }
            )
        }
        
        if (showMicMuteConfigDialog) {
            MicMuteWidgetConfigDialog(
                 initialConfig = micMuteWidgetConfigMap[currentConfigWidgetId] ?: MicMuteWidgetConfig(),
                onDismiss = {
                    showMicMuteConfigDialog = false
                },
                onConfirm = { config ->
                    micMuteWidgetConfigMap = micMuteWidgetConfigMap + (currentConfigWidgetId to config)
                    
                    // 如果悬浮窗正在运行，重新创建以应用新配置
                    if (activeStates[currentConfigWidgetId] == true) {
                        floatWindowManager.removeViewById(currentConfigWidgetId)
                        floatWindowManager.addComposeView(
                            id = currentConfigWidgetId,
                            content = { 
                                MicMuteWidget(
                                    imageProperties = config.imageProperties
                                )
                            },
                            imageProperties = config.imageProperties
                        )
                    }
                    
                    showMicMuteConfigDialog = false
                }
            )
        }
    }
}