package com.xmbest.floatmaster.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.hilt.navigation.compose.hiltViewModel
import com.xmbest.floatmaster.R
import com.xmbest.floatmaster.model.getFloatWidgetItems
import com.xmbest.floatmaster.ui.activity.main.MainIntent
import com.xmbest.floatmaster.ui.activity.main.MainViewModel
import com.xmbest.floatmaster.ui.component.ItemCard
import com.xmbest.floatmaster.ui.component.PermissionStatusCard
import com.xmbest.floatmaster.ui.component.WidgetConfigDialogs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeScreenViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val floatWidgetItems = getFloatWidgetItems()

    // 获取权限状态
    val permissionState by mainViewModel.state.collectAsState()
    
    // 从ViewModel获取状态
    val selectedItems by homeViewModel.selectedItems.collectAsState()
    val selectedActiveCount by homeViewModel.selectedActiveCount.collectAsState()
    val widgetStateChanged by homeViewModel.widgetStateChanged.collectAsState()

    // 初始化DataStore
    LaunchedEffect(Unit) {
        homeViewModel.initDataStore(context)
    }

    // 监听权限状态变化，当权限状态改变时触发UI刷新
    LaunchedEffect(permissionState.permissions) {
        homeViewModel.notifyWidgetStateChanged()
    }

    Scaffold(
        bottomBar = {
            // 只有当有选中项目时才显示底部按钮
            if (selectedItems.isNotEmpty()) {
                BottomActionBar(
                    selectedActiveCount = selectedActiveCount,
                    onBatchStop = { homeViewModel.batchStopSelected() },
                    onBatchStart = { homeViewModel.batchStartSelected(floatWidgetItems) }
                )
            }
        }
    ) { paddingValues ->
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
            if (!permissionState.allPermissionsGranted) {
                PermissionStatusCard(
                    state = permissionState,
                    context = context,
                    onGrantPermission = { permission ->
                        mainViewModel.handleIntent(MainIntent.RequestPermission(permission))
                    }
                )
            }

            WidgetGrid(
                floatWidgetItems = floatWidgetItems,
                selectedItems = selectedItems,
                homeViewModel = homeViewModel,
                widgetStateChanged = widgetStateChanged
            )
        }
        
        // 统一的配置对话框
        WidgetConfigDialogs(
            configManager = homeViewModel.configManager,
            widgetFactory = homeViewModel.widgetFactory,
            onWidgetRecreated = {
                // Widget重新创建后触发UI更新
                homeViewModel.notifyWidgetStateChanged()
            }
        )
    }
}

@Composable
private fun BottomActionBar(
    selectedActiveCount: Int,
    onBatchStop: () -> Unit,
    onBatchStart: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            if (selectedActiveCount > 0) {
                // 有选中项目在运行，显示全部停止按钮
                Button(
                    onClick = onBatchStop,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.batch_stop_all))
                }
            } else {
                // 选中项目都没运行，显示全部运行按钮
                Button(
                    onClick = onBatchStart,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.batch_start_all))
                }
            }
        }
    }
}

@Composable
private fun WidgetGrid(
    floatWidgetItems: List<com.xmbest.floatmaster.model.FloatWidgetItem>,
    selectedItems: Set<String>,
    homeViewModel: HomeScreenViewModel,
    widgetStateChanged: Long
) {
    FlowRow(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        floatWidgetItems.fastForEach { item ->
            // 直接从ViewModel获取真实状态，widgetStateChanged确保重组
            val isActive by remember(item.id, widgetStateChanged) {
                derivedStateOf {
                    homeViewModel.isWidgetActive(item.id)
                }
            }

            ItemCard(
                item = item,
                isActive = isActive,
                configManager = homeViewModel.configManager,
                isEnabled = item.permissionChecker(),
                isSelected = selectedItems.contains(item.id),
                onToggle = { widgetItem, shouldActivate ->
                    homeViewModel.toggleWidget(widgetItem, shouldActivate)
                },
                onEditClick = {
                    homeViewModel.configManager.showConfigDialog(item.id)
                },
                onSelectionChange = { widgetItem, isSelected ->
                    homeViewModel.updateSelection(widgetItem, isSelected)
                }
            )
        }
    }
}