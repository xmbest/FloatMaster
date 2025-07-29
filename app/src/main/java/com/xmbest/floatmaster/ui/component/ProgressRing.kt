package com.xmbest.floatmaster.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 进度圆环组件
 * 
 * 绘制带动画效果的圆形进度环，支持不同状态的颜色显示
 * 未激活状态显示绿色，激活状态显示红色
 * 
 * @param progress 进度值，范围 0.0 到 1.0
 * @param isActive 是否为激活状态，影响进度环颜色
 * @param size 圆环的整体大小
 * @param strokeWidth 圆环线条的宽度
 * @param enableAnimation 是否启用内部动画，默认为true
 */
@Composable
fun ProgressRing(
    progress: Float,
    isActive: Boolean,
    size: Dp = 48.dp,
    strokeWidth: Dp = 4.dp,
    enableAnimation: Boolean = true
) {
    // 动画进度值
    val animatedProgress by if (enableAnimation) {
        animateFloatAsState(
            targetValue = progress,
            animationSpec = tween(durationMillis = 300, easing = LinearEasing),
            label = "progress"
        )
    } else {
        // 禁用动画时直接使用传入的progress值
        remember(progress) { mutableStateOf(progress) }
    }
    
    // 颜色常量定义
    val greenColor = Color(0xFF4CAF50)  // 未激活状态颜色
    val redColor = Color(0xFFF44336)    // 激活状态颜色
    val grayColor = Color.Gray.copy(alpha = 0.3f)  // 背景圆环颜色
    
    Canvas(
        modifier = Modifier.size(size)
    ) {
        val strokeWidthPx = strokeWidth.toPx()
        val radius = (size.toPx() - strokeWidthPx) / 2
        
        // 绘制背景圆环
        drawCircle(
            color = grayColor,
            radius = radius,
            style = Stroke(width = strokeWidthPx)
        )
        
        // 绘制进度圆环
         if (animatedProgress > 0f) {
             val progressColor = if (isActive) redColor else greenColor
             drawArc(
                 color = progressColor,
                 startAngle = -90f,  // 从顶部开始
                 sweepAngle = 360f * animatedProgress,
                 useCenter = false,
                 style = Stroke(
                     width = strokeWidthPx,
                     cap = StrokeCap.Round
                 )
             )
         }
    }
}