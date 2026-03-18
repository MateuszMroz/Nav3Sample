package com.mudita.compose.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.mudita.compose.navigation.NavigableContent
import com.mudita.compose.navigation.navigateTo
import com.mudita.compose.navigation.navigateUp
import com.mudita.features.auth.navigation.Auth

/**
 * Auth nested navigation graph.
 * Manages internal auth flow (Login <-> Register)
 */
@Composable
fun AuthNavigation(
    modifier: Modifier = Modifier
) = NavigableContent<Auth>(Auth.Login) {
    NavDisplay(
        backStack = this,
        modifier = modifier,
        onBack = { navigateUp() },
        entryProvider = entryProvider {
            entry<Auth.Login> {
                LoginScreen(
                    onNavigateToDetails = { navigateTo(Auth.Register) }
                )
            }

            entry<Auth.Register> {
                RegisterScreen(
                    onNavigateUp = { navigateUp() }
                )
            }
        }
    )
}
