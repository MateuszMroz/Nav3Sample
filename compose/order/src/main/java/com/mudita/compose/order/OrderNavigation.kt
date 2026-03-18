package com.mudita.compose.order

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.mudita.compose.navigation.navigateTo
import com.mudita.compose.navigation.navigateUp
import com.mudita.compose.navigation.rememberNavBackStack
import com.mudita.features.order.navigation.Order

/**
 * Order nested navigation graph.
 * Manages internal order flow (List -> Detail -> Add)
 */
@Composable
fun OrderNavigation(
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack<Order>(Order.List)

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { backStack.navigateUp() },
        entryProvider = entryProvider {
            entry<Order.List> {
                OrderListScreen(
                    onNavigateToOrderDetail = { orderId ->
                        backStack.navigateTo(Order.Detail(orderId))
                    },
                    onNavigateToAddOrder = { backStack.navigateTo(Order.Add) }
                )
            }

            entry<Order.Detail> { route ->
                OrderDetailScreen(
                    orderId = route.orderId,
                    onNavigateBack = { backStack.navigateUp() },
                    onNavigateToEditOrder = { TODO("No route") },
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
