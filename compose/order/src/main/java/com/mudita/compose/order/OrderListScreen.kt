package com.mudita.compose.order

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mudita.compose.navigation.NavActionsEffect
import com.mudita.core.domain.model.Order
import com.mudita.features.order.presentation.OrderListIntent
import com.mudita.features.order.presentation.OrderListState
import com.mudita.features.order.presentation.OrderListViewModel
import com.mudita.libraries.navigation.AppNavigator
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(
    navigator: AppNavigator,
    viewModel: OrderListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    NavActionsEffect(
        actions = viewModel.navActions,
        navigator = navigator
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zamówienia") },
                actions = {
                    TextButton(
                        onClick = { viewModel.handleIntent(OrderListIntent.OnLogout) }
                    ) {
                        Text("Wyloguj")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.handleIntent(OrderListIntent.OnAddOrderClick) }
            ) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_input_add),
                    contentDescription = "Dodaj zamówienie"
                )
            }
        }
    ) { paddingValues ->
        OrderListContent(
            state = state,
            onOrderClick = { orderId -> 
                viewModel.handleIntent(OrderListIntent.OnOrderClick(orderId))
            },
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun OrderListContent(
    state: OrderListState,
    onOrderClick: (String) -> Unit,
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
            
            state.orders.isEmpty() -> {
                Text("Brak zamówień")
            }
            
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.orders) { order ->
                        OrderListItem(
                            order = order,
                            onClick = { onOrderClick(order.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderListItem(
    order: Order,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = order.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = order.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            Text(
                text = "Status: ${order.status.name}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            Text(
                text = "Kwota: ${order.totalAmount} zł",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
