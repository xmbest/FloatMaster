package com.xmbest.floatmaster.model

import android.view.ViewGroup
import androidx.compose.ui.unit.isSpecified

/**
 * 悬浮窗位置和尺寸配置
 */
data class WindowPosition(
    val x: Float = 0f,
    val y: Float = 0f,
    val width: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    val height: Int = ViewGroup.LayoutParams.WRAP_CONTENT
) {
    companion object {
        /**
         * 从ImageProperties创建WindowPosition
         */
        fun fromImageProperties(imageProperties: ImageProperties): WindowPosition {
            return WindowPosition(
                x = imageProperties.x,
                y = imageProperties.y,
                width = imageProperties.width.toInt(),
                height = imageProperties.height.toInt()
            )
        }
        
        /**
         * 从TextProperties创建WindowPosition
         */
        fun fromTextProperties(textProperties: TextProperties): WindowPosition {
            val width = if (textProperties.width.isSpecified) {
                textProperties.width.value.toInt()
            } else {
                ViewGroup.LayoutParams.WRAP_CONTENT
            }
            val height = if (textProperties.height.isSpecified) {
                textProperties.height.value.toInt()
            } else {
                ViewGroup.LayoutParams.WRAP_CONTENT
            }
            return WindowPosition(
                x = textProperties.x,
                y = textProperties.y,
                width = width,
                height = height
            )
        }
    }
}