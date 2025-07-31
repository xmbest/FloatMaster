package com.xmbest.floatmaster.ui.screen.home

import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xmbest.floatmaster.constants.WidgetConstants
import com.xmbest.floatmaster.factory.WidgetFactory
import com.xmbest.floatmaster.manager.FloatWindowManager
import com.xmbest.floatmaster.manager.WidgetConfigManager
import com.xmbest.floatmaster.model.FloatWidgetItem
import com.xmbest.floatmaster.model.Permission
import com.xmbest.floatmaster.module.DataStoreModule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    val floatWindowManager: FloatWindowManager,
    val configManager: WidgetConfigManager,
    val widgetFactory: WidgetFactory,
    private val dataStore: DataStoreModule,
    private val application: Application
) : ViewModel() {
    
    // 选中的widget项目
    private val _selectedItems = MutableStateFlow(setOf<String>())
    val selectedItems: StateFlow<Set<String>> = _selectedItems.asStateFlow()

    // Widget状态变化流
    private val _widgetStateChanged = MutableStateFlow(System.currentTimeMillis())
    val widgetStateChanged: StateFlow<Long> = _widgetStateChanged.asStateFlow()
    
    // 选中且运行中的项目数量
    val selectedActiveCount: StateFlow<Int> = combine(
        selectedItems,
        widgetStateChanged
    ) { selected, _ ->
        selected.count { itemId ->
            floatWindowManager.hasView(itemId)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )
    
    init {
        // 异步加载配置，避免阻塞初始化
        viewModelScope.launch {
            configManager.loadAllConfigs()
        }
        // 立即加载选中项目
        loadSelectedItems()
    }
    
    private fun loadSelectedItems() {
        viewModelScope.launch {
            dataStore.getSelectedWidgets().collect { savedSelectedItems ->
                // 过滤掉没有权限的选中项
                val filteredItems = filterItemsWithPermission(savedSelectedItems)
                _selectedItems.value = filteredItems
                
                // 如果过滤后的项目数量发生变化，更新DataStore
                if (filteredItems.size != savedSelectedItems.size) {
                    dataStore.saveSelectedWidgets(filteredItems)
                }
            }
        }
    }
    
    /**
     * 过滤掉没有权限的选中项
     */
    private fun filterItemsWithPermission(selectedItems: Set<String>): Set<String> {
        return selectedItems.filter { itemId ->
            checkWidgetPermission(itemId)
        }.toSet()
    }
    
    /**
     * 检查特定widget的权限
     */
    private fun checkWidgetPermission(widgetId: String): Boolean {
        // 所有widget都需要悬浮窗权限
        if (!Permission.OVERLAY.isGranted(application)) {
            return false
        }
        
        // 根据widget类型检查特定权限
        return when (widgetId) {
            com.xmbest.floatmaster.constants.WidgetConstants.WIDGET_ID_MIC_MUTE -> {
                // 麦克风widget需要录音权限
                ContextCompat.checkSelfPermission(
                    application,
                    android.Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            }
            WidgetConstants.WIDGET_ID_NETWORK_SPEED,
            WidgetConstants.WIDGET_ID_TIME_DISPLAY -> {
                // 网络速度和时间显示widget只需要悬浮窗权限
                true
            }
            else -> false
        }
    }
    
    /**
     * 权限变化时调用，移除没有权限的选中项
     */
    fun onPermissionChanged() {
        val currentSelected = _selectedItems.value
        val filteredItems = filterItemsWithPermission(currentSelected)
        
        if (filteredItems.size != currentSelected.size) {
            _selectedItems.value = filteredItems
            // 保存到DataStore
            viewModelScope.launch {
                dataStore.saveSelectedWidgets(filteredItems)
            }
            // 停止没有权限但正在运行的widget
            val removedItems = currentSelected - filteredItems
            removedItems.forEach { itemId ->
                if (floatWindowManager.hasView(itemId)) {
                    widgetFactory.deactivateWidget(itemId)
                }
            }
            notifyWidgetStateChanged()
        }
    }
    
    fun toggleWidget(widgetItem: FloatWidgetItem, shouldActivate: Boolean) {
        if (shouldActivate) {
            widgetFactory.activateWidget(widgetItem)
        } else {
            widgetFactory.deactivateWidget(widgetItem.id)
        }
        notifyWidgetStateChanged()
    }
    
    fun updateSelection(widgetItem: FloatWidgetItem, isSelected: Boolean) {
        val newSelectedItems = if (isSelected) {
            _selectedItems.value + widgetItem.id
        } else {
            _selectedItems.value - widgetItem.id
        }
        _selectedItems.value = newSelectedItems
        
        // 保存选中状态到DataStore
        viewModelScope.launch {
            dataStore.saveSelectedWidgets(newSelectedItems)
        }
    }
    
    fun batchStopSelected() {
        _selectedItems.value.forEach { itemId ->
            if (floatWindowManager.hasView(itemId)) {
                widgetFactory.deactivateWidget(itemId)
            }
        }
        notifyWidgetStateChanged()
    }
    
    fun batchStartSelected(floatWidgetItems: List<FloatWidgetItem>) {
        _selectedItems.value.forEach { itemId ->
            val item = floatWidgetItems.find { it.id == itemId }
            item?.let {
                if (!floatWindowManager.hasView(itemId)) {
                    widgetFactory.activateWidget(it)
                }
            }
        }
        notifyWidgetStateChanged()
    }
    
    fun notifyWidgetStateChanged() {
        _widgetStateChanged.value = System.currentTimeMillis()
    }
    
    fun isWidgetActive(widgetId: String): Boolean {
        return floatWindowManager.hasView(widgetId)
    }
}