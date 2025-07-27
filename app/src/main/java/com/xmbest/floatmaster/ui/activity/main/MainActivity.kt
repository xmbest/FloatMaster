package com.xmbest.floatmaster.ui.activity.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.xmbest.floatmaster.R
import com.xmbest.floatmaster.manager.PermissionManager
import com.xmbest.floatmaster.model.Permission
import com.xmbest.floatmaster.ui.theme.FloatMasterTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var permissionManager: PermissionManager

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
            
            // 监听权限消息状态
            LaunchedEffect(state.showPermissionMessage, state.permissionMessage) {
                if (state.showPermissionMessage && state.permissionMessage.isNotBlank()) {
                    snackbarHostState.showSnackbar(state.permissionMessage)
                    viewModel.handleIntent(MainIntent.DismissPermissionMessage)
                }
            }

            FloatMasterTheme {
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        TopAppBar(
                            title = { Text(stringResource(R.string.app_title)) }
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
//                        Text(stringResource(R.string.hello))
                    }
                }
            }
        }
    }




    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // 根据权限名称找到对应的Permission枚举
        Permission.fromPermissionName(Permission.RECORD_AUDIO.permissionName)?.let { permission ->
            viewModel.handleIntent(
                MainIntent.OnPermissionResult(
                    permission = permission,
                    isGranted = isGranted
                )
            )
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
