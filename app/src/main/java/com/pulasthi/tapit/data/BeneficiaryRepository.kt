package com.pulasthi.tapit.data

import com.pulasthi.tapit.viewmodel.TransferBeneficiary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * In-memory store for transfer beneficiaries shared between beneficiary management and Send Money.
 */
object BeneficiaryRepository {

    private val defaultBeneficiaries = listOf(
        TransferBeneficiary(
            id = "1",
            name = "Pulasthi Avinash",
            bankName = "BOC",
            accountNumber = "93175567",
            mobileNumber = "0771234567",
        ),
        TransferBeneficiary(
            id = "2",
            name = "Ashan chamindu",
            bankName = "BOC",
            accountNumber = "93173673",
            mobileNumber = "0712345678",
        ),
        TransferBeneficiary(
            id = "3",
            name = "R.M.K.T.Rathnayaka",
            bankName = "Commercial Bank",
            accountNumber = "88214590",
            mobileNumber = "0789876543",
        ),
        TransferBeneficiary(
            id = "4",
            name = "W.M.P.A.Bandara",
            bankName = "People's Bank",
            accountNumber = "15361154",
            mobileNumber = "0761122334",
        ),
    )

    private val _beneficiaries = MutableStateFlow(defaultBeneficiaries)
    val beneficiaries: StateFlow<List<TransferBeneficiary>> = _beneficiaries.asStateFlow()

    fun snapshot(): List<TransferBeneficiary> = _beneficiaries.value

    fun findById(id: String): TransferBeneficiary? = _beneficiaries.value.find { it.id == id }

    fun addBeneficiary(beneficiary: TransferBeneficiary) {
        _beneficiaries.update { it + beneficiary }
    }

    fun updateBeneficiary(beneficiary: TransferBeneficiary) {
        _beneficiaries.update { list ->
            list.map { if (it.id == beneficiary.id) beneficiary else it }
        }
    }

    fun deleteBeneficiary(id: String) {
        _beneficiaries.update { list -> list.filter { it.id != id } }
    }
}
