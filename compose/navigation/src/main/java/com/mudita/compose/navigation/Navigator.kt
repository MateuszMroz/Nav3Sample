package com.mudita.compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.serialization.NavBackStackSerializer
import androidx.navigation3.runtime.serialization.NavKeySerializer
import com.mudita.core.navigation.NavRoute



/**
 * Proposal of Convenience method to build navigation Root
 * It's example of usage is in [AuthNavigation] file
 * */
@Composable
fun <RouteGroup : NavRoute> NavigableContent(
    start: RouteGroup,
    content: @Composable NavBackStack<RouteGroup>.() -> Unit
) {
    val backStack: NavBackStack<RouteGroup> = rememberNavBackStack<RouteGroup>(start)
    backStack.content()
}


/**
 * This is exact copy of [androidx.navigation3.runtime.rememberNavBackStack] but without
 * missing generic [RouteGroup] type
 * */
@Composable
fun <RouteGroup : NavRoute> rememberNavBackStack(vararg elements: RouteGroup): NavBackStack<RouteGroup> {
    return rememberSerializable(
        serializer = NavBackStackSerializer(elementSerializer = NavKeySerializer())
    ) {
        NavBackStack(*elements)
    }
}

fun <RouteGroup : NavRoute> NavBackStack<RouteGroup>.navigateTo(route: RouteGroup): Boolean {
    return add(route)
}

fun <RouteGroup : NavRoute> NavBackStack<RouteGroup>.navigateUp() {
    if (size > 1) {
        removeLastOrNull()
    }
}

fun <RouteGroup : NavRoute> NavBackStack<RouteGroup>.popBackTo(
    route: RouteGroup,
    inclusive: Boolean
) {
    val index = indexOfLast { it == route }
    if (index != -1) {
        val removeFrom = if (inclusive) index else index + 1
        while (size > removeFrom) {
            removeLastOrNull()
        }
    }
}

fun <RouteGroup : NavRoute> NavBackStack<RouteGroup>.clearWith(route: RouteGroup) {
    val oldRoute = lastOrNull()
    val routeToAdd = if (oldRoute == route) oldRoute else route

    clear()
    add(routeToAdd)
}
