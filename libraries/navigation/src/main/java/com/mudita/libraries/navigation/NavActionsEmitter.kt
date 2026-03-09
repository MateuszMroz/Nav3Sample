package com.mudita.libraries.navigation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Factory function to create NavActionsEmitter.
 */
fun NavActionsEmitter(): NavActionsEmitter = NavActionsEmitterImpl()

/**
 * Emitter for navigation actions.
 * Use delegation pattern in ViewModels: `by NavActionsEmitter()`
 */
interface NavActionsEmitter {
    val navActions: Flow<NavAction>
    suspend fun emitNavAction(action: NavAction)
}

private class NavActionsEmitterImpl : NavActionsEmitter {
    // BUFFERED - prevents losing actions when UI temporarily not observing
    private val _navActions = Channel<NavAction>(Channel.BUFFERED)
    override val navActions: Flow<NavAction> = _navActions.receiveAsFlow()

    override suspend fun emitNavAction(action: NavAction) {
        _navActions.send(action)
    }
}
