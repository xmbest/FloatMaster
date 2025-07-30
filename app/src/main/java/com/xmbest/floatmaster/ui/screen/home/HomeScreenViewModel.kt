package com.xmbest.floatmaster.ui.screen.home

import androidx.lifecycle.ViewModel
import com.xmbest.floatmaster.manager.FloatWindowManager
import com.xmbest.floatmaster.manager.WidgetConfigManager
import com.xmbest.floatmaster.factory.WidgetFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    val floatWindowManager: FloatWindowManager,
    val configManager: WidgetConfigManager,
    val widgetFactory: WidgetFactory
) : ViewModel() {
    
    init {
        // 在ViewModel初始化时加载所有配置
        configManager.loadAllConfigs()
    }
}