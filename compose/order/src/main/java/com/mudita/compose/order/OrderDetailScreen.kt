package com.mudita.compose.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mudita.core.domain.model.Order
import com.mudita.features.order.presentation.OrderDetailIntent
import com.mudita.features.order.presentation.OrderDetailState
import com.mudita.features.order.presentation.OrderDetailViewModel
import com.mudita.libraries.navigation.AppNavigator
import com.mudita.compose.navigation.NavActionsEffect
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: String,
    navigator: AppNavigator,
    viewModel: OrderDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Observe navigation actions from ViewModel - reusable!
    NavActionsEffect(
        actions = viewModel.navActions,
        navigator = navigator
    )
    
    LaunchedEffect(orderId) {
        viewModel.handleIntent(OrderDetailIntent.LoadOrder(orderId))
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Szczegóły zamówienia") },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.handleIntent(OrderDetailIntent.OnBackClick) }
                    ) {
                        Icon(
                            imageVector = Default.ArrowBack,
                            contentDescription = "Powrót"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        OrderDetailContent(
            state = state,
            onEditClick = { viewModel.handleIntent(OrderDetailIntent.OnEditClick) },
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun OrderDetailContent(
    state: OrderDetailState,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator()
            }
            
            state.error != null -> {
                Text(
                    text = "Błąd: ${state.error}",
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            state.order != null -> {
                OrderDetailItem(
                    order = state.order,
                    onEditClick = onEditClick
                )
            }
            
            else -> {
                Text("Nie znaleziono zamówienia")
            }
        }
    }
}

@Composable
fun OrderDetailItem(
    order: Order?,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (order == null) return
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = order.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DetailRow(
                        label = "ID:",
                        value = order.id
                    )
                    
                    DetailRow(
                        label = "Opis:",
                        value = order.description
                    )
                    
                    DetailRow(
                        label = "Status:",
                        value = order.status.name
                    )
                    
                    DetailRow(
                        label = "Kwota:",
                        value = "${order.totalAmount} zł"
                    )
                }
            }
        }
        
        Button(
            onClick = onEditClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Edytuj zamówienie")
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
