# Navigation 3 - Koncepty i implementacja

## Czym jest Navigation 3?

Navigation 3 to nowa biblioteka nawigacyjna zaprojektowana specjalnie dla Jetpack Compose, która daje pełną kontrolę nad stosem nawigacji (back stack).

## Główne koncepty

### 1. Back Stack jako lista

W Navigation 3 stos nawigacji to po prostu lista elementów:

```kotlin
interface BackStackEntry {
    val destination: NavigationDestination
}

interface BackStackState {
    val entries: List<BackStackEntry>
    val currentEntry: BackStackEntry?
}
```

Nawigacja = dodawanie/usuwanie elementów z listy.

### 2. Pełna kontrola nad stosem

```kotlin
// Dodaj do stosu
backStack.add(destination)

// Usuń ze stosu
backStack.remove(destination)

// Wyczyść i zastąp
backStack.clear()
backStack.add(newDestination)
```

### 3. Abstrakcja przez interfejsy

W naszej implementacji:

```kotlin
interface NavigationDestination {
    val key: String
}

sealed class Destination(override val key: String) : NavigationDestination {
    data object OrderList : Destination("order_list")
    data class OrderDetail(val orderId: String) : Destination("order_detail/$orderId")
}
```

### 4. Navigator jako bridge

```kotlin
interface Navigator {
    val navigationCommands: StateFlow<NavigationCommand?>
    
    fun navigate(destination: NavigationDestination)
    fun navigateBack(toDestination: NavigationDestination? = null)
    fun popBackStack(inclusive: Boolean = false)
    fun replaceWith(destination: NavigationDestination)
    fun clearCommand()
}
```

Navigator emituje komendy, które NavController interpretuje i wykonuje.

## Zalety Navigation 3 względem Navigation 2

### 1. Prostsza integracja z Compose

**Navigation 2:**
```kotlin
// Wymaga NavHost, NavGraphBuilder, String routes
NavHost(navController, startDestination = "home") {
    composable("home") { HomeScreen() }
    composable("details/{id}") { backStackEntry ->
        DetailsScreen(backStackEntry.arguments?.getString("id"))
    }
}
```

**Navigation 3:**
```kotlin
// Prosta funkcja mapująca
when (currentRoute) {
    is Route.Home -> HomeScreen()
    is Route.Details -> DetailsScreen(currentRoute.id)
}
```

### 2. Pełna kontrola nad back stackiem

**Navigation 2:**
- Back stack jest zarządzany wewnętrznie
- Trudno zmodyfikować wiele wpisów naraz
- Ograniczone API (navigate, popBackStack, popUpTo)

**Navigation 3:**
- Back stack to zwykła lista
- Możesz robić co chcesz: filtrować, mapować, czyścić
- Pełna elastyczność

### 3. Adaptive layouts

Navigation 3 pozwala na wyświetlanie wielu destinacji jednocześnie (np. master-detail na tabletach):

```kotlin
// Odczytaj wiele elementów ze stosu
val lastTwo = backStack.entries.takeLast(2)

// Wyświetl obok siebie na dużym ekranie
Row {
    Display(lastTwo[0])
    Display(lastTwo[1])
}
```

### 4. Type safety

```kotlin
// Navigation 2 - String routes (błędy w runtime)
navController.navigate("details/123")

// Navigation 3 - Sealed classes (błędy w compile time)
navController.navigate(Destination.OrderDetail(orderId = "123"))
```

## Nasza implementacja

### Struktura

```
libraries/navigation/
├── NavigationDestination.kt    # Interfejs miejsca docelowego
├── NavigationCommand.kt        # Sealed interface komend
├── Navigator.kt                # Interfejs nawigatora
├── NavigatorImpl.kt            # Implementacja (StateFlow)
└── BackStack.kt                # Abstrakcja stosu
```

### Przepływ danych

```
Feature (ViewModel)
    ↓ emit Effect
Compose Screen
    ↓ callback
NavGraph
    ↓ navigate()
Navigator
    ↓ emit NavigationCommand
NavController
    ↓ modyfikuje back stack
UI aktualizuje się automatycznie (recomposition)
```

### Przykład użycia

**1. Definiujemy destination:**
```kotlin
sealed class Destination(override val key: String) : NavigationDestination {
    data object OrderList : Destination("order_list")
    data class OrderDetail(val orderId: String) : Destination("order_detail/$orderId")
}
```

**2. ViewModel emituje effect:**
```kotlin
fun onOrderClick(orderId: String) {
    viewModelScope.launch {
        _effect.emit(OrderListEffect.NavigateToOrderDetail(orderId))
    }
}
```

**3. Screen reaguje na effect:**
```kotlin
LaunchedEffect(Unit) {
    viewModel.effect.collect { effect ->
        when (effect) {
            is OrderListEffect.NavigateToOrderDetail ->
                onNavigateToDetail(effect.orderId)
        }
    }
}
```

**4. NavGraph nawiguje:**
```kotlin
OrderListScreen(
    viewModel = viewModel,
    onNavigateToDetail = { orderId ->
        navController.navigate(Destination.OrderDetail(orderId).key)
    }
)
```

## Testowanie

Dzięki abstrakcji przez interfejsy, testowanie jest bardzo proste:

```kotlin
class FakeNavigator : Navigator {
    val commands = mutableListOf<NavigationCommand>()
    
    override fun navigate(destination: NavigationDestination) {
        commands.add(NavigateTo(destination))
    }
    
    // ... inne metody
}

@Test
fun `when order clicked, navigates to detail`() {
    val fakeNavigator = FakeNavigator()
    val viewModel = OrderListViewModel(useCase, fakeNavigator)
    
    viewModel.handleIntent(OrderListIntent.OnOrderClick("123"))
    
    // Sprawdź czy została wywołana nawigacja
    assertTrue(fakeNavigator.commands.any { 
        it is NavigateTo && 
        it.destination.key.contains("123") 
    })
}
```

## Migracja z Navigation 2

1. **Abstrakcja pozostaje ta sama** - feature'y nie wiedzą o frameworku nawigacji
2. **Zmiana tylko w module app** - NavGraph i NavController
3. **Stopniowa migracja** - można mieć oba systemy działające równolegle

## Podsumowanie

Navigation 3 to nowoczesne podejście do nawigacji w Compose:
- ✅ Prostsza integracja
- ✅ Pełna kontrola
- ✅ Type safety
- ✅ Adaptive layouts
- ✅ Łatwiejsze testowanie

Nasza abstrakcja w `libraries/navigation` sprawia, że zmiana frameworka nawigacji nie wymaga zmian w feature'ach - wystarczy podmienić implementację w module `app`.
