package com.xmbest.floatmaster.model

import androidx.compose.ui.graphics.Color

/**
 * 图片属性数据类
 * @param backgroundColor 背景色
 * @param color 前景颜色
 * @param width 宽度
 * @param height 高度
 * @param x 当前X坐标
 * @param y 当前Y坐标
 */
data class ImageProperties(
    val backgroundColor: Color = Color.Transparent, // 蓝色背景，更容易看到
    val color: Color = Color.Black, // 白色图标
    val width: Float = 300f,
    val height: Float = 300f,
    val x: Float = 100f,
    val y: Float = 100f
)