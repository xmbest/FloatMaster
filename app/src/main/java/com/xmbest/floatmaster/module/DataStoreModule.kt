package com.xmbest.floatmaster.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "floatMaster")

/**
 * Preferences DataStore实现
 * <a href="https://developer.android.google.cn/topic/libraries/architecture/datastore?hl=zh-cn#kotlin>datastore</a>
 */
class DataStoreModule(private val context: Context) {
    companion object {
        //TODO 待具体实现
        val todo = intPreferencesKey("todo")
    }

    suspend fun <T> setValue(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit {
            it[key] = value
        }
    }

    fun <T> getValue(key: Preferences.Key<T>, defaultValue: T) =
        context.dataStore.data.map { it[key] ?: defaultValue }
}