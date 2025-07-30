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
import com.xmbest.floatmaster.model.TextProperties
import com.xmbest.floatmaster.ui.component.ImagePropertiesEditor
import com.xmbest.floatmaster.ui.component.TextPropertiesEditor

/**
 * 组件配置对话框
 * @param widgetId 组件ID
 * @param widgetType 组件类型 ("text" 或 "image")
 * @param initialTextProperties 初始文本属性
 * @param initialImageProperties 初始图片属性
 * @param onDismiss 关闭对话框回调
 * @param onConfirm 确认配置回调
 */
@Composable
fun WidgetConfigDialog(
    widgetId: String,
    widgetType: String, // "text" 或 "image"
    initialTextProperties: TextProperties? = null,
    initialImageProperties: ImageProperties? = null,
    onDismiss: () -> Unit,
    onConfirm: (TextProperties?, ImageProperties?) -> Unit
) {
    var textProperties by remember { mutableStateOf(initialTextProperties ?: TextProperties()) }
    var imageProperties by remember { mutableStateOf(initialImageProperties ?: ImageProperties()) }
    
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
                    text = "配置 $widgetId",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                when (widgetType) {
                    "text" -> {
                        TextPropertiesEditor(
                            properties = textProperties,
                            onPropertiesChange = { textProperties = it }
                        )
                    }
                    "image" -> {
                        ImagePropertiesEditor(
                            properties = imageProperties,
                            onPropertiesChange = { imageProperties = it }
                        )
                    }
                }
                
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
                        onClick = {
                            when (widgetType) {
                                "text" -> onConfirm(textProperties, null)
                                "image" -> onConfirm(null, imageProperties)
                            }
                        }
                    ) {
                        Text("确认")
                    }
                }
            }
        }
    }
}