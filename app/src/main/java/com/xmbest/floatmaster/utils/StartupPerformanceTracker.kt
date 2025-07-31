package com.xmbest.floatmaster.utils

import android.util.Log

/**
 * 启动性能追踪器
 * 用于测量应用启动时间和各个阶段的耗时
 */
object StartupPerformanceTracker {
    private const val TAG = "StartupPerformance"
    private val timeMarks = mutableMapOf<String, Long>()
    private var appStartTime = 0L
    
    /**
     * 标记应用启动开始
     */
    fun markAppStart() {
        appStartTime = System.currentTimeMillis()
        timeMarks["app_start"] = appStartTime
        Log.d(TAG, "App startup started")
    }
    
    /**
     * 标记一个时间点
     */
    fun mark(label: String) {
        val currentTime = System.currentTimeMillis()
        timeMarks[label] = currentTime
        val elapsed = if (appStartTime > 0) currentTime - appStartTime else 0
        Log.d(TAG, "Mark: $label at ${elapsed}ms")
    }
    
    /**
     * 计算两个标记之间的时间差
     */
    fun getElapsedTime(startLabel: String, endLabel: String): Long {
        val startTime = timeMarks[startLabel] ?: return -1
        val endTime = timeMarks[endLabel] ?: return -1
        return endTime - startTime
    }
    
    /**
     * 获取从应用启动到指定标记的总时间
     */
    fun getTotalElapsedTime(label: String): Long {
        val markTime = timeMarks[label] ?: return -1
        return if (appStartTime > 0) markTime - appStartTime else -1
    }
    
    /**
     * 打印性能报告
     */
    fun printReport() {
        Log.d(TAG, "=== Startup Performance Report ===")
        timeMarks.forEach { (label, time) ->
            val elapsed = time - appStartTime
            Log.d(TAG, "$label: ${elapsed}ms")
        }
        Log.d(TAG, "=================================")
    }
    
    /**
     * 清除所有标记
     */
    fun clear() {
        timeMarks.clear()
        appStartTime = 0L
    }
}