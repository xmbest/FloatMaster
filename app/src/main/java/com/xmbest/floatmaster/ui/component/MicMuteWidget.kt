package com.xmbest.floatmaster.ui.component

import android.content.Context
import android.media.AudioManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xmbest.floatmaster.R
import com.xmbest.floatmaster.model.ImageProperties

/**
 * mic禁用、启用
 */
@Composable
fun MicMuteWidget(imageProperties: ImageProperties = ImageProperties()) {
    val context = LocalContext.current
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    /**
     * 是否禁用mic
     */
    var disable by remember { mutableStateOf(audioManager.isMicrophoneMute) }
    Row(
        modifier = Modifier
            .size(imageProperties.width.dp, imageProperties.height.dp)
            .background(imageProperties.backgroundColor)
            .clickable {
                audioManager.isMicrophoneMute = !disable
                disable = audioManager.isMicrophoneMute
            }
    ) {
        Icon(
            if (disable) Icons.Default.MicOff else Icons.Default.Mic,
            tint = imageProperties.color,
            contentDescription = stringResource(R.string.mic_control),
            modifier = Modifier
                .fillMaxSize()
        )
    }
}