package com.pulasthi.tapit.ui.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pulasthi.tapit.ui.components.TapItGradientBackground
import com.pulasthi.tapit.ui.components.TapItLogo
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

    TapItGradientBackground {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            TapItLogo()
        }
    }
}
