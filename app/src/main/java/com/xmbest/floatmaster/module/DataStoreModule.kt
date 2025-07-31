package com.xmbest.floatmaster.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "floatMaster")

/**
 * Preferences DataStore实现
 * <a href="https://developer.android.google.cn/topic/libraries/architecture/datastore?hl=zh-cn#kotlin>datastore</a>
 */
class DataStoreModule(private val context: Context) {
    companion object {
        // Widget位置信息
        fun getWidgetXKey(widgetId: String) = floatPreferencesKey("widget_${widgetId}_x")
        fun getWidgetYKey(widgetId: String) = floatPreferencesKey("widget_${widgetId}_y")
        fun getWidgetWidthKey(widgetId: String) = floatPreferencesKey("widget_${widgetId}_width")
        fun getWidgetHeightKey(widgetId: String) = floatPreferencesKey("widget_${widgetId}_height")
        
        // Widget配置信息
        fun getWidgetTextColorKey(widgetId: String) = longPreferencesKey("widget_${widgetId}_text_color")
        fun getWidgetBackgroundColorKey(widgetId: String) = longPreferencesKey("widget_${widgetId}_bg_color")
        fun getWidgetTextSizeKey(widgetId: String) = floatPreferencesKey("widget_${widgetId}_text_size")
        fun getWidgetRefreshIntervalKey(widgetId: String) = longPreferencesKey("widget_${widgetId}_refresh_interval")
        fun getWidgetFormatKey(widgetId: String) = stringPreferencesKey("widget_${widgetId}_format")
        fun getWidgetTimeZoneKey(widgetId: String) = stringPreferencesKey("widget_${widgetId}_timezone")
        
        // 选中状态信息
        private val SELECTED_WIDGETS_KEY = stringSetPreferencesKey("selected_widgets")
    }

    suspend fun <T> setValue(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit {
            it[key] = value
        }
    }

    fun <T> getValue(key: Preferences.Key<T>, defaultValue: T) =
        context.dataStore.data.map { it[key] ?: defaultValue }
    
    // 保存Widget位置信息
    suspend fun saveWidgetPosition(widgetId: String, x: Float, y: Float, width: Float, height: Float) {
        context.dataStore.edit { preferences ->
            preferences[getWidgetXKey(widgetId)] = x
            preferences[getWidgetYKey(widgetId)] = y
            preferences[getWidgetWidthKey(widgetId)] = width
            preferences[getWidgetHeightKey(widgetId)] = height
        }
    }
    
    // 获取Widget位置信息
    fun getWidgetPosition(widgetId: String): Flow<Map<String, Float>> {
        return context.dataStore.data.map { preferences ->
            mapOf(
                "x" to (preferences[getWidgetXKey(widgetId)] ?: 100f),
                "y" to (preferences[getWidgetYKey(widgetId)] ?: 200f),
                "width" to (preferences[getWidgetWidthKey(widgetId)] ?: 120f),
                "height" to (preferences[getWidgetHeightKey(widgetId)] ?: 80f)
            )
        }
    }
    
    // 保存Widget配置信息
    suspend fun saveWidgetConfig(widgetId: String, config: Map<String, Any>) {
        context.dataStore.edit { preferences ->
            config.forEach { (key, value) ->
                when (value) {
                    is Long -> preferences[longPreferencesKey("widget_${widgetId}_$key")] = value
                    is Float -> preferences[floatPreferencesKey("widget_${widgetId}_$key")] = value
                    is String -> preferences[stringPreferencesKey("widget_${widgetId}_$key")] = value
                }
            }
        }
    }
    
    // 获取Widget配置信息
    fun getWidgetConfig(widgetId: String, configKeys: List<Pair<String, Any>>): Flow<Map<String, Any>> {
        return context.dataStore.data.map { preferences ->
            configKeys.associate { (key, defaultValue) ->
                val prefKey = when (defaultValue) {
                    is Long -> longPreferencesKey("widget_${widgetId}_$key")
                    is Float -> floatPreferencesKey("widget_${widgetId}_$key")
                    is String -> stringPreferencesKey("widget_${widgetId}_$key")
                    else -> throw IllegalArgumentException("Unsupported type: ${defaultValue::class}")
                }
                key to (preferences[prefKey] ?: defaultValue)
            }
        }
    }
    
    // 保存选中的Widget列表
    suspend fun saveSelectedWidgets(selectedWidgetIds: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_WIDGETS_KEY] = selectedWidgetIds
        }
    }
    
    // 获取选中的Widget列表
    fun getSelectedWidgets(): Flow<Set<String>> {
        return context.dataStore.data.map { preferences ->
            preferences[SELECTED_WIDGETS_KEY] ?: emptySet()
        }
    }
}