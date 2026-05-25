package com.pulasthi.tapit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AccountDeletedViewModel : ViewModel() {

    private val _navigateToLogin = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToLogin = _navigateToLogin.asSharedFlow()

    init {
        viewModelScope.launch {
            delay(DISPLAY_MS)
            _navigateToLogin.tryEmit(Unit)
        }
    }

    companion object {
        private const val DISPLAY_MS = 2_500L
    }
}
