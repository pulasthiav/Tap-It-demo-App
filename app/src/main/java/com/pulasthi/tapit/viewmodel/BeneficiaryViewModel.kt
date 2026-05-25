package com.pulasthi.tapit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulasthi.tapit.data.BeneficiaryRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class BeneficiaryUiState(
    val beneficiaries: List<TransferBeneficiary> = emptyList(),
    val searchQuery: String = "",
    val pendingDelete: TransferBeneficiary? = null,
    val editingBeneficiaryId: String? = null,
    val formName: String = "",
    val formBank: String = "",
    val formAccountNumber: String = "",
    val formNameError: String? = null,
    val formBankError: String? = null,
    val formAccountError: String? = null,
) {
    val filteredBeneficiaries: List<TransferBeneficiary>
        get() {
            val query = searchQuery.trim()
            if (query.isEmpty()) return beneficiaries
            return beneficiaries.filter {
                it.name.contains(query, ignoreCase = true) ||
                    it.bankName.contains(query, ignoreCase = true) ||
                    it.accountNumber.contains(query)
            }
        }

    val isEditMode: Boolean
        get() = editingBeneficiaryId != null

    val formTitle: String
        get() = if (isEditMode) "Edit Beneficiary" else "Add Beneficiary"
}

class BeneficiaryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BeneficiaryUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigateBackAfterSave = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateBackAfterSave = _navigateBackAfterSave.asSharedFlow()

    private val _showDeleteSuccess = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val showDeleteSuccess = _showDeleteSuccess.asSharedFlow()

    init {
        viewModelScope.launch {
            BeneficiaryRepository.beneficiaries.collect { list ->
                _uiState.update { it.copy(beneficiaries = list) }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun startAddForm() {
        _uiState.update {
            it.copy(
                editingBeneficiaryId = null,
                formName = "",
                formBank = "",
                formAccountNumber = "",
                formNameError = null,
                formBankError = null,
                formAccountError = null,
            )
        }
    }

    fun startEditForm(beneficiaryId: String) {
        val beneficiary = BeneficiaryRepository.findById(beneficiaryId) ?: return
        _uiState.update {
            it.copy(
                editingBeneficiaryId = beneficiary.id,
                formName = beneficiary.name,
                formBank = beneficiary.bankName,
                formAccountNumber = beneficiary.accountNumber,
                formNameError = null,
                formBankError = null,
                formAccountError = null,
            )
        }
    }

    fun onFormNameChange(value: String) {
        _uiState.update { it.copy(formName = value, formNameError = null) }
    }

    fun onFormBankChange(value: String) {
        _uiState.update { it.copy(formBank = value, formBankError = null) }
    }

    fun onFormAccountChange(value: String) {
        val digits = value.filter { it.isDigit() }
        _uiState.update { it.copy(formAccountNumber = digits, formAccountError = null) }
    }

    fun addBeneficiary() {
        if (!validateForm()) return
        val state = _uiState.value
        BeneficiaryRepository.addBeneficiary(
            TransferBeneficiary(
                id = UUID.randomUUID().toString(),
                name = state.formName.trim(),
                bankName = state.formBank.trim(),
                accountNumber = state.formAccountNumber.trim(),
            ),
        )
        _navigateBackAfterSave.tryEmit(Unit)
    }

    fun editBeneficiary() {
        if (!validateForm()) return
        val state = _uiState.value
        val id = state.editingBeneficiaryId ?: return
        BeneficiaryRepository.updateBeneficiary(
            TransferBeneficiary(
                id = id,
                name = state.formName.trim(),
                bankName = state.formBank.trim(),
                accountNumber = state.formAccountNumber.trim(),
                mobileNumber = BeneficiaryRepository.findById(id)?.mobileNumber.orEmpty(),
            ),
        )
        _navigateBackAfterSave.tryEmit(Unit)
    }

    fun onSaveForm() {
        if (_uiState.value.isEditMode) {
            editBeneficiary()
        } else {
            addBeneficiary()
        }
    }

    fun requestDelete(beneficiary: TransferBeneficiary) {
        _uiState.update { it.copy(pendingDelete = beneficiary) }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(pendingDelete = null) }
    }

    fun confirmDelete() {
        val target = _uiState.value.pendingDelete ?: return
        BeneficiaryRepository.deleteBeneficiary(target.id)
        _uiState.update { it.copy(pendingDelete = null) }
        _showDeleteSuccess.tryEmit(Unit)
    }

    fun deleteBeneficiary(id: String) {
        BeneficiaryRepository.deleteBeneficiary(id)
    }

    private fun validateForm(): Boolean {
        val state = _uiState.value
        val nameError = if (state.formName.isBlank()) "Enter beneficiary name" else null
        val bankError = if (state.formBank.isBlank()) "Enter bank name" else null
        val accountError = when {
            state.formAccountNumber.isBlank() -> "Enter account number"
            state.formAccountNumber.length < 5 -> "Enter a valid account number"
            else -> null
        }

        if (nameError != null || bankError != null || accountError != null) {
            _uiState.update {
                it.copy(
                    formNameError = nameError,
                    formBankError = bankError,
                    formAccountError = accountError,
                )
            }
            return false
        }
        return true
    }
}
