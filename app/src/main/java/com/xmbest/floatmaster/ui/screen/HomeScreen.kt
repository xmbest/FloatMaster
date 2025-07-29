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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.xmbest.floatmaster.manager.FloatWindowManager
import com.xmbest.floatmaster.model.getFloatWidgetItems
import com.xmbest.floatmaster.ui.component.ItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val floatWindowManager = remember { FloatWindowManager(context) }
    val floatWidgetItems = getFloatWidgetItems()

    // 记录每个功能项的激活状态
    val activeStates = remember { mutableStateMapOf<String, Boolean>() }

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
                            // 添加悬浮窗
                            floatWindowManager.addComposeView(
                                id = widgetItem.id,
                                content = widgetItem.widgetComposer,
                                startX = 100,
                                startY = 100,
                            )
                            activeStates[widgetItem.id] = true
                        } else {
                            // 移除悬浮窗
                            floatWindowManager.removeViewById(widgetItem.id)
                            activeStates[widgetItem.id] = false
                        }
                    },
                    onEditClick = {

                    }
                )
            }
        }
    }
}