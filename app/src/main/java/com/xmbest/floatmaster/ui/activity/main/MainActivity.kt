package com.xmbest.floatmaster.ui.activity.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.xmbest.floatmaster.manager.PermissionManager
import com.xmbest.floatmaster.model.Permission
import com.xmbest.floatmaster.ui.screen.MainScreen
import com.xmbest.floatmaster.ui.theme.FloatMasterTheme
import com.xmbest.floatmaster.utils.StartupPerformanceTracker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var permissionManager: PermissionManager

    // 跟踪当前请求的权限
    private var currentRequestedPermission: Permission? = null

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        StartupPerformanceTracker.mark("main_activity_create_start")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        StartupPerformanceTracker.mark("ui_setup_start")
        // 立即设置UI，提升启动体验
        setContent {
            val state by viewModel.state.collectAsState()
            val snackbarHostState = remember { SnackbarHostState() }

            FloatMasterTheme {
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    MainScreen()
                }
            }
        }

        StartupPerformanceTracker.mark("ui_setup_complete")
        
        // 延迟初始化非关键组件，避免阻塞UI渲染
        lifecycleScope.launch {
            delay(100) // 让UI先渲染
            StartupPerformanceTracker.mark("delayed_init_start")
            initializeComponents()
            StartupPerformanceTracker.mark("delayed_init_complete")
            StartupPerformanceTracker.printReport()
        }
    }

    /**
     * 初始化非关键组件
     */
    private suspend fun initializeComponents() {
        StartupPerformanceTracker.mark("permission_manager_init_start")
        // 初始化权限管理器
        permissionManager = PermissionManager(this@MainActivity)
        StartupPerformanceTracker.mark("permission_manager_init_complete")

        StartupPerformanceTracker.mark("permission_check_start")
        // 初始检查权限
        viewModel.handleIntent(MainIntent.CheckPermissions)
        StartupPerformanceTracker.mark("permission_check_complete")

        // 请求所需权限
        permissionManager.requestPermissionsIfNeeded(
            normalPermissionLauncher = requestPermissionLauncher,
            overlayPermissionLauncher = overlayPermissionLauncher
        )

        // 监听权限请求事件
        viewModel.permissionRequest.collect { permission ->
            handlePermissionRequest(permission)
        }
    }

    override fun onResume() {
        super.onResume()
        // 从设置页面返回时重新检查权限状态
        viewModel.handleIntent(MainIntent.CheckPermissions)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // 使用当前请求的权限
        currentRequestedPermission?.let { permission ->
            viewModel.handleIntent(
                MainIntent.OnPermissionResult(
                    permission = permission,
                    isGranted = isGranted
                )
            )
            currentRequestedPermission = null
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        val isGranted = Permission.OVERLAY.isGranted(this)
        viewModel.handleIntent(
            MainIntent.OnPermissionResult(
                permission = Permission.OVERLAY,
                isGranted = isGranted
            )
        )
    }
    
    private fun handlePermissionRequest(permission: Permission) {
        currentRequestedPermission = permission
        permissionManager.requestPermissionSmart(
            permission = permission,
            normalPermissionLauncher = requestPermissionLauncher,
            overlayPermissionLauncher = overlayPermissionLauncher
        )
    }
}
