package com.xmbest.floatmaster.ui.dialog

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xmbest.floatmaster.R
import com.xmbest.floatmaster.model.TextProperties
import com.xmbest.floatmaster.ui.component.ConfigSectionTitle
import com.xmbest.floatmaster.ui.component.DropdownFieldWithDescription
import com.xmbest.floatmaster.ui.component.NumberInputField
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
@Composable
fun NetworkSpeedWidgetConfigDialog(
    initialConfig: NetworkSpeedWidgetConfig,
    onDismiss: () -> Unit,
    onConfirm: (NetworkSpeedWidgetConfig) -> Unit
) {
    var config by remember { mutableStateOf(initialConfig) }
    
    val displayModes = listOf(
        Triple(NetworkSpeedMode.BOTH, "上传+下载", "显示上传和下载速度"),
        Triple(NetworkSpeedMode.DOWNLOAD_ONLY, "仅下载", "仅显示下载速度"),
        Triple(NetworkSpeedMode.UPLOAD_ONLY, "仅上传", "仅显示上传速度")
    )
    
    BaseConfigDialog(
        title = "网络速度显示配置",
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
            value = config.mode,
            onValueChange = { newMode ->
                config = config.copy(mode = newMode)
            },
            options = displayModes,
            label = stringResource(R.string.config_display_mode)
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