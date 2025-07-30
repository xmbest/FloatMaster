package com.xmbest.floatmaster.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.hilt.navigation.compose.hiltViewModel
import com.xmbest.floatmaster.manager.FloatWindowManager
import com.xmbest.floatmaster.manager.rememberWidgetConfigManager
import com.xmbest.floatmaster.factory.WidgetFactory
import com.xmbest.floatmaster.model.getFloatWidgetItems
import com.xmbest.floatmaster.ui.component.ItemCard
import com.xmbest.floatmaster.ui.component.WidgetConfigDialogs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val floatWindowManager = remember { FloatWindowManager(context) }
    val configManager = rememberWidgetConfigManager()
    val widgetFactory = remember { WidgetFactory(floatWindowManager, configManager) }
    val floatWidgetItems = getFloatWidgetItems()
    
    // 获取权限状态
    val viewModel: com.xmbest.floatmaster.ui.activity.main.MainViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    // 记录每个功能项的激活状态
    val activeStates = remember { mutableStateMapOf<String, Boolean>() }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 权限状态显示 - 只有当存在未授权权限时才显示
            if (!state.allPermissionsGranted) {
                com.xmbest.floatmaster.ui.component.PermissionStatusCard(
                    state = state,
                    context = context,
                    onGrantPermission = { permission ->
                        // 这里需要从Activity获取权限管理器，暂时留空
                        // TODO: 实现权限请求逻辑
                    }
                )
            }
            
            FlowRow(
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
                                widgetFactory.activateWidget(widgetItem)
                                activeStates[widgetItem.id] = true
                            } else {
                                widgetFactory.deactivateWidget(widgetItem.id)
                                activeStates[widgetItem.id] = false
                            }
                        },
                        onEditClick = {
                            configManager.showConfigDialog(it.id)
                        }
                    )
                }
            }
        }
        
        // 统一的配置对话框
        WidgetConfigDialogs(
            configManager = configManager,
            widgetFactory = widgetFactory,
            activeStates = activeStates
        )
    }
}