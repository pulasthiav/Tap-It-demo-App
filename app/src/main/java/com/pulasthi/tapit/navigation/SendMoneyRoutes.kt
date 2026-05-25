package com.pulasthi.tapit.navigation

object SendMoneyRoutes {
    const val GRAPH = "send_money"
    const val BENEFICIARY_LIST = "send_money_beneficiaries"
    const val ADD_BENEFICIARY = "send_money_beneficiary_add"
    const val EDIT_BENEFICIARY = "send_money_beneficiary_edit/{beneficiaryId}"
    const val RECIPIENT = "send_money_recipient"
    const val AMOUNT = "send_money_amount"
    const val CONFIRM = "send_money_confirm"
    const val SUCCESS = "send_money_success"

    const val ARG_BENEFICIARY_ID = "beneficiaryId"

    fun editBeneficiary(beneficiaryId: String): String =
        "send_money_beneficiary_edit/$beneficiaryId"
}
