package com.xmbest.floatmaster.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xmbest.floatmaster.R
import kotlinx.coroutines.launch

/**
 * 圆形进度按钮组件
 *
 * 结合进度圆环和中心图标的可点击按钮组件
 * 支持激活/未激活状态切换，显示对应的播放/暂停图标
 * 可选择性显示进度动画效果
 *
 * @param isActive 当前激活状态，影响图标和进度环颜色
 * @param enabled 是否启用点击功能
 * @param progress 可选的进度值，范围 0.0 到 1.0
 * @param onClick 点击事件回调函数
 */
@Composable
fun CircularProgressButton(
    isActive: Boolean,
    enabled: Boolean,
    progress: Float = 0f,
    onClick: () -> Unit
) {
    val buttonSize = 48.dp
    val iconSize = 24.dp

    // 动画状态管理
    var isAnimating by remember { mutableStateOf(false) }
    var animationStartState by remember { mutableStateOf(isActive) }
    val animatedProgress = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .size(buttonSize)
            .clickable(
                enabled = enabled && !isAnimating,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                // 触发点击动画
                if (!isAnimating) {
                    scope.launch {
                        // 立即锁定状态
                        isAnimating = true
                        animationStartState = isActive

                        // 重置并执行动画
                        animatedProgress.snapTo(0f)
                        animatedProgress.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(durationMillis = 400, easing = LinearEasing)
                        )

                        // 动画完成后重置并解锁
                        animatedProgress.snapTo(0f)
                        isAnimating = false

                        // 执行回调
                        onClick()
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // 进度圆环
        ProgressRing(
            progress = if (isAnimating) animatedProgress.value else progress,
            isActive = if (isAnimating) animationStartState else isActive,
            size = buttonSize,
            enableAnimation = !isAnimating  // 动画期间禁用ProgressRing内部动画
        )

        // 中心图标 - 动画期间也使用固定状态
        val displayState = if (isAnimating) animationStartState else isActive
        Icon(
            imageVector = if (displayState) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = stringResource(
                if (displayState) R.string.pause_description else R.string.start_description
            ),
            modifier = Modifier.size(iconSize)
        )
    }
}