package com.pulasthi.tapit.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pulasthi.tapit.R
import com.pulasthi.tapit.ui.theme.TapItBluePrimary
import com.pulasthi.tapit.viewmodel.SplashViewModel

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: SplashViewModel = viewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.navigateToLogin.collect {
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TapItBluePrimary)
            .onGloballyPositioned {
                SplashScreenController.shouldKeepOnScreen = false
            },
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.image_12),
            contentDescription = "Tap-It Secure transfers",
            modifier = Modifier.fillMaxWidth(0.6f),
            contentScale = ContentScale.Fit,
        )
    }
}
