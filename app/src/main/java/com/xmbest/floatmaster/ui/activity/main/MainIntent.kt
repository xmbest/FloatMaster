package com.xmbest.floatmaster.ui.activity.main

import com.xmbest.floatmaster.model.Permission

sealed class MainIntent {
    object CheckPermissions : MainIntent()
    data class OnPermissionResult(val permission: Permission, val isGranted: Boolean) : MainIntent()
    object DismissPermissionMessage : MainIntent()
}