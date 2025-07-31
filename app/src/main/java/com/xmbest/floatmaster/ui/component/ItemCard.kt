package com.xmbest.floatmaster.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xmbest.floatmaster.R
import com.xmbest.floatmaster.model.FloatWidgetItem


/**
 * Widget位置信息数据类
 */
data class WidgetPosition(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
)

/**
 * 获取Widget位置信息
 */
@Composable
private fun getWidgetPosition(
    configManager: com.xmbest.floatmaster.manager.WidgetConfigManager?,
    widgetId: String
): WidgetPosition {
    val position = configManager?.getWidgetPosition(widgetId) ?: mapOf(
        "x" to 100f,
        "y" to 200f,
        "width" to 300f,
        "height" to 120f
    )
    
    return WidgetPosition(
        x = position["x"]?.toInt() ?: 100,
        y = position["y"]?.toInt() ?: 200,
        width = position["width"]?.toInt() ?: 300,
        height = position["height"]?.toInt() ?: 120
    )
}

/**
 * 通用信息文本组件
 */
@Composable
private fun InfoText(
    text: String,
    style: TextStyle = MaterialTheme.typography.bodySmall,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Text(
        text = text,
        style = style,
        color = color
    )
}

/**
 * 显示Widget配置信息
 */
@Composable
private fun WidgetConfigInfo(
    configManager: com.xmbest.floatmaster.manager.WidgetConfigManager?,
    widgetId: String
) {
    configManager?.let { manager ->
        val config = manager.getConfig(widgetId)
        when (config) {
            is com.xmbest.floatmaster.ui.dialog.TimeWidgetConfig -> {
                InfoText(
                    text = stringResource(R.string.config_info_refresh_interval, config.refreshIntervalMs)
                )
                InfoText(
                    text = stringResource(R.string.config_info_font_size, config.textProperties.textSize.toString())
                )
            }
            is com.xmbest.floatmaster.ui.dialog.NetworkSpeedWidgetConfig -> {
                InfoText(
                    text = stringResource(R.string.config_info_refresh_interval, config.refreshIntervalMs)
                )
                val modeText = when (config.mode) {
                    com.xmbest.floatmaster.ui.widget.NetworkSpeedMode.DOWNLOAD_ONLY -> stringResource(R.string.network_mode_download_only)
                    com.xmbest.floatmaster.ui.widget.NetworkSpeedMode.UPLOAD_ONLY -> stringResource(R.string.network_mode_upload_only)
                    com.xmbest.floatmaster.ui.widget.NetworkSpeedMode.BOTH -> stringResource(R.string.network_mode_both)
                }
                InfoText(
                    text = stringResource(R.string.config_info_display_mode, modeText)
                )
            }
            is com.xmbest.floatmaster.ui.dialog.MicMuteWidgetConfig -> {
                // 麦克风Widget不显示额外的配置信息
            }
        }
    }
}

/**
 * 悬浮功能卡片组件
 * 
 * 显示悬浮功能项的详细信息，包括图标、标题、描述和控制按钮
 * 支持显示悬浮窗的实时坐标和大小信息
 * 
 * @param item 悬浮功能项数据
 * @param isActive 当前激活状态
 * @param onToggle 状态切换回调函数
 * @param onEditClick 编辑按钮点击回调
 * @param configManager Widget配置管理器
 * @param itemWidth 卡片宽度
 * @param isEnabled 权限是否已授予
 */
@Composable
fun ItemCard(
    item: FloatWidgetItem,
    isActive: Boolean = false,
    onToggle: (FloatWidgetItem, Boolean) -> Unit,
    onEditClick: () -> Unit,
    configManager: com.xmbest.floatmaster.manager.WidgetConfigManager? = null,
    itemWidth: Int = 320,
    isEnabled: Boolean = true,
    isSelected: Boolean = false,
    onSelectionChange: (FloatWidgetItem, Boolean) -> Unit = { _, _ -> }
) {
    val position = getWidgetPosition(configManager, item.id)
    
    Card(
        modifier = Modifier
            .clickable(enabled = isEnabled) { 
                if (isEnabled) {
                    onSelectionChange(item, !isSelected)
                }
            }
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CardDefaults.shape
                    )
                } else {
                    Modifier
                }
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardDefaults.cardColors().containerColor
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .alpha(if (isEnabled) 1f else 0.6f)
        ) {
            // 标题行
            ItemHeaderRow(
                item = item,
                isActive = isActive,
                isEnabled = isEnabled,
                onToggle = onToggle,
                itemWidth = itemWidth,
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 信息行
            ItemInfoRow(
                position = position,
                configManager = configManager,
                widgetId = item.id,
                onEditClick = onEditClick,
                itemWidth = itemWidth,
                isEnabled = isEnabled
            )
        }
    }
}

/**
 * 卡片标题行组件
 */
@Composable
private fun ItemHeaderRow(
    item: FloatWidgetItem,
    isActive: Boolean,
    isEnabled: Boolean,
    onToggle: (FloatWidgetItem, Boolean) -> Unit,
    itemWidth: Int,
) {
    Row(
        modifier = Modifier.width(itemWidth.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            Icon(
                item.icon,
                contentDescription = item.title,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
        CircularProgressButton(
            isActive = isActive,
            enabled = isEnabled,
            onClick = { onToggle(item, !isActive) }
        )
    }
}

/**
 * 卡片信息行组件
 */
@Composable
private fun ItemInfoRow(
    position: WidgetPosition,
    configManager: com.xmbest.floatmaster.manager.WidgetConfigManager?,
    widgetId: String,
    onEditClick: () -> Unit,
    itemWidth: Int,
    isEnabled: Boolean = true
) {
    Row(
        modifier = Modifier.width(itemWidth.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            InfoText(
                text = stringResource(R.string.coordinate_label, position.x, position.y)
            )
            InfoText(
                text = stringResource(R.string.size_label, position.width, position.height)
            )
            
            WidgetConfigInfo(
                configManager = configManager,
                widgetId = widgetId
            )
        }
        
        IconButton(
            onClick = onEditClick,
            modifier = Modifier.size(32.dp),
            enabled = isEnabled
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(R.string.edit_item),
                modifier = Modifier.size(18.dp),
                tint = if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
        }
    }
}

