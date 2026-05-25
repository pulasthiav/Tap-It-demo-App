package com.pulasthi.tapit.ui.qr

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pulasthi.tapit.ui.theme.TapItBluePrimary
import com.pulasthi.tapit.ui.theme.TapItInputBackground
import com.pulasthi.tapit.ui.theme.TapItTextPrimary
import com.pulasthi.tapit.ui.theme.TapItTextSecondary
import com.pulasthi.tapit.ui.theme.TapItWhite
import com.pulasthi.tapit.viewmodel.QRViewModel
import com.pulasthi.tapit.viewmodel.QrScannerStatus

@Composable
fun QRPayScreen(
    onBack: () -> Unit,
    onNavigateToConfirm: () -> Unit,
    viewModel: QRViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        viewModel.onPermissionResult(granted)
    }

    LaunchedEffect(Unit) {
        viewModel.refreshPermission(context.applicationContext)
    }

    LaunchedEffect(uiState.hasCameraPermission, uiState.permissionRequested) {
        if (!uiState.hasCameraPermission && !uiState.permissionRequested) {
            viewModel.requestPermission()
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigateToConfirm.collect { onNavigateToConfirm() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TapItBluePrimary),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TapItWhite,
                )
            }
            Text(
                text = "QR Pay",
                color = TapItWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 48.dp),
                textAlign = TextAlign.Center,
            )
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = TapItInputBackground,
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                ) {
                    when {
                        uiState.hasCameraPermission && uiState.canStartCamera -> {
                            QrCameraPreview(
                                enabled = true,
                                flashEnabled = uiState.flashEnabled,
                                onQrCodeScanned = viewModel::onQrCodeScanned,
                                onCameraError = viewModel::onScanError,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }

                        uiState.scannerStatus == QrScannerStatus.PermissionDenied -> {
                            PermissionDeniedContent(
                                message = uiState.errorMessage
                                    ?: "Camera permission is required to scan QR codes",
                                onRequestPermission = {
                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                },
                            )
                        }

                        else -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "Preparing camera…",
                                    color = TapItWhite,
                                    fontSize = 14.sp,
                                )
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 12.dp)
                            .fillMaxWidth(0.92f),
                        shape = RoundedCornerShape(14.dp),
                        color = TapItBluePrimary,
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                            Text(
                                text = "Scan QR Code",
                                color = TapItWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "Scan QR Code to Pay",
                                color = TapItWhite.copy(alpha = 0.9f),
                                fontSize = 13.sp,
                                modifier = Modifier.padding(top = 4.dp),
                            )
                            Text(
                                text = "Hold your camera to scan a QR code",
                                color = TapItWhite.copy(alpha = 0.85f),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 2.dp),
                            )
                        }
                    }

                    if (uiState.hasCameraPermission) {
                        IconButton(
                            onClick = viewModel::toggleFlash,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 72.dp, end = 8.dp)
                                .size(48.dp),
                        ) {
                            Surface(
                                shape = RoundedCornerShape(24.dp),
                                color = Color.Black.copy(alpha = 0.55f),
                            ) {
                                Icon(
                                    imageVector = if (uiState.flashEnabled) {
                                        Icons.Default.FlashOn
                                    } else {
                                        Icons.Default.FlashOff
                                    },
                                    contentDescription = "Toggle flash",
                                    tint = TapItWhite,
                                    modifier = Modifier.padding(10.dp),
                                )
                            }
                        }
                    }
                }

                PaymentBrandsRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                )
            }
        }
    }
}

@Composable
private fun PermissionDeniedContent(
    message: String,
    onRequestPermission: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp),
        ) {
            Text(
                text = message,
                color = TapItWhite,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRequestPermission,
                colors = ButtonDefaults.buttonColors(
                    containerColor = TapItBluePrimary,
                    contentColor = TapItWhite,
                ),
            ) {
                Text("Allow Camera")
            }
        }
    }
}

@Composable
private fun PaymentBrandsRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp),
    ) {
        PaymentBrandChip(
            modifier = Modifier.weight(1f),
            label = "VISA",
            accent = TapItBluePrimary,
        )
        PaymentBrandChip(
            modifier = Modifier.weight(1f),
            label = "MC",
            accent = Color(0xFFEB001B),
        )
        PaymentBrandChip(
            modifier = Modifier.weight(1f),
            label = "Q+",
            accent = Color(0xFFFF6F00),
        )
    }
}

@Composable
private fun PaymentBrandChip(
    modifier: Modifier,
    label: String,
    accent: Color,
) {
    Surface(
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(12.dp),
        color = TapItWhite,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, TapItTextSecondary.copy(alpha = 0.2f)),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                color = accent,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
