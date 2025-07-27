package com.xmbest.floatmaster.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.xmbest.floatmaster.model.TextProperties
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * 展示当前时间
 * @param refreshIntervalMs 刷新时间
 * @param formatStr 当前展示时间格式
 * @param serverUrl 时间服务器地址，默认为本机时间
 * @param textProperties 文本属性
 */
@Composable
fun TimeWidget(
    refreshIntervalMs: Long = 1000L,
    formatStr: String? = null,
    serverUrl: String? = null,
    timeZone: String? = null,
    textProperties: TextProperties = TextProperties()
) {
    val formatter = SimpleDateFormat(formatStr ?: "HH:mm:ss", Locale.getDefault())
    var text by remember { mutableStateOf("") }
    LaunchedEffect(UInt) {
        while (isActive) {
            text = when {
                !timeZone.isNullOrEmpty() -> {
                    formatter.timeZone = TimeZone.getTimeZone(timeZone)
                    formatter.format(Date())
                }

                !serverUrl.isNullOrEmpty() -> {
                    // TODO: 从服务器获取时间
                    formatter.format(Date())
                }

                else -> {
                    formatter.format(Date())
                }
            }
            delay(refreshIntervalMs)
        }
    }
    TextWidget(text, textProperties)
}