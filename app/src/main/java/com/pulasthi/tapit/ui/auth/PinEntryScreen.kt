package com.pulasthi.tapit.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulasthi.tapit.ui.components.NumericKeypad
import com.pulasthi.tapit.ui.components.PinDotIndicators
import com.pulasthi.tapit.ui.components.TapItGradientBackground
import com.pulasthi.tapit.ui.theme.TapItError
import com.pulasthi.tapit.ui.theme.TapItWhite

@Composable
fun PinEntryScreen(
    title: String,
    pinLength: Int,
    onDigitClick: (Int) -> Unit,
    onBackspaceClick: () -> Unit,
    errorMessage: String? = null,
) {
    TapItGradientBackground {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(120.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = title,
                    color = TapItWhite,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(32.dp))

                PinDotIndicators(pinLength = pinLength)

                errorMessage?.let { message ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = message,
                        color = TapItError,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            NumericKeypad(
                onDigitClick = onDigitClick,
                onBackspaceClick = onBackspaceClick,
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
