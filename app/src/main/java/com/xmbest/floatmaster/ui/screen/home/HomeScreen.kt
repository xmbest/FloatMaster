package com.xmbest.floatmaster.ui.screen.home

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
import androidx.compose.foundation.layout.FlowRow
import com.xmbest.floatmaster.manager.FloatWindowManager
import com.xmbest.floatmaster.manager.WidgetConfigManager
import com.xmbest.floatmaster.factory.WidgetFactory
import com.xmbest.floatmaster.model.getFloatWidgetItems
import com.xmbest.floatmaster.ui.activity.main.MainViewModel
import com.xmbest.floatmaster.ui.activity.main.MainIntent
import com.xmbest.floatmaster.ui.component.ItemCard
import com.xmbest.floatmaster.ui.component.PermissionStatusCard
import com.xmbest.floatmaster.ui.component.WidgetConfigDialogs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    floatWindowManager: FloatWindowManager = hiltViewModel<HomeScreenViewModel>().floatWindowManager,
    configManager: WidgetConfigManager = hiltViewModel<HomeScreenViewModel>().configManager,
    widgetFactory: WidgetFactory = hiltViewModel<HomeScreenViewModel>().widgetFactory
) {
    val context = LocalContext.current
    val floatWidgetItems = getFloatWidgetItems()
    
    // 获取权限状态
    val viewModel: MainViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    // 使用状态来触发重组，确保UI与FloatWindowManager状态同步
    var refreshTrigger by remember { mutableStateOf(0) }
    
    // 监听权限状态变化，当权限状态改变时触发UI刷新
    LaunchedEffect(state.permissions) {
        refreshTrigger++
    }

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
                PermissionStatusCard(
                    state = state,
                    context = context,
                    onGrantPermission = { permission ->
                        viewModel.handleIntent(MainIntent.RequestPermission(permission))
                    }
                )
            }
            
            FlowRow(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                floatWidgetItems.fastForEach {
                    // 直接从FloatWindowManager获取真实状态，refreshTrigger确保重组
                    val isActive by remember(it.id) { 
                        derivedStateOf { 
                            refreshTrigger // 依赖refreshTrigger触发重新计算
                            floatWindowManager.hasView(it.id) 
                        } 
                    }
                    
                    // 计算当前Widget的权限状态
                    val isEnabled = it.permissionChecker()
                    
                    ItemCard(
                        item = it,
                        isActive = isActive,
                        configManager = configManager,
                        isEnabled = isEnabled,
                        onToggle = { widgetItem, shouldActivate ->
                            if (shouldActivate) {
                                widgetFactory.activateWidget(widgetItem)
                            } else {
                                widgetFactory.deactivateWidget(widgetItem.id)
                            }
                            // 触发重组以更新UI状态
                            refreshTrigger++
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
            onWidgetRecreated = {
                // Widget重新创建后触发UI更新
                refreshTrigger++
            }
        )
    }
}