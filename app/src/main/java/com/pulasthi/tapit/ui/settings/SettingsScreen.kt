package com.pulasthi.tapit.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pulasthi.tapit.ui.home.PromoBanner
import com.pulasthi.tapit.ui.settings.components.SettingsMenuGrid
import com.pulasthi.tapit.ui.settings.components.SettingsScaffold
import com.pulasthi.tapit.ui.theme.TapItBluePrimary
import com.pulasthi.tapit.ui.theme.TapItWhite
import com.pulasthi.tapit.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    onNavigateTo: (String) -> Unit,
    onNavigateToWallet: () -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigateTo.collect { route -> onNavigateTo(route) }
    }
    LaunchedEffect(Unit) {
        viewModel.navigateToWallet.collect { onNavigateToWallet() }
    }
    LaunchedEffect(Unit) {
        viewModel.logout.collect { onLogout() }
    }

    SettingsScaffold(title = "Settings", onBack = onBack) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 20.dp),
        ) {
            PromoBanner(
                titleLine1 = "The Secret to Safe",
                titleLine2 = "Online Money Transfers",
            )

            Spacer(modifier = Modifier.height(24.dp))

            SettingsMenuGrid(
                items = uiState.menuItems,
                onItemClick = viewModel::onMenuItemClick,
                modifier = Modifier.weight(1f),
            )

            Button(
                onClick = viewModel::onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TapItBluePrimary,
                    contentColor = TapItWhite,
                ),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp),
                )
                Text(
                    text = "Logout",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
