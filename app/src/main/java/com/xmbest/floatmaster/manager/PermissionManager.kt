package com.xmbest.floatmaster.manager

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.xmbest.floatmaster.model.Permission

/**
 * 权限管理器，统一处理权限申请逻辑
 */
class PermissionManager(private val activity: Activity) {
    
    companion object {
        private const val TAG = "PermissionManager"
        // 权限状态缓存，避免重复检查
        private val permissionCache = mutableMapOf<String, Boolean>()
        private var lastCacheTime = 0L
        private const val CACHE_DURATION = 5000L // 5秒缓存
    }

    // 用于跟踪权限是否已经被请求过
    private val requestedPermissions = mutableSetOf<String>()

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
        requestSpecificPermission(permission, normalPermissionLauncher, overlayPermissionLauncher)
    }

    /**
     * 检查是否有悬浮窗权限
     */
    fun hasOverlayPermission(): Boolean {
        val cacheKey = "overlay_permission"
        val currentTime = System.currentTimeMillis()
        
        // 检查缓存
        if (currentTime - lastCacheTime < CACHE_DURATION && permissionCache.containsKey(cacheKey)) {
            return permissionCache[cacheKey] ?: false
        }
        
        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(activity)
        } else {
            true
        }
        
        // 更新缓存
        permissionCache[cacheKey] = hasPermission
        lastCacheTime = currentTime
        
        return hasPermission
    }

    /**
     * 检查是否有指定权限
     */
    fun hasPermission(permission: String): Boolean {
        val currentTime = System.currentTimeMillis()
        
        // 检查缓存
        if (currentTime - lastCacheTime < CACHE_DURATION && permissionCache.containsKey(permission)) {
            return permissionCache[permission] ?: false
        }
        
        val hasPermission = ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
        
        // 更新缓存
        permissionCache[permission] = hasPermission
        lastCacheTime = currentTime
        
        return hasPermission
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

    /**
     * 申请悬浮窗权限
     */
    @SuppressLint("ObsoleteSdkInt")
    fun requestOverlayPermission(overlayPermissionLauncher: ActivityResultLauncher<Intent>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                "package:${activity.packageName}".toUri()
            )
            overlayPermissionLauncher.launch(intent)
        }
    }

    /**
     * 申请普通权限
     */
    fun requestNormalPermission(
        permissionName: String,
        normalPermissionLauncher: ActivityResultLauncher<String>
    ) {
        // 记录权限已被请求
        requestedPermissions.add(permissionName)
        normalPermissionLauncher.launch(permissionName)
    }
    
    /**
     * 申请普通权限（带权限对象）
     */
    fun requestNormalPermission(
        permission: Permission,
        normalPermissionLauncher: ActivityResultLauncher<String>,
        onPermissionRequested: (Permission) -> Unit
    ) {
        // 记录权限已被请求
        requestedPermissions.add(permission.permissionName)
        // 通知调用者当前请求的权限
        onPermissionRequested(permission)
        normalPermissionLauncher.launch(permission.permissionName)
    }

    /**
     * 申请指定权限（统一入口）
     */
    fun requestSpecificPermission(
        permission: Permission,
        normalPermissionLauncher: ActivityResultLauncher<String>,
        overlayPermissionLauncher: ActivityResultLauncher<Intent>
    ) {
        when (permission) {
            Permission.RECORD_AUDIO -> {
                // 检查权限是否被永久拒绝
                if (isPermissionPermanentlyDenied(permission.permissionName)) {
                    openAppSettings()
                } else {
                    requestNormalPermission(permission.permissionName, normalPermissionLauncher)
                }
            }
            Permission.OVERLAY -> {
                requestOverlayPermission(overlayPermissionLauncher)
            }
        }
    }

    /**
     * 检查权限是否被永久拒绝（用户选择了"不再询问"）
     */
    fun isPermissionPermanentlyDenied(permissionName: String): Boolean {
        // 权限未被授予
        val isPermissionDenied = ActivityCompat.checkSelfPermission(activity, permissionName) == PackageManager.PERMISSION_DENIED
        // 不应该显示权限说明（用户选择了不再询问，或者从未请求过）
        val shouldNotShowRationale = !ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionName)
        // 权限已经被请求过（避免首次启动时的误判）
        val hasBeenRequested = requestedPermissions.contains(permissionName)
        
        return isPermissionDenied && shouldNotShowRationale && hasBeenRequested
    }

    /**
     * 跳转到应用设置页面
     */
    fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", activity.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        activity.startActivity(intent)
    }

    /**
     * 智能权限申请（会自动判断是否需要跳转到设置）
     */
    fun requestPermissionSmart(
        permission: Permission,
        normalPermissionLauncher: ActivityResultLauncher<String>,
        overlayPermissionLauncher: ActivityResultLauncher<Intent>,
        onPermissionRequested: ((Permission) -> Unit)? = null
    ) {
        if (permission.isGranted(activity)) {
            return // 权限已授予，无需申请
        }
        
        when (permission) {
            Permission.RECORD_AUDIO -> {
                if (isPermissionPermanentlyDenied(permission.permissionName)) {
                    openAppSettings()
                } else {
                    if (onPermissionRequested != null) {
                        requestNormalPermission(permission, normalPermissionLauncher, onPermissionRequested)
                    } else {
                        requestNormalPermission(permission.permissionName, normalPermissionLauncher)
                    }
                }
            }
            Permission.OVERLAY -> {
                requestOverlayPermission(overlayPermissionLauncher)
            }
        }
    }
}