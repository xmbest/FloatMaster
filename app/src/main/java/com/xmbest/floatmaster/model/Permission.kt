package com.xmbest.floatmaster.model

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.xmbest.floatmaster.R
import com.xmbest.floatmaster.manager.PermissionManager

/**
 * 权限枚举类，统一管理所有权限
 */
enum class Permission(
    val permissionName: String,
    val displayNameResId: Int,
    val isSpecialPermission: Boolean = false
) {
    RECORD_AUDIO(
        permissionName = Manifest.permission.RECORD_AUDIO,
        displayNameResId = R.string.audio_permission
    ),
    OVERLAY(
        permissionName = "android.permission.SYSTEM_ALERT_WINDOW",
        displayNameResId = R.string.overlay_permission,
        isSpecialPermission = true
    );

    /**
     * 检查权限是否已授予
     * 优化版本：使用缓存避免重复检查
     */
    @SuppressLint("ObsoleteSdkInt")
    fun isGranted(context: Context): Boolean {
        // 尝试使用PermissionManager的缓存机制
        if (context is Activity) {
            return try {
                val permissionManager = PermissionManager(context)
                when (this) {
                    RECORD_AUDIO -> permissionManager.hasPermission(permissionName)
                    OVERLAY -> permissionManager.hasOverlayPermission()
                }
            } catch (e: Exception) {
                // 如果出错，回退到原始检查方式
                fallbackPermissionCheck(context)
            }
        } else {
            // 非Activity上下文，使用原始检查方式
            return fallbackPermissionCheck(context)
        }
    }
    
    /**
     * 原始权限检查方式（作为备用）
     */
    @SuppressLint("ObsoleteSdkInt")
    private fun fallbackPermissionCheck(context: Context): Boolean {
        return when (this) {
            RECORD_AUDIO -> {
                ContextCompat.checkSelfPermission(
                    context,
                    permissionName
                ) == PackageManager.PERMISSION_GRANTED
            }
            OVERLAY -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Settings.canDrawOverlays(context)
                } else {
                    true
                }
            }
        }
    }

    /**
     * 获取权限显示名称
     */
    fun getDisplayName(context: Context): String {
        return context.getString(displayNameResId)
    }

    companion object {
        /**
         * 根据权限名称获取权限枚举
         */
        fun fromPermissionName(permissionName: String): Permission? {
            return Permission.entries.find { it.permissionName == permissionName }
        }

        /**
         * 获取所有权限
         */
        fun getAllPermissions(): List<Permission> {
            return Permission.entries
        }

        /**
         * 获取所有普通权限（非特殊权限）
         */
        fun getNormalPermissions(): List<Permission> {
            return Permission.entries.filter { !it.isSpecialPermission }
        }

        /**
         * 获取所有特殊权限
         */
        fun getSpecialPermissions(): List<Permission> {
            return Permission.entries.filter { it.isSpecialPermission }
        }
    }
}