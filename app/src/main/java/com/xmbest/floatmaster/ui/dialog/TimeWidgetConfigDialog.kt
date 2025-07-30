package com.xmbest.floatmaster.ui.dialog

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xmbest.floatmaster.model.TextProperties
import com.xmbest.floatmaster.ui.component.ConfigSectionTitle
import com.xmbest.floatmaster.ui.component.DropdownFieldWithDescription
import com.xmbest.floatmaster.ui.component.NumberInputField
import com.xmbest.floatmaster.ui.component.TextInputField
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
@Composable
fun TimeWidgetConfigDialog(
    initialConfig: TimeWidgetConfig,
    onDismiss: () -> Unit,
    onConfirm: (TimeWidgetConfig) -> Unit
) {
    var config by remember { mutableStateOf(initialConfig) }
    
    // 预设时间格式
    val timeFormats = listOf(
        Triple("HH:mm:ss", "HH:mm:ss", "24小时制 (14:30:25)"),
        Triple("hh:mm:ss a", "hh:mm:ss a", "12小时制 (02:30:25 PM)"),
        Triple("HH:mm", "HH:mm", "24小时制无秒 (14:30)"),
        Triple("hh:mm a", "hh:mm a", "12小时制无秒 (02:30 PM)"),
        Triple("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss", "完整日期时间"),
        Triple("MM-dd HH:mm", "MM-dd HH:mm", "月日时分")
    )
    
    BaseConfigDialog(
        title = "时间显示配置",
        onDismiss = onDismiss,
        onConfirm = { onConfirm(config) }
    ) {
        NumberInputField(
            value = config.refreshIntervalMs,
            onValueChange = { newInterval ->
                config = config.copy(refreshIntervalMs = newInterval)
            },
            label = "刷新间隔 (毫秒)"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        DropdownFieldWithDescription(
            value = config.formatStr,
            onValueChange = { newFormat ->
                config = config.copy(formatStr = newFormat)
            },
            options = timeFormats,
            label = "时间格式"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextInputField(
            value = config.timeZone ?: "",
            onValueChange = { newTimeZone ->
                config = config.copy(timeZone = if (newTimeZone.isBlank()) null else newTimeZone)
            },
            label = "时区 (可选，如: Asia/Shanghai)"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ConfigSectionTitle("文本样式")
        
        TextPropertiesEditor(
             properties = config.textProperties,
             onPropertiesChange = { textProps ->
                 config = config.copy(textProperties = textProps)
             }
         )
     }
}