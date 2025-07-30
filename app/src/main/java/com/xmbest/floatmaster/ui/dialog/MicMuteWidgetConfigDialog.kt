package com.xmbest.floatmaster.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.xmbest.floatmaster.R
import com.xmbest.floatmaster.model.ImageProperties
import com.xmbest.floatmaster.ui.component.ConfigSectionTitle
import com.xmbest.floatmaster.ui.component.ImagePropertiesEditor

/**
 * MicMuteWidget专用配置数据类
 */
data class MicMuteWidgetConfig(
    val imageProperties: ImageProperties = ImageProperties()
)

/**
 * MicMuteWidget配置对话框
 */
@Composable
fun MicMuteWidgetConfigDialog(
    initialConfig: MicMuteWidgetConfig,
    onDismiss: () -> Unit,
    onConfirm: (MicMuteWidgetConfig) -> Unit
) {
    var config by remember { mutableStateOf(initialConfig) }
    
    BaseConfigDialog(
        title = stringResource(R.string.config_title_mic_mute),
        onDismiss = onDismiss,
        onConfirm = { onConfirm(config) }
    ) {
        ConfigSectionTitle(stringResource(R.string.section_appearance_style))
        
        ImagePropertiesEditor(
            properties = config.imageProperties,
            onPropertiesChange = { imageProps ->
                config = config.copy(imageProperties = imageProps)
            }
        )
    }
}