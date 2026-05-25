package com.pulasthi.tapit.ui.bills

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pulasthi.tapit.ui.theme.TapItBluePrimary
import com.pulasthi.tapit.ui.theme.TapItInputBackground
import com.pulasthi.tapit.ui.theme.TapItTextHint
import com.pulasthi.tapit.ui.theme.TapItTextPrimary
import com.pulasthi.tapit.ui.theme.TapItTextSecondary
import com.pulasthi.tapit.ui.theme.TapItWhite
import com.pulasthi.tapit.viewmodel.BillCategoryItem
import com.pulasthi.tapit.viewmodel.BillProvider
import com.pulasthi.tapit.viewmodel.PayBillsContentMode
import com.pulasthi.tapit.viewmodel.PayBillsHubItem
import com.pulasthi.tapit.viewmodel.PayBillsViewModel

@Composable
fun PayBillsScreen(
    onBack: () -> Unit,
    onNavigateToPaymentForm: () -> Unit,
    viewModel: PayBillsViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigateToPaymentForm.collect { onNavigateToPaymentForm() }
    }

    val headerTitle = when (uiState.contentMode) {
        PayBillsContentMode.HUB -> uiState.hubTitle
        PayBillsContentMode.PROVIDERS -> uiState.providersScreenTitle
    }

    val onHeaderBack = when (uiState.contentMode) {
        PayBillsContentMode.HUB -> onBack
        PayBillsContentMode.PROVIDERS -> viewModel::onExitProviders
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TapItBluePrimary),
    ) {
        PayBillsHeader(
            searchQuery = uiState.searchQuery,
            onSearchQueryChange = viewModel::onSearchQueryChange,
            onBack = onHeaderBack,
            showTitle = uiState.contentMode == PayBillsContentMode.HUB,
            title = headerTitle,
        )

        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = TapItInputBackground,
        ) {
            when (uiState.contentMode) {
                PayBillsContentMode.HUB -> PayBillsHubContent(
                    items = uiState.hubListItems,
                    onCategoryClick = viewModel::onCategorySelected,
                    onProviderClick = viewModel::onHubProviderSelected,
                )
                PayBillsContentMode.PROVIDERS -> PayBillsProvidersContent(
                    title = uiState.providersScreenTitle,
                    providers = uiState.filteredProviders,
                    onProviderClick = viewModel::onProviderSelected,
                )
            }
        }
    }
}

@Composable
private fun PayBillsHubContent(
    items: List<PayBillsHubItem>,
    onCategoryClick: (BillCategoryItem) -> Unit,
    onProviderClick: (BillProvider) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp),
    ) {
        Text(
            text = "Pay Bills",
            color = TapItTextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(
                items = items,
                key = { item ->
                    when (item) {
                        is PayBillsHubItem.Category -> "cat_${item.category.id.id}"
                        is PayBillsHubItem.Provider -> "prov_${item.provider.id}"
                    }
                },
            ) { item ->
                when (item) {
                    is PayBillsHubItem.Category -> BillCategoryRow(
                        category = item.category,
                        onClick = { onCategoryClick(item.category) },
                    )
                    is PayBillsHubItem.Provider -> BillProviderRow(
                        provider = item.provider,
                        subtitle = null,
                        onClick = { onProviderClick(item.provider) },
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun PayBillsProvidersContent(
    title: String,
    providers: List<BillProvider>,
    onProviderClick: (BillProvider) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp),
    ) {
        Text(
            text = title,
            color = TapItTextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
        )

        if (providers.isEmpty()) {
            Text(
                text = "No providers match your search",
                color = TapItTextSecondary,
                fontSize = 15.sp,
                modifier = Modifier.padding(top = 8.dp),
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(
                    items = providers,
                    key = { it.id },
                ) { provider ->
                    BillProviderRow(
                        provider = provider,
                        subtitle = null,
                        onClick = { onProviderClick(provider) },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun BillProviderLogo(
    provider: BillProvider,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 56.dp,
) {
    val brand = provider.brand
    Surface(
        modifier = modifier.size(size),
        shape = CircleShape,
        color = brand.primaryColor,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = brand.logoText,
                    color = TapItWhite,
                    fontSize = if (brand.logoText.length > 5) 9.sp else 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
                brand.logoSubtext?.let { sub ->
                    Text(
                        text = sub,
                        color = brand.secondaryColor,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Composable
fun BillProviderRow(
    provider: BillProvider,
    subtitle: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BillProviderLogo(provider = provider)
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = provider.name,
                color = TapItTextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
            )
            subtitle?.let {
                Text(
                    text = it,
                    color = TapItTextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
    }
}

@Composable
private fun BillCategoryRow(
    category: BillCategoryItem,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = TapItBluePrimary,
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = null,
                    tint = TapItWhite,
                    modifier = Modifier.size(26.dp),
                )
            }
        }
        Text(
            text = category.title,
            color = TapItTextPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp),
        )
    }
}

@Composable
private fun PayBillsHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onBack: () -> Unit,
    showTitle: Boolean,
    title: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = {
                Text("Search", color = TapItTextHint)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = TapItBluePrimary,
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF2D2D2D),
                unfocusedContainerColor = Color(0xFF2D2D2D),
                focusedTextColor = TapItWhite,
                unfocusedTextColor = TapItWhite,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = TapItWhite,
            ),
            singleLine = true,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TapItWhite,
                )
            }
            if (showTitle) {
                Text(
                    text = title,
                    color = TapItWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp),
                )
            }
        }
    }
}
