package com.xmbest.floatmaster.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Recomposer
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.compositionContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * 用来维护所有的悬浮view窗口
 */
class FloatWindowManager(private val context: Context) {

    /**
     * 自定义生命周期所有者，用于管理ComposeView的生命周期
     */
    private class FloatLifecycleOwner : 
        androidx.lifecycle.LifecycleOwner,
        ViewModelStoreOwner,
        SavedStateRegistryOwner {
        
        private val lifecycleRegistry = LifecycleRegistry(this)
        private val store = ViewModelStore()
        private val savedStateRegistryController = SavedStateRegistryController.create(this)
        
        override val lifecycle: Lifecycle = lifecycleRegistry
        override val viewModelStore: ViewModelStore = store
        override val savedStateRegistry: SavedStateRegistry = savedStateRegistryController.savedStateRegistry
        
        init {
            savedStateRegistryController.performRestore(null)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }
        
        fun destroy() {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            store.clear()
        }
    }

    private val windowManager: WindowManager by lazy { context.getSystemService(WINDOW_SERVICE) as WindowManager }
    private val layoutFlag: Int by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
    }

    private val floatViews = mutableListOf<View>()
    private val lifecycleOwners = mutableMapOf<View, FloatLifecycleOwner>()
    private val coroutineScope = CoroutineScope(AndroidUiDispatcher.Main)

    /**
     * 添加悬浮view
     * @param view 悬浮的view
     * @param startX 起始x轴点
     * @param startY 起始y轴点
     * @param width 宽度，如果为0则使用view的宽度
     * @param height 高度，如果为0则使用view的高度
     */
    private fun addView(view: View, startX: Int = 0, startY: Int = 0, width: Int = 0, height: Int = 0) {
        val viewWidth = if (width > 0) width else if (view.width > 0) view.width else ViewGroup.LayoutParams.WRAP_CONTENT
        val viewHeight = if (height > 0) height else if (view.height > 0) view.height else ViewGroup.LayoutParams.WRAP_CONTENT
        val params = WindowManager.LayoutParams(
            viewWidth,
            viewHeight,
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
     * 添加Compose悬浮窗
     * @param content Compose内容
     * @param startX 起始x轴点
     * @param startY 起始y轴点
     * @param width 宽度
     * @param height 高度
     */
    fun addComposeView(
        content: @Composable () -> Unit,
        startX: Int = 0,
        startY: Int = 0,
        width: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
        height: Int = ViewGroup.LayoutParams.WRAP_CONTENT
    ) {
        val composeView = createComposeView(content)
        addView(composeView, startX, startY, width, height)
    }

    /**
     * 创建ComposeView并设置生命周期
     * @param content Compose内容
     * @return 配置好的ComposeView
     */
    private fun createComposeView(content: @Composable () -> Unit): ComposeView {
        val lifecycleOwner = FloatLifecycleOwner()
        val composeView = ComposeView(context)
        
        // 设置生命周期相关的ViewTree - 使用扩展函数
        composeView.setViewTreeLifecycleOwner(lifecycleOwner)
        composeView.setViewTreeViewModelStoreOwner(lifecycleOwner)
        composeView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)
        
        // 创建Recomposer并设置为compositionContext
        val recomposer = Recomposer(coroutineScope.coroutineContext)
        composeView.compositionContext = recomposer
        
        // 启动Recomposer
        coroutineScope.launch {
            recomposer.runRecomposeAndApplyChanges()
        }
        
        // 设置Compose内容
        composeView.setContent(content)
        
        // 保存生命周期所有者的引用
        lifecycleOwners[composeView] = lifecycleOwner
        
        return composeView
    }

    /**
     * 移除指定view
     */
    fun removeView(view: View) {
        runCatching {
            // 如果是ComposeView，需要清理生命周期
            lifecycleOwners[view]?.let { lifecycleOwner ->
                lifecycleOwner.destroy()
                lifecycleOwners.remove(view)
            }
            
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
        val viewsToRemove = floatViews.toList()
        viewsToRemove.forEach { view ->
            removeView(view)
        }
    }

    /**
     * 获取所有悬浮窗View
     */
    fun getAllViews(): List<View> {
        return floatViews.toList()
    }

    /**
     * 销毁管理器，清理所有资源
     */
    fun destroy() {
        removeAllView()
        lifecycleOwners.clear()
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