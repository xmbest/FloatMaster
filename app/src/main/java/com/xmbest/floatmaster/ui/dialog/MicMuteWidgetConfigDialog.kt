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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.xmbest.floatmaster.model.ImageProperties
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MicMuteWidgetConfigDialog(
    initialConfig: MicMuteWidgetConfig,
    onDismiss: () -> Unit,
    onConfirm: (MicMuteWidgetConfig) -> Unit
) {
    var config by remember { mutableStateOf(initialConfig) }
    
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
                    text = "麦克风状态显示配置",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                

                // 图像属性编辑
                Text(
                    text = "外观样式",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                ImagePropertiesEditor(
                    properties = config.imageProperties,
                    onPropertiesChange = { imageProps ->
                        config = config.copy(imageProperties = imageProps)
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