package com.mudita.compose.order

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.mudita.compose.navigation.rememberNavigator
import com.mudita.compose.order.OrderDetailScreen
import com.mudita.compose.order.OrderListScreen
import com.mudita.features.order.navigation.Order

/**
 * Order nested navigation graph.
 * Manages internal order flow (List -> Detail -> Add)
 */
@Composable
fun OrderNavigation(
    modifier: Modifier = Modifier
) {
    val orderNavigator = rememberNavigator(startDestination = Order.List)
    
    NavDisplay(
        backStack = orderNavigator.backStack,
        modifier = modifier,
        onBack = { orderNavigator.navigateUp() },
        entryProvider = entryProvider {
            entry<Order.List> {
                OrderListScreen(
                    navigator = orderNavigator
                )
            }
            
            entry<Order.Detail> { route ->
                OrderDetailScreen(
                    orderId = route.orderId,
                    navigator = orderNavigator
                )
            }
            
            entry<Order.Add> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Ekran dodawania zamówienia (TODO)")
                }
            }
        }
    )
}
