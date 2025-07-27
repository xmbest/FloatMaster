package com.xmbest.floatmaster.ui.component

import android.net.TrafficStats
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.xmbest.floatmaster.model.TextProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

/**
 * 网速显示模式
 */
enum class NetworkSpeedMode {
    DOWNLOAD_ONLY,  // 仅显示下载速度
    UPLOAD_ONLY,    // 仅显示上传速度
    BOTH           // 同时显示上下行速度
}

/**
 * 网速控件
 * @param textProperties 文本属性
 * @param refreshIntervalMs 刷新间隔（毫秒）
 * @param mode 网速显示模式
 */
@Composable
fun NetworkSpeedWidget(
    refreshIntervalMs: Long = 1000L,
    mode: NetworkSpeedMode = NetworkSpeedMode.BOTH,
    textProperties: TextProperties = TextProperties()
) {
    var lastRxBytes by remember { mutableLongStateOf(0L) }
    var lastTxBytes by remember { mutableLongStateOf(0L) }
    var lastUpdateTime by remember { mutableLongStateOf(0L) }
    var text by remember { mutableStateOf(getInitialText(mode)) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            // 初始化数据
            lastRxBytes = TrafficStats.getTotalRxBytes()
            lastTxBytes = TrafficStats.getTotalTxBytes()
            lastUpdateTime = System.currentTimeMillis()

            while (isActive) {
                delay(refreshIntervalMs)

                val currentTime = System.currentTimeMillis()
                val timeDiff = currentTime - lastUpdateTime

                // 避免除零错误
                if (timeDiff > 0) {
                    val currentRxBytes = TrafficStats.getTotalRxBytes()
                    val currentTxBytes = TrafficStats.getTotalTxBytes()

                    val downloadSpeed = calculateSpeed(currentRxBytes, lastRxBytes, timeDiff)
                    val uploadSpeed = calculateSpeed(currentTxBytes, lastTxBytes, timeDiff)

                    // 更新状态
                    lastRxBytes = currentRxBytes
                    lastTxBytes = currentTxBytes
                    lastUpdateTime = currentTime

                    text = formatDisplayText(mode, downloadSpeed, uploadSpeed)
                }
            }
        }
    }

    TextWidget(text = text, properties = textProperties)
}

/**
 * 获取初始显示文本
 */
private fun getInitialText(mode: NetworkSpeedMode): String {
    return when (mode) {
        NetworkSpeedMode.DOWNLOAD_ONLY -> "↓ 0 B/s"
        NetworkSpeedMode.UPLOAD_ONLY -> "↑ 0 B/s"
        NetworkSpeedMode.BOTH -> "↓ 0 B/s ↑ 0 B/s"
    }
}

/**
 * 计算网速（字节/秒）
 * @param currentBytes 当前字节数
 * @param lastBytes 上次字节数
 * @param timeDiffMs 时间差（毫秒）
 * @return 速度（字节/秒）
 */
private fun calculateSpeed(currentBytes: Long, lastBytes: Long, timeDiffMs: Long): Long {
    return if (timeDiffMs > 0) {
        (currentBytes - lastBytes) * 1000 / timeDiffMs
    } else {
        0L
    }
}

/**
 * 格式化显示文本
 * @param mode 显示模式
 * @param downloadSpeed 下载速度
 * @param uploadSpeed 上传速度
 * @return 格式化后的显示文本
 */
private fun formatDisplayText(
    mode: NetworkSpeedMode,
    downloadSpeed: Long,
    uploadSpeed: Long
): String {
    return when (mode) {
        NetworkSpeedMode.DOWNLOAD_ONLY -> "↓ ${formatSpeed(downloadSpeed)}"
        NetworkSpeedMode.UPLOAD_ONLY -> "↑ ${formatSpeed(uploadSpeed)}"
        NetworkSpeedMode.BOTH -> "↓ ${formatSpeed(downloadSpeed)} ↑ ${formatSpeed(uploadSpeed)}"
    }
}

/**
 * 格式化网速显示
 * @param bytesPerSecond 每秒字节数
 * @return 格式化后的速度字符串
 */
private fun formatSpeed(bytesPerSecond: Long): String {
    return when {
        bytesPerSecond < 0 -> "0 B/s" // 处理负值情况
        bytesPerSecond < 1024 -> "$bytesPerSecond B/s"
        bytesPerSecond < 1024 * 1024 -> "%.1f KB/s".format(bytesPerSecond / 1024.0)
        bytesPerSecond < 1024 * 1024 * 1024 -> "%.1f MB/s".format(bytesPerSecond / (1024.0 * 1024.0))
        else -> "%.1f GB/s".format(bytesPerSecond / (1024.0 * 1024.0 * 1024.0))
    }
}