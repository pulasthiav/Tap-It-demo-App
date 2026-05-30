package com.pulasthi.tapit.ui.splash

/**
 * Keeps the OS splash overlay visible until the Compose splash has drawn,
 * so the handoff from native splash to Compose is a seamless solid-blue transition.
 */
object SplashScreenController {
    @Volatile
    var shouldKeepOnScreen: Boolean = true
}
