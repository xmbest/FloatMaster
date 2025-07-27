package com.xmbest.floatmaster.ui.activity.main

import com.xmbest.floatmaster.model.Permission

data class MainState(
    val permissions: Map<Permission, Boolean> = Permission.getAllPermissions().associateWith { false },
    val permissionMessage: String = "",
    val showPermissionMessage: Boolean = false,
    val isLoading: Boolean = false
) {
    val allPermissionsGranted: Boolean
        get() = permissions.values.all { it }

    /**
     * 检查指定权限是否已授予
     */
    fun hasPermission(permission: Permission): Boolean {
        return permissions[permission] ?: false
    }

    /**
     * 获取未授予的权限列表
     */
    fun getMissingPermissions(): List<Permission> {
        return permissions.filter { !it.value }.keys.toList()
    }

    /**
     * 更新指定权限状态
     */
    fun updatePermission(permission: Permission, isGranted: Boolean): MainState {
        return copy(permissions = permissions.toMutableMap().apply { put(permission, isGranted) })
    }
}