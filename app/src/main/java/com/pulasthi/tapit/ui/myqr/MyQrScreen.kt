package com.pulasthi.tapit.ui.myqr

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pulasthi.tapit.ui.components.FlowScreenScaffold
import com.pulasthi.tapit.ui.theme.TapItBluePrimary
import com.pulasthi.tapit.ui.theme.TapItDisabledButton
import com.pulasthi.tapit.ui.theme.TapItTextPrimary
import com.pulasthi.tapit.ui.theme.TapItTextSecondary
import com.pulasthi.tapit.ui.theme.TapItWhite
import com.pulasthi.tapit.viewmodel.MyQrViewModel
import com.pulasthi.tapit.viewmodel.QrCodeType

private object MyQrDimens {
    val CardTopPadding = 120.dp
    val CardHorizontalPadding = 24.dp
    val CardInnerPadding = 24.dp
    val CardCornerRadius = 28.dp
    val BottomNavClearance = 88.dp
}

@Composable
fun MyQrScreen(
    viewModel: MyQrViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isPersonal = uiState.selectedType == QrCodeType.Personal

    FlowScreenScaffold(title = "My QR") {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(TapItBluePrimary)
                .padding(bottom = MyQrDimens.BottomNavClearance),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = MyQrDimens.CardHorizontalPadding)
                        .padding(top = MyQrDimens.CardTopPadding),
                    shape = RoundedCornerShape(MyQrDimens.CardCornerRadius),
                    color = TapItWhite,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(MyQrDimens.CardInnerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        QrTypeTab(
                            label = "Personal QR",
                            selected = isPersonal,
                            onClick = { viewModel.onQrTypeSelected(QrCodeType.Personal) },
                        )

                        Text(
                            text = uiState.instruction,
                            color = TapItTextPrimary,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 20.dp),
                        )

                        Box(
                            modifier = Modifier
                                .size(240.dp)
                                .background(TapItWhite, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = "QR code",
                                tint = TapItTextPrimary,
                                modifier = Modifier.size(200.dp),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun QrTypeTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    inactiveStyle: Boolean = false,
) {
    val background = when {
        selected -> TapItBluePrimary
        inactiveStyle -> TapItDisabledButton
        else -> TapItDisabledButton
    }
    val textColor = if (selected) TapItWhite else TapItTextSecondary

    Text(
        text = label,
        color = textColor,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .fillMaxWidth()
            .background(background, RoundedCornerShape(24.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 14.dp),
        textAlign = TextAlign.Center,
    )
}
