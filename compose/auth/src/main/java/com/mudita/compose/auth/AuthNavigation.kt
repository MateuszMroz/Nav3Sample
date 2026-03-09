package com.mudita.compose.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.mudita.compose.navigation.rememberNavigator
import com.mudita.features.auth.navigation.Auth

/**
 * Auth nested navigation graph.
 * Manages internal auth flow (Login <-> Register)
 */
@Composable
fun AuthNavigation(
    modifier: Modifier = Modifier
) {
    val authNavigator = rememberNavigator(startDestination = Auth.Login)
    
    NavDisplay(
        backStack = authNavigator.backStack,
        modifier = modifier,
        onBack = { authNavigator.navigateUp() },
        entryProvider = entryProvider {
            entry<Auth.Login> {
                LoginScreen(
                    navigator = authNavigator
                )
            }
            
            entry<Auth.Register> {
                RegisterScreen(
                    navigator = authNavigator
                )
            }
        }
    )
}
