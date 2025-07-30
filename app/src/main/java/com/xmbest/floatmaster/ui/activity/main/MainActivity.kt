package com.xmbest.floatmaster.ui.activity.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xmbest.floatmaster.manager.PermissionManager
import com.xmbest.floatmaster.model.Permission
import com.xmbest.floatmaster.ui.component.PermissionStatusCard
import com.xmbest.floatmaster.ui.screen.MainScreen
import com.xmbest.floatmaster.ui.theme.FloatMasterTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var permissionManager: PermissionManager

    // 跟踪当前请求的权限
    private var currentRequestedPermission: Permission? = null

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 初始化权限管理器
        permissionManager = PermissionManager(this)

        // 初始检查权限
        viewModel.handleIntent(MainIntent.CheckPermissions)

        // 请求所需权限
        permissionManager.requestPermissionsIfNeeded(
            normalPermissionLauncher = requestPermissionLauncher,
            overlayPermissionLauncher = overlayPermissionLauncher
        )

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
}
