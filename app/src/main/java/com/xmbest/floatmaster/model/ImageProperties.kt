package com.xmbest.floatmaster.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/**
 * 文本属性数据类
 * @param backgroundColor 背景色
 * @param color 前景颜色
 * @param width 宽度
 * @param height 高度
 * @param x 当前X坐标
 * @param y 当前Y坐标
 */
data class ImageProperties(
    val backgroundColor: Color = Color.Transparent,
    val color: Color = Color.Black,
    val width: Float = 160f,
    val height: Float = 160f,
    val x: Float = 0f,
    val y: Float = 0f
)