package com.xmbest.floatmaster.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.xmbest.floatmaster.model.TextProperties
import com.xmbest.floatmaster.ui.component.TextPropertiesEditor
import com.xmbest.floatmaster.ui.widget.NetworkSpeedMode

/**
 * NetworkSpeedWidget专用配置数据类
 */
data class NetworkSpeedWidgetConfig(
    val refreshIntervalMs: Long = 1000L,
    val mode: NetworkSpeedMode = NetworkSpeedMode.DOWNLOAD_ONLY,
    val textProperties: TextProperties = TextProperties()
)

/**
 * NetworkSpeedWidget配置对话框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkSpeedWidgetConfigDialog(
    initialConfig: NetworkSpeedWidgetConfig,
    onDismiss: () -> Unit,
    onConfirm: (NetworkSpeedWidgetConfig) -> Unit
) {
    var config by remember { mutableStateOf(initialConfig) }
    var refreshInterval by remember { mutableLongStateOf(initialConfig.refreshIntervalMs) }
    var mode by remember { mutableStateOf(initialConfig.mode) }
    
    val displayModes = listOf(
        NetworkSpeedMode.BOTH to "上传+下载",
        NetworkSpeedMode.DOWNLOAD_ONLY to "仅下载",
        NetworkSpeedMode.UPLOAD_ONLY to "仅上传"
    )
    
    var modeExpanded by remember { mutableStateOf(false) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "网络速度显示配置",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // 刷新间隔
                OutlinedTextField(
                    value = refreshInterval.toString(),
                    onValueChange = { 
                        val newInterval = it.toLongOrNull() ?: 1000L
                        refreshInterval = newInterval
                        config = config.copy(refreshIntervalMs = newInterval)
                    },
                    label = { Text("刷新间隔 (毫秒)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 显示模式选择
                ExposedDropdownMenuBox(
                    expanded = modeExpanded,
                    onExpandedChange = { modeExpanded = !modeExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = displayModes.find { it.first == mode }?.second ?: "上传+下载",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("显示模式") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = modeExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = modeExpanded,
                        onDismissRequest = { modeExpanded = false }
                    ) {
                        displayModes.forEach { (modeValue, description) ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(description)
                                        Text(
                                            text = when(modeValue) {
                                                NetworkSpeedMode.BOTH -> "显示上传和下载速度"
                                                NetworkSpeedMode.DOWNLOAD_ONLY -> "仅显示下载速度"
                                                NetworkSpeedMode.UPLOAD_ONLY -> "仅显示上传速度"
                                            },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                onClick = {
                                    mode = modeValue
                                    config = config.copy(mode = modeValue)
                                    modeExpanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 文本属性编辑
                Text(
                    text = "文本样式",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                TextPropertiesEditor(
                    properties = config.textProperties,
                    onPropertiesChange = { textProps ->
                        config = config.copy(textProperties = textProps)
                    }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("取消")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onConfirm(config) }
                    ) {
                        Text("确认")
                    }
                }
            }
        }
    }
}