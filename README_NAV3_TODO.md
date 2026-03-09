# Navigation 3 Sample - TODO

## Status: W trakcie implementacji

Przepraszam za zamieszanie. Navigation 3 ma zupełnie inne API niż Navigation 2, którego użyłem jako wzór.

## Główne różnice między Nav2 a Nav3:

### Navigation 2:
- `NavController` - centralny kontroler nawigacji
- `NavHost` - kontener dla wszystkich destynacji
- `composable()` - definicje ekranów w NavHost
- String routes lub type-safe routes

### Navigation 3:
- **Back Stack** - zarządzanie stosem nawigacji jako listą
- **NavDisplay** - komponent UI wyświetlający stos
- **Scene Strategies** - strategie renderowania (jak jeden ekran, dual pane, itp.)
- Pełna kontrola nad back stackiem

## Poprawna implementacja Nav3 wymaga:

### 1. Zarządzanie Back Stackiem
```kotlin
val backStack = rememberMutableBackStack<Route>(initial = OrderListRoute)

// Nawiguj przez modyfikację listy
backStack.push(OrderDetailRoute(orderId))
backStack.pop()
```

### 2. NavDisplay zamiast NavHost
```kotlin
NavDisplay(
    backStack = backStack,
    sceneStrategy = SingleSceneStrategy()
) { route ->
    when (route) {
        is OrderListRoute -> OrderListScreen()
        is OrderDetailRoute -> OrderDetailScreen(route.orderId)
    }
}
```

### 3. Scene Strategies
- `SingleSceneStrategy` - jeden ekran na raz
- `DualPaneSceneStrategy` - dwa ekrany obok siebie (tablety)
- Custom strategies - własne layouty

## Co trzeba zrobić:

1. ✅ Struktura modułów jest poprawna
2. ✅ Koin DI jest skonfigurowany
3. ✅ MVI w features jest poprawne
4. ✅ Abstrakcja nawigacji (Route, NavAction, etc.) jest dobra
5. ❌ **Nav3AppNavigator** - musi używać Back Stack API, nie NavController
6. ❌ **AppNavGraph** - musi używać NavDisplay z właściwym API
7. ❌ **MainActivity** - musi używać rememberMutableBackStack

## Następne kroki:

Użyj dokumentacji:
- https://developer.android.com/guide/navigation/navigation-3/get-started
- https://developer.android.com/guide/navigation/navigation-3/basics

Kluczowe API:
```kotlin
// Zamiast NavController
val backStack = rememberMutableBackStack<Route>(OrderListRoute)

// Nawigacja
backStack.push(destination)
backStack.pop()
backStack.popTo(destination, inclusive = false)

// Wyświetlanie
NavDisplay(
    backStack = backStack,
    sceneStrategy = remember { SingleSceneStrategy() }
) { route -> /* render composable */ }
```

## Obecna implementacja

Utworzona struktura:
- ✅ `libraries/navigation` - abstrakcja z Route, NavAction, NavActionsEmitter
- ✅ `features/order` - MVI ViewModels
- ✅ `compose/order` - Ekrany Compose
- ✅ `app` - Routes i Koin DI

**Problem**: Używam nieistniejącego API (NavController w Nav3).

## Rozwiązanie

Potrzebuję przepisać:
1. `Nav3AppNavigator` - wrapper na Back Stack
2. `AppNavGraph` - używać NavDisplay z backStack
3. `MainActivity` - używać rememberMutableBackStack

Alternatywnie, mogę użyć Jetpack Navigation Compose (które jest stabilne i ma type-safe routing z kotlinx.serialization), zamiast eksperymentalnego Nav3.

Prz

ypominam: Navigation 3 (1.0.1) jest nowsze, ale Jetpack Navigation Compose jest bardziej dojrzałe i powszechnie używane.
