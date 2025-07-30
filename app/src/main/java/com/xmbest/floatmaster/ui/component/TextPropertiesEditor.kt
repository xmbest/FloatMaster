package com.xmbest.floatmaster.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xmbest.floatmaster.model.TextProperties

/**
 * 文本属性编辑器组件
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextPropertiesEditor(
    properties: TextProperties,
    onPropertiesChange: (TextProperties) -> Unit
) {
    var x by remember { mutableFloatStateOf(properties.x) }
    var y by remember { mutableFloatStateOf(properties.y) }
    var width by remember { mutableFloatStateOf(properties.width.value) }
    var height by remember { mutableFloatStateOf(properties.height.value) }
    var textSize by remember { mutableFloatStateOf(properties.textSize.value) }
    var textColor by remember { mutableStateOf(properties.textColor) }
    var backgroundColor by remember { mutableStateOf(properties.backgroundColor) }
    var fontWeight by remember { mutableStateOf(properties.fontWeight) }
    
    val fontWeights = listOf(
        FontWeight.Normal to "正常",
        FontWeight.Bold to "粗体",
        FontWeight.Light to "细体",
        FontWeight.Medium to "中等",
        FontWeight.SemiBold to "半粗体"
    )
    
    var fontWeightExpanded by remember { mutableStateOf(false) }
    
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
                    val newWidth = it.toFloatOrNull() ?: 200f
                    width = newWidth
                    onPropertiesChange(properties.copy(width = newWidth.dp))
                },
                label = { Text("宽度") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            
            OutlinedTextField(
                value = height.toString(),
                onValueChange = { 
                    val newHeight = it.toFloatOrNull() ?: 50f
                    height = newHeight
                    onPropertiesChange(properties.copy(height = newHeight.dp))
                },
                label = { Text("高度") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 文本大小
        OutlinedTextField(
            value = textSize.toString(),
            onValueChange = { 
                val newSize = it.toFloatOrNull() ?: 16f
                textSize = newSize
                onPropertiesChange(properties.copy(textSize = newSize.sp))
            },
            label = { Text("文本大小") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 字体粗细
        ExposedDropdownMenuBox(
            expanded = fontWeightExpanded,
            onExpandedChange = { fontWeightExpanded = !fontWeightExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = fontWeights.find { it.first == fontWeight }?.second ?: "正常",
                onValueChange = { },
                readOnly = true,
                label = { Text("字体粗细") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = fontWeightExpanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            
            ExposedDropdownMenu(
                expanded = fontWeightExpanded,
                onDismissRequest = { fontWeightExpanded = false }
            ) {
                fontWeights.forEach { (weight, name) ->
                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            fontWeight = weight
                            onPropertiesChange(properties.copy(fontWeight = weight))
                            fontWeightExpanded = false
                        }
                    )
                }
            }
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
            // 文本颜色
            Column {
                Text("文本颜色", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(4.dp))
                ColorPicker(
                    color = textColor,
                    onColorChange = { color ->
                        textColor = color
                        onPropertiesChange(properties.copy(textColor = color))
                    }
                )
            }
            
            // 背景颜色
            Column {
                Text("背景颜色", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(4.dp))
                ColorPicker(
                    color = backgroundColor,
                    onColorChange = { color ->
                        backgroundColor = color
                        onPropertiesChange(properties.copy(backgroundColor = color))
                    }
                )
            }
        }
    }
}

/**
 * 简单的颜色选择器
 */
@Composable
fun ColorPicker(
    color: Color,
    onColorChange: (Color) -> Unit
) {
    val presetColors = listOf(
        Color.Transparent, Color.Black, Color.White, Color.Red, Color.Green, Color.Blue,
        Color.Yellow, Color.Cyan, Color.Magenta, Color.Gray,
        Color(0xFF2196F3), Color(0xFF4CAF50), Color(0xFFFF9800)
    )
    
    var showCustomColor by remember { mutableStateOf(false) }
    
    Column {
        // 当前颜色显示
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color)
                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                .clickable { showCustomColor = !showCustomColor }
        )
        
        if (showCustomColor) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // 预设颜色
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                presetColors.take(7).forEach { presetColor ->
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(presetColor)
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                            .clickable { 
                                onColorChange(presetColor)
                                showCustomColor = false
                            }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                presetColors.drop(7).forEach { presetColor ->
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(presetColor)
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                            .clickable { 
                                onColorChange(presetColor)
                                showCustomColor = false
                            }
                    )
                }
            }
        }
    }
}