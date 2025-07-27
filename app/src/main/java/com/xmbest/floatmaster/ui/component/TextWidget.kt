package com.xmbest.floatmaster.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xmbest.floatmaster.model.TextProperties

@Composable
fun TextWidget(
    text: String,
    properties: TextProperties = TextProperties()
) {
    Text(
        text = text,
        color = properties.textColor,
        fontSize = properties.textSize,
        fontWeight = properties.fontWeight,
        modifier = Modifier
            .size(width = properties.width, height = properties.height)
            .background(properties.backgroundColor)
    )
}