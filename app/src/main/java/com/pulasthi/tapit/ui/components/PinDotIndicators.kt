package com.pulasthi.tapit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.pulasthi.tapit.ui.theme.TapItPinDotEmpty
import com.pulasthi.tapit.ui.theme.TapItPinDotFilled

private const val PIN_LENGTH = 4

@Composable
fun PinDotIndicators(
    pinLength: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        repeat(PIN_LENGTH) { index ->
            val isFilled = index < pinLength
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(if (isFilled) TapItPinDotFilled else TapItPinDotEmpty),
            )
        }
    }
}
