package com.xmbest.floatmaster.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 文本属性数据类
 * @param backgroundColor 背景色
 * @param textColor 文本颜色
 * @param textSize 文本大小
 * @param width 宽度
 * @param height 高度
 * @param fontWeight 文本粗细
 * @param x 当前X坐标
 * @param y 当前Y坐标
 */
data class TextProperties(
    val backgroundColor: Color = Color.Transparent,
    val textColor: Color = Color.Black,
    val textSize: TextUnit = 16.sp,
    val width: Dp = 300.dp,
    val height: Dp = 120.dp,
    val fontWeight: FontWeight = FontWeight.Normal,
    val x: Float = 100f,
    val y: Float = 100f
)