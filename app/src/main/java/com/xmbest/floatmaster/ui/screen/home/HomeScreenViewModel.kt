package com.xmbest.floatmaster.ui.screen.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xmbest.floatmaster.manager.FloatWindowManager
import com.xmbest.floatmaster.manager.WidgetConfigManager
import com.xmbest.floatmaster.factory.WidgetFactory
import com.xmbest.floatmaster.model.FloatWidgetItem
import com.xmbest.floatmaster.module.DataStoreModule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    val floatWindowManager: FloatWindowManager,
    val configManager: WidgetConfigManager,
    val widgetFactory: WidgetFactory
) : ViewModel() {
    
    private lateinit var dataStore: DataStoreModule
    
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
        // 在ViewModel初始化时加载所有配置
        configManager.loadAllConfigs()
    }
    
    fun initDataStore(context: Context) {
        if (!::dataStore.isInitialized) {
            dataStore = DataStoreModule(context)
            loadSelectedItems()
        }
    }
    
    private fun loadSelectedItems() {
        viewModelScope.launch {
            dataStore.getSelectedWidgets().collect { savedSelectedItems ->
                _selectedItems.value = savedSelectedItems
            }
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