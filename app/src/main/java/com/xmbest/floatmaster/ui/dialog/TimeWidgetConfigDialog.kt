package com.xmbest.floatmaster.ui.dialog

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xmbest.floatmaster.R
import com.xmbest.floatmaster.model.TextProperties
import com.xmbest.floatmaster.ui.component.ConfigSectionTitle
import com.xmbest.floatmaster.ui.component.DropdownFieldWithDescription
import com.xmbest.floatmaster.ui.component.NumberInputField
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
        Triple("HH:mm:ss", "HH:mm:ss", stringResource(R.string.time_format_24h_with_seconds)),
        Triple("hh:mm:ss a", "hh:mm:ss a", stringResource(R.string.time_format_12h_with_seconds)),
        Triple("HH:mm", "HH:mm", stringResource(R.string.time_format_24h_no_seconds)),
        Triple("hh:mm a", "hh:mm a", stringResource(R.string.time_format_12h_no_seconds)),
        Triple("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss", stringResource(R.string.time_format_full)),
        Triple("MM-dd HH:mm", "MM-dd HH:mm", stringResource(R.string.time_format_short))
    )
    
    BaseConfigDialog(
        title = stringResource(R.string.config_title_time_display),
        onDismiss = onDismiss,
        onConfirm = { onConfirm(config) }
    ) {
        NumberInputField(
            value = config.refreshIntervalMs,
            onValueChange = { newInterval ->
                config = config.copy(refreshIntervalMs = newInterval)
            },
            label = stringResource(R.string.label_refresh_interval)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        DropdownFieldWithDescription(
            value = config.formatStr,
            onValueChange = { newFormat ->
                config = config.copy(formatStr = newFormat)
            },
            options = timeFormats,
            label = stringResource(R.string.label_time_format)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 时区选择
        val timezones = listOf(
            Triple("", stringResource(R.string.timezone_system_default), stringResource(R.string.timezone_system_default)),
            Triple("Asia/Shanghai", stringResource(R.string.timezone_beijing), stringResource(R.string.timezone_beijing)),
            Triple("Asia/Tokyo", stringResource(R.string.timezone_tokyo), stringResource(R.string.timezone_tokyo)),
            Triple("America/New_York", stringResource(R.string.timezone_new_york), stringResource(R.string.timezone_new_york)),
            Triple("Europe/London", stringResource(R.string.timezone_london), stringResource(R.string.timezone_london)),
            Triple("Europe/Paris", stringResource(R.string.timezone_paris), stringResource(R.string.timezone_paris)),
            Triple("UTC", stringResource(R.string.timezone_utc), stringResource(R.string.timezone_utc))
        )
        
        DropdownFieldWithDescription(
            value = config.timeZone ?: "",
            onValueChange = { newTimeZone ->
                config = config.copy(timeZone = if (newTimeZone.isBlank()) null else newTimeZone)
            },
            options = timezones,
            label = stringResource(R.string.label_timezone)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ConfigSectionTitle(stringResource(R.string.section_text_style))
        
        TextPropertiesEditor(
             properties = config.textProperties,
             onPropertiesChange = { textProps ->
                 config = config.copy(textProperties = textProps)
             }
         )
     }
}