package com.xmbest.floatmaster.ui.component

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xmbest.floatmaster.R
import com.xmbest.floatmaster.model.FloatWidgetItem


/**
 * 悬浮功能卡片组件
 * 
 * 显示悬浮功能项的详细信息，包括图标、标题、描述和控制按钮
 * 支持显示悬浮窗的实时坐标和大小信息
 * 
 * @param item 悬浮功能项数据
 * @param isActive 当前激活状态
 * @param onToggle 状态切换回调函数
 * @param x 悬浮窗当前X坐标
 * @param y 悬浮窗当前Y坐标
 * @param width 悬浮窗当前宽度
 * @param height 悬浮窗当前高度
 */
@Composable
fun ItemCard(
    item: FloatWidgetItem,
    isActive: Boolean = false,
    onToggle: (FloatWidgetItem, Boolean) -> Unit,
    onEditClick: () -> Unit,
    x: Int = 100,
    y: Int = 200,
    width: Int = 120,
    height: Int = 80,
    itemWidth: Int = 320
) {
    Card(
        modifier = Modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        val enable = item.permissionChecker()
        Column(
            modifier = Modifier
                .padding(16.dp)
                .alpha(if (enable) 1f else 0.6f)
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
                            color =
                                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
                CircularProgressButton(
                    isActive = isActive,
                    enabled = enable,
                    onClick = { onToggle(item, !isActive) }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            // 悬浮窗信息
            Row(
                modifier = Modifier.width(itemWidth.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.coordinate_label, x, y),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.size_label, width, height),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                // 编辑按钮
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit_item),
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

