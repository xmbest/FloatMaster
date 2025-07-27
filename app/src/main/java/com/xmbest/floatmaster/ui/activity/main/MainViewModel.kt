package com.xmbest.floatmaster.ui.activity.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.xmbest.floatmaster.model.Permission

@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    fun handleIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.CheckPermissions -> checkPermissions()
            is MainIntent.OnPermissionResult -> handlePermissionResult(
                intent.permission,
                intent.isGranted
            )
        }
    }

    private fun checkPermissions() {
        viewModelScope.launch {
            val updatedPermissions = Permission.getAllPermissions().associateWith { permission ->
                permission.isGranted(application)
            }
            _state.value = _state.value.copy(permissions = updatedPermissions)
        }
    }


    private fun handlePermissionResult(permission: Permission, isGranted: Boolean) {
        _state.value = _state.value.updatePermission(permission, isGranted)
    }
}