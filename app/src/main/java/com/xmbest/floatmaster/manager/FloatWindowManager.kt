package com.xmbest.floatmaster.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import kotlin.math.abs

/**
 * 用来维护所有的悬浮view窗口
 */
class FloatWindowManager(private val context: Context) {

    private val windowManager: WindowManager by lazy { context.getSystemService(WINDOW_SERVICE) as WindowManager }
    private val layoutFlag: Int by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
    }

    private val floatViews = mutableListOf<View>()

    /**
     * 添加悬浮view
     * @param view 悬浮的view
     * @param startX 起始x轴点
     * @param startY 起始y轴点
     */
    fun addView(view: View, startX: Int = 0, startY: Int = 0) {
        val params = WindowManager.LayoutParams(
            view.width,
            view.height,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START
        // view的坐标
        params.x = startX
        params.y = startY
        addDragFunctionality(view, params)
        floatViews.add(view)
        windowManager.addView(view, params)
    }

    /**
     * 移除指定view
     */
    private fun removeView(view: View) {
        runCatching {
            floatViews.remove(view)
            windowManager.removeView(view)
        }.onFailure {
            // view可能已经被移除了
        }
    }

    /**
     * 移除所有view
     */
    fun removeAllView() {
        val iterator = floatViews.iterator()
        if (iterator.hasNext()) {
            val view = iterator.next()
            removeView(view)
        }
    }

    /**
     * 给view添加拖拽功能
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun addDragFunctionality(view: View, params: WindowManager.LayoutParams) {
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f
        var isDragging = false
        val touchSlop = 10 // 触摸阈值，超过这个距离才认为是拖拽

        view.setOnTouchListener { _, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    isDragging = false
                    false // 不拦截DOWN事件，让点击事件能够正常处理
                }

                android.view.MotionEvent.ACTION_MOVE -> {
                    val deltaX = event.rawX - initialTouchX
                    val deltaY = event.rawY - initialTouchY

                    // 只有移动距离超过阈值才开始拖拽
                    if (!isDragging && (abs(deltaX) > touchSlop || abs(deltaY) > touchSlop)) {
                        isDragging = true
                    }

                    if (isDragging) {
                        params.x = initialX + deltaX.toInt()
                        params.y = initialY + deltaY.toInt()
                        windowManager.updateViewLayout(view, params)
                        true // 拖拽时拦截事件
                    } else {
                        false // 未拖拽时不拦截
                    }
                }

                android.view.MotionEvent.ACTION_UP -> {
                    if (isDragging) {
                        isDragging = false
                        true // 拖拽结束时拦截UP事件
                    } else {
                        false // 非拖拽时不拦截，让点击事件正常处理
                    }
                }

                else -> false
            }
        }
    }

}