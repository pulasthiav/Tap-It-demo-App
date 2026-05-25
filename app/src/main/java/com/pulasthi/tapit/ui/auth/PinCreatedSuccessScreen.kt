package com.pulasthi.tapit.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pulasthi.tapit.ui.components.TapItGradientBackground
import com.pulasthi.tapit.ui.theme.TapItGreen
import com.pulasthi.tapit.ui.theme.TapItWhite
import com.pulasthi.tapit.viewmodel.PinCreatedSuccessViewModel

@Composable
fun PinCreatedSuccessScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: PinCreatedSuccessViewModel = viewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.navigateToLogin.collect {
            onNavigateToLogin()
        }
    }

    TapItGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Surface(
                shape = CircleShape,
                color = TapItGreen,
                modifier = Modifier.size(80.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = TapItWhite,
                    modifier = Modifier
                        .padding(20.dp)
                        .size(40.dp),
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "PIN Code Created Successfully",
                color = TapItWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
    }
}
