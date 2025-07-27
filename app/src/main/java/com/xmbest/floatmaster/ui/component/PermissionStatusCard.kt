package com.xmbest.floatmaster.ui.component

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xmbest.floatmaster.R
import com.xmbest.floatmaster.model.Permission
import com.xmbest.floatmaster.ui.activity.main.MainState

/**
 * 权限状态显示卡片组件
 * @param state 主界面状态
 * @param context 上下文，用于获取权限显示名称
 * @param onGrantPermission 权限授权回调，参数为需要授权的权限
 */
@Composable
fun PermissionStatusCard(
    state: MainState,
    context: Context,
    onGrantPermission: (Permission) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (state.allPermissionsGranted)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.permission_status),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Permission.getAllPermissions().filter { !state.hasPermission(it) }.forEach { permission ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = permission.getDisplayName(context),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = { onGrantPermission(permission) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.grant_permission),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                if (permission != Permission.getAllPermissions().last { !state.hasPermission(it) }) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}