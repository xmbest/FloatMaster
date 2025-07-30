package com.xmbest.floatmaster.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.xmbest.floatmaster.model.ImageProperties

/**
 * 图像属性编辑器组件
 */
@Composable
fun ImagePropertiesEditor(
    properties: ImageProperties,
    onPropertiesChange: (ImageProperties) -> Unit
) {
    var x by remember { mutableFloatStateOf(properties.x) }
    var y by remember { mutableFloatStateOf(properties.y) }
    var width by remember { mutableFloatStateOf(properties.width) }
    var height by remember { mutableFloatStateOf(properties.height) }
    var color by remember { mutableStateOf(properties.color) }
    var backgroundColor by remember { mutableStateOf(properties.backgroundColor) }
    
    Column {
        // 位置设置
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = x.toString(),
                onValueChange = { 
                    val newX = it.toFloatOrNull() ?: 0f
                    x = newX
                    onPropertiesChange(properties.copy(x = newX))
                },
                label = { Text("X坐标") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            
            OutlinedTextField(
                value = y.toString(),
                onValueChange = { 
                    val newY = it.toFloatOrNull() ?: 0f
                    y = newY
                    onPropertiesChange(properties.copy(y = newY))
                },
                label = { Text("Y坐标") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 尺寸设置
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = width.toString(),
                onValueChange = { 
                    val newWidth = it.toFloatOrNull() ?: 100f
                    width = newWidth
                    onPropertiesChange(properties.copy(width = newWidth))
                },
                label = { Text("宽度") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            
            OutlinedTextField(
                value = height.toString(),
                onValueChange = { 
                    val newHeight = it.toFloatOrNull() ?: 100f
                    height = newHeight
                    onPropertiesChange(properties.copy(height = newHeight))
                },
                label = { Text("高度") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 颜色设置
        Text(
            text = "颜色设置",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 前景颜色（图标颜色）
            Column {
                Text("图标颜色", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(4.dp))
                ColorPicker(
                    color = color,
                    onColorChange = { newColor ->
                        color = newColor
                        onPropertiesChange(properties.copy(color = newColor))
                    }
                )
            }
            
            // 背景颜色
            Column {
                Text("背景颜色", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(4.dp))
                ColorPicker(
                    color = backgroundColor,
                    onColorChange = { newColor ->
                        backgroundColor = newColor
                        onPropertiesChange(properties.copy(backgroundColor = newColor))
                    }
                )
            }
        }
    }
}