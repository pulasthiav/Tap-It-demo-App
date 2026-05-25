package com.pulasthi.tapit.ui.reload.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulasthi.tapit.ui.theme.TapItWhite
import com.pulasthi.tapit.viewmodel.ReloadOperator

@Composable
fun ReloadOperatorLogo(
    operator: ReloadOperator,
    modifier: Modifier = Modifier,
    size: Dp = 72.dp,
) {
    Surface(
        modifier = modifier.size(size),
        shape = CircleShape,
        color = operator.brand.primaryColor,
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = operator.brand.logoText,
                color = TapItWhite,
                fontSize = if (operator.brand.logoText.length > 5) 11.sp else 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
        }
    }
}
