package com.pulasthi.tapit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val _navigateToLogin = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToLogin = _navigateToLogin.asSharedFlow()

    init {
        viewModelScope.launch {
            delay(SPLASH_DURATION_MS)
            _navigateToLogin.tryEmit(Unit)
        }
    }

    companion object {
        private const val SPLASH_DURATION_MS = 2_000L
    }
}
