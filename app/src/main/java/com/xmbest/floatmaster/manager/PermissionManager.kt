package com.xmbest.floatmaster.manager

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.core.net.toUri
import com.xmbest.floatmaster.model.Permission

/**
 * 权限管理器，统一处理权限申请逻辑
 */
class PermissionManager(private val activity: Activity) {

    /**
     * 检查并申请所有需要的权限
     */
    fun requestPermissionsIfNeeded(
        normalPermissionLauncher: ActivityResultLauncher<String>,
        overlayPermissionLauncher: ActivityResultLauncher<Intent>
    ) {
        Permission.getAllPermissions().forEach { permission ->
            if (!permission.isGranted(activity)) {
                requestPermission(permission, normalPermissionLauncher, overlayPermissionLauncher)
            }
        }
    }

    /**
     * 申请单个权限
     */
    private fun requestPermission(
        permission: Permission,
        normalPermissionLauncher: ActivityResultLauncher<String>,
        overlayPermissionLauncher: ActivityResultLauncher<Intent>
    ) {
        when (permission) {
            Permission.RECORD_AUDIO -> {
                normalPermissionLauncher.launch(permission.permissionName)
            }
            Permission.OVERLAY -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        "package:${activity.packageName}".toUri()
                    )
                    overlayPermissionLauncher.launch(intent)
                }
            }
        }
    }

    /**
     * 检查所有权限状态
     */
    fun checkAllPermissions(): Map<Permission, Boolean> {
        return Permission.getAllPermissions().associateWith { permission ->
            permission.isGranted(activity)
        }
    }

    /**
     * 获取未授予的权限列表
     */
    fun getMissingPermissions(): List<Permission> {
        return Permission.getAllPermissions().filter { !it.isGranted(activity) }
    }

    /**
     * 检查是否所有权限都已授予
     */
    fun areAllPermissionsGranted(): Boolean {
        return Permission.getAllPermissions().all { it.isGranted(activity) }
    }
}