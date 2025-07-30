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

/**
 * TimeWidget专用配置数据类
 */
data class TimeWidgetConfig(
    val refreshIntervalMs: Long = 1000L,
    val formatStr: String = "HH:mm:ss",
    val timeZone: String? = null,
    val textProperties: TextProperties = TextProperties()
)

/**
 * TimeWidget配置对话框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeWidgetConfigDialog(
    initialConfig: TimeWidgetConfig,
    onDismiss: () -> Unit,
    onConfirm: (TimeWidgetConfig) -> Unit
) {
    var config by remember { mutableStateOf(initialConfig) }
    var refreshInterval by remember { mutableLongStateOf(initialConfig.refreshIntervalMs) }
    var formatStr by remember { mutableStateOf(initialConfig.formatStr) }
    var timeZone by remember { mutableStateOf(initialConfig.timeZone ?: "") }
    
    // 预设时间格式
    val timeFormats = listOf(
        "HH:mm:ss" to "24小时制 (14:30:25)",
        "hh:mm:ss a" to "12小时制 (02:30:25 PM)",
        "HH:mm" to "24小时制无秒 (14:30)",
        "hh:mm a" to "12小时制无秒 (02:30 PM)",
        "yyyy-MM-dd HH:mm:ss" to "完整日期时间",
        "MM-dd HH:mm" to "月日时分"
    )
    
    var formatExpanded by remember { mutableStateOf(false) }
    
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
                    text = "时间显示配置",
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
                
                // 时间格式选择
                ExposedDropdownMenuBox(
                    expanded = formatExpanded,
                    onExpandedChange = { formatExpanded = !formatExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = formatStr,
                        onValueChange = { 
                            formatStr = it
                            config = config.copy(formatStr = it)
                        },
                        label = { Text("时间格式") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = formatExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = formatExpanded,
                        onDismissRequest = { formatExpanded = false }
                    ) {
                        timeFormats.forEach { (format, description) ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(format)
                                        Text(
                                            text = description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                onClick = {
                                    formatStr = format
                                    config = config.copy(formatStr = format)
                                    formatExpanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 时区设置
                OutlinedTextField(
                    value = timeZone,
                    onValueChange = { 
                        timeZone = it
                        config = config.copy(timeZone = if (it.isBlank()) null else it)
                    },
                    label = { Text("时区 (可选，如: Asia/Shanghai)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
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