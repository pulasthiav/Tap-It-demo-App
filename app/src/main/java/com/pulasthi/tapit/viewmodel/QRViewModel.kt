package com.pulasthi.tapit.viewmodel

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulasthi.tapit.data.PaymentCardRepository
import com.pulasthi.tapit.util.QrPayloadParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

data class QrPaymentDetails(
    val merchantName: String,
    val amount: Double? = null,
    val accountReference: String? = null,
    val bankName: String? = null,
) {
    val formattedAmount: String
        get() = amount?.let { "%.2f".format(it) } ?: "0.00"
}

enum class QrScannerStatus {
    Idle,
    AwaitingPermission,
    Scanning,
    Decoded,
    PermissionDenied,
    Error,
}

enum class QrPaymentStatus {
    Idle,
    Processing,
    Success,
}

data class QrPayUiState(
    val hasCameraPermission: Boolean = false,
    val permissionRequested: Boolean = false,
    val flashEnabled: Boolean = false,
    val scannerStatus: QrScannerStatus = QrScannerStatus.AwaitingPermission,
    val scanLocked: Boolean = false,
    val errorMessage: String? = null,
    val paymentDetails: QrPaymentDetails? = null,
    val amount: String = "",
    val cards: List<GovPaymentCard> = emptyList(),
    val selectedCardIndex: Int = 0,
    val amountError: String? = null,
    val paymentStatus: QrPaymentStatus = QrPaymentStatus.Idle,
) {
    val transferAmount: Double?
        get() = amount.toDoubleOrNull()

    val formattedAmount: String
        get() = transferAmount?.let { "%.2f".format(it) } ?: "0.00"

    val canStartCamera: Boolean
        get() = hasCameraPermission && !scanLocked && scannerStatus != QrScannerStatus.Decoded
}

class QRViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(QrPayUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigateToConfirm = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToConfirm = _navigateToConfirm.asSharedFlow()

    private val _navigateToSuccess = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToSuccess = _navigateToSuccess.asSharedFlow()

    private val qrDecodeInFlight = AtomicBoolean(false)

    fun refreshPermission(context: android.content.Context) {
        val appContext = context.applicationContext
        val granted = ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.CAMERA,
        ) == PackageManager.PERMISSION_GRANTED

        _uiState.update {
            it.copy(
                hasCameraPermission = granted,
                scannerStatus = when {
                    granted && !it.scanLocked -> QrScannerStatus.Scanning
                    granted -> it.scannerStatus
                    it.permissionRequested -> QrScannerStatus.PermissionDenied
                    else -> QrScannerStatus.AwaitingPermission
                },
                errorMessage = if (granted) null else it.errorMessage,
            )
        }
    }

    fun onPermissionResult(granted: Boolean) {
        _uiState.update {
            it.copy(
                hasCameraPermission = granted,
                permissionRequested = true,
                scannerStatus = if (granted) QrScannerStatus.Scanning else QrScannerStatus.PermissionDenied,
                errorMessage = if (granted) null else "Camera permission is required to scan QR codes",
            )
        }
    }

    fun requestPermission() {
        _uiState.update {
            it.copy(
                permissionRequested = true,
                scannerStatus = QrScannerStatus.AwaitingPermission,
            )
        }
    }

    fun toggleFlash() {
        _uiState.update { it.copy(flashEnabled = !it.flashEnabled) }
    }

    fun onQrCodeScanned(rawValue: String) {
        if (_uiState.value.scanLocked || !qrDecodeInFlight.compareAndSet(false, true)) {
            return
        }

        viewModelScope.launch {
            try {
                val details = withContext(Dispatchers.Default) {
                    QrPayloadParser.parse(rawValue)
                }
                val walletCards = withContext(Dispatchers.Default) {
                    PaymentCardRepository.snapshot().map { saved ->
                        GovPaymentCard(
                            id = saved.id,
                            holderName = saved.holderName,
                            maskedNumber = saved.maskedNumber,
                        )
                    }
                }

                _uiState.update {
                    it.copy(
                        scanLocked = true,
                        scannerStatus = QrScannerStatus.Decoded,
                        paymentDetails = details,
                        amount = details.amount?.let { amount -> "%.2f".format(amount) } ?: "",
                        errorMessage = null,
                        cards = walletCards,
                        selectedCardIndex = 0.coerceAtMost((walletCards.size - 1).coerceAtLeast(0)),
                    )
                }
                _navigateToConfirm.tryEmit(Unit)
            } finally {
                qrDecodeInFlight.set(false)
            }
        }
    }

    fun onScanError(message: String) {
        _uiState.update {
            it.copy(
                scannerStatus = QrScannerStatus.Error,
                errorMessage = message,
            )
        }
    }

    fun onAmountChange(value: String) {
        val filtered = value.filter { it.isDigit() || it == '.' }
        _uiState.update { it.copy(amount = filtered, amountError = null) }
    }

    fun onCardSelected(index: Int) {
        _uiState.update { it.copy(selectedCardIndex = index) }
    }

    fun refreshWalletCards() {
        viewModelScope.launch(Dispatchers.Default) {
            val walletCards = PaymentCardRepository.snapshot().map { saved ->
                GovPaymentCard(
                    id = saved.id,
                    holderName = saved.holderName,
                    maskedNumber = saved.maskedNumber,
                )
            }
            _uiState.update {
                it.copy(
                    cards = walletCards,
                    selectedCardIndex = 0.coerceAtMost((walletCards.size - 1).coerceAtLeast(0)),
                )
            }
        }
    }

    fun onConfirmPayment() {
        val state = _uiState.value
        if (state.paymentStatus == QrPaymentStatus.Processing) return

        val amount = state.transferAmount
        if (amount == null || amount <= 0) {
            _uiState.update { it.copy(amountError = "Enter a valid amount") }
            return
        }
        if (state.cards.isEmpty()) {
            _uiState.update { it.copy(amountError = "Add a card in My Wallet first") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(paymentStatus = QrPaymentStatus.Processing, amountError = null) }
            delay(1_200L)
            _uiState.update { it.copy(paymentStatus = QrPaymentStatus.Success) }
            _navigateToSuccess.tryEmit(Unit)
        }
    }

    fun resetForNewScan() {
        qrDecodeInFlight.set(false)
        _uiState.update {
            QrPayUiState(
                hasCameraPermission = it.hasCameraPermission,
                permissionRequested = it.permissionRequested,
                scannerStatus = if (it.hasCameraPermission) {
                    QrScannerStatus.Scanning
                } else {
                    QrScannerStatus.AwaitingPermission
                },
            )
        }
    }

    fun resetFlow() {
        qrDecodeInFlight.set(false)
        _uiState.value = QrPayUiState()
    }
}
