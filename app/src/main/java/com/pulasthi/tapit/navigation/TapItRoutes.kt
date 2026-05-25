package com.pulasthi.tapit.navigation

object TapItRoutes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val CREATE_ACCOUNT = "create_account"
    const val CREATE_PIN = "create_pin"
    const val REENTER_PIN = "reenter_pin/{expectedPin}"
    const val PIN_CREATED_SUCCESS = "pin_created_success"
    const val ENTER_PIN = "enter_pin"

    /** Main app shell with bottom navigation (post-auth). */
    const val MAIN = "main"
    const val DASHBOARD = MAIN

    const val SETTINGS = "settings"
    const val GOV_PAY = GovPayRoutes.GRAPH
    const val WALLET = WalletRoutes.GRAPH
    const val PAY_BILLS = PayBillsRoutes.GRAPH
    const val RELOAD = ReloadRoutes.GRAPH
    const val SEND_MONEY = SendMoneyRoutes.GRAPH
    const val QR_PAY = QrPayRoutes.GRAPH
    const val SCHEDULE = ScheduleRoutes.GRAPH

    const val ARG_EXPECTED_PIN = "expectedPin"

    fun reenterPin(expectedPin: String): String = "reenter_pin/$expectedPin"
}

object MainRoutes {
    const val HOME = "home"
    const val MY_QR = "my_qr"
    const val MY_WALLET = "my_wallet"
    const val HISTORY = "history"
}
