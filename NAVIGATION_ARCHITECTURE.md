# Architektura Nawigacji - Nav3 + Koin

## Przegląd

Implementacja nawigacji oparta na AndroidX Navigation 3 z integracją Koin DI, wykorzystująca wzorzec NavActionsEmitter dla czystego separation of concerns.

## Struktura modułów

```
:libraries:navigation          # Interfejsy, NavAction, NavActionsEmitter
:core:navigation               # NavRoute (Route + NavKey bridge)
:app                           # Navigator, rememberNavigator
:compose:common                # NavActionsEffect
:features:order                # ViewModels, Routes
:compose:order                 # Screens
```

## Przepływ danych

```
User action (klik w UI)
    │
    ▼
ViewModel.onXClick()
    │ viewModelScope.launch
    ▼
NavActionsEmitter.emitNavAction(NavAction.NavigateTo(Route))
    │ Channel<NavAction>(BUFFERED)
    ▼
Flow<NavAction>
    │ LaunchedEffect (aktywny tylko gdy UI widoczne)
    ▼
NavActionsEffect
    │
    ▼
Navigator.navigateTo(route)
    │
    ▼
NavBackStack.add(route)  ← rememberNavBackStack (persistence + system back)
    │
    ▼
NavDisplay rekomponuje
```

## Komponenty

### 1. Route (`:libraries:navigation`)

```kotlin
interface Route
```

Bazowy interfejs dla wszystkich tras nawigacji. Czysta abstrakcja bez zależności od frameworka.

### 2. NavRoute (`:core:navigation`)

```kotlin
interface NavRoute : Route, NavKey
```

**Bridge między abstrakcją a Navigation 3.**

Łączy nasz `Route` z `NavKey` z Navigation 3 w jeden interfejs.

**Zalety:**
- **Jeden interfejs** - zamiast `Route, NavKey` tylko `NavRoute`
- **Centralne miejsce** - w module `:core:navigation`
- **Type-safe** - działa z `rememberNavBackStack<NavRoute>`
- **Clean** - domain layer używa tylko `Route`, implementacja używa `NavRoute`

**Użycie:**
```kotlin
@Serializable
sealed interface Order : NavRoute {
    @Serializable
    data object List : Order
    
    @Serializable
    data class Detail(val id: String) : Order
}
```

### 3. NavAction (`:libraries:navigation`)

```kotlin
sealed class NavAction {
    data object NavigateUp : NavAction()
    data class NavigateTo(val route: Route) : NavAction()
    data class PopBackTo(
        val route: Route,
        val inclusive: Boolean = false
    ) : NavAction()
}
```

Reprezentuje akcje nawigacyjne emitowane przez ViewModele.

### 4. NavActionsEmitter (`:libraries:navigation`)

```kotlin
fun NavActionsEmitter(): NavActionsEmitter = NavActionsEmitterImpl()

interface NavActionsEmitter {
    val navActions: Flow<NavAction>
    suspend fun emitNavAction(action: NavAction)
}

private class NavActionsEmitterImpl : NavActionsEmitter {
    // BUFFERED - nie gubi akcji gdy UI chwilowo nie obserwuje
    private val _navActions = Channel<NavAction>(Channel.BUFFERED)
    override val navActions: Flow<NavAction> = _navActions.receiveAsFlow()

    override suspend fun emitNavAction(action: NavAction) {
        _navActions.send(action)
    }
}
```

**Kluczowe cechy:**
- `Channel.BUFFERED` - akcje czekają gdy UI jest chwilowo nieaktywne
- Brak race conditions
- Brak memory leaks

### 5. AppNavigator (`:libraries:navigation`)

```kotlin
interface AppNavigator {
    fun navigateTo(route: Route)
    fun navigateUp()
    fun popBackTo(route: Route, inclusive: Boolean = false)
}
```

Interfejs dla operacji nawigacyjnych.

### 6. Navigator (`:app`)

```kotlin
@Stable
class Navigator(
    val backStack: NavBackStack<NavRoute>
) : AppNavigator {

    override fun navigateTo(route: Route) {
        require(route is NavRoute) { "Route must implement NavRoute" }
        backStack.add(route)
    }

    override fun navigateUp() {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }

    override fun popBackTo(route: Route, inclusive: Boolean) {
        require(route is NavRoute) { "Route must implement NavRoute" }
        val index = backStack.indexOfLast { it == route }
        if (index != -1) {
            val removeFrom = if (inclusive) index else index + 1
            while (backStack.size > removeFrom) {
                backStack.removeLastOrNull()
            }
        }
    }
}

@Composable
fun rememberNavigator(startDestination: NavRoute): Navigator {
    val backStack = rememberNavBackStack(startDestination)
    return remember { Navigator(backStack) }
}
```

**Kluczowe cechy:**
- Używa `rememberNavBackStack` dla persistence i system back
- Żyje w Compose, NIE w Koin DI
- Routes muszą implementować `NavRoute`
- Type-safe dzięki `NavBackStack<NavRoute>`

### 7. NavActionsEffect (`:compose:common`)

```kotlin
@Composable
fun NavActionsEffect(
    actions: Flow<NavAction>,
    navigator: AppNavigator
) {
    LaunchedEffect(Unit) {
        actions.collect { action ->
            when (action) {
                is NavAction.NavigateUp -> navigator.navigateUp()
                is NavAction.NavigateTo -> navigator.navigateTo(action.route)
                is NavAction.PopBackTo -> navigator.popBackTo(action.route, action.inclusive)
            }
        }
    }
}
```

**Kluczowe cechy:**
- `LaunchedEffect` - cancelowany gdy UI znika
- Połączenie między ViewModelem a Navigatorem
- Reusable w każdym screenie

## Użycie

### Definiowanie Routes

```kotlin
@Serializable
sealed interface Order : NavRoute {
    
    @Serializable
    data object List : Order
    
    @Serializable
    data class Detail(val orderId: String) : Order
    
    @Serializable
    data object Add : Order
}
```

**Wymagania:**
- `@Serializable` - dla Nav3
- Implementuje `NavRoute` (który rozszerza `Route` + `NavKey`)

**Korzyści NavRoute:**
- Jeden interfejs zamiast dwóch (`Route, NavKey`)
- Centralne miejsce w `:core:navigation`
- Type-safe z `rememberNavBackStack<NavRoute>`
- Clean abstraction dla domain layer

### ViewModel - Delegation Pattern

```kotlin
class OrderListViewModel(
    private val getOrdersUseCase: GetOrdersUseCase
) : ViewModel(), NavActionsEmitter by NavActionsEmitter() {
    
    private val _state = MutableStateFlow(OrderListState())
    val state: StateFlow<OrderListState> = _state.asStateFlow()
    
    fun onOrderClick(orderId: String) {
        viewModelScope.launch {
            emitNavAction(NavigateTo(Order.Detail(orderId)))
        }
    }
    
    fun onBackClick() {
        viewModelScope.launch {
            emitNavAction(NavAction.NavigateUp)
        }
    }
}
```

**Kluczowe cechy:**
- `by NavActionsEmitter()` - delegation pattern
- Brak zależności od `Navigator` - łatwe testowanie
- `emitNavAction()` dostępne bezpośrednio
- `navActions: Flow<NavAction>` dostępne dla UI

### Screen - Używanie NavActionsEffect

```kotlin
@Composable
fun OrderListScreen(
    navigator: AppNavigator,
    viewModel: OrderListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    // Połącz ViewModel z Navigator
    NavActionsEffect(
        actions = viewModel.navActions,
        navigator = navigator
    )
    
    // UI
    Scaffold(
        topBar = { /* ... */ },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.handleIntent(OnAddOrderClick) }
            ) {
                Icon(/* ... */)
            }
        }
    ) { paddingValues ->
        OrderListContent(
            state = state,
            onOrderClick = { orderId -> 
                viewModel.handleIntent(OnOrderClick(orderId))
            },
            modifier = Modifier.padding(paddingValues)
        )
    }
}
```

### Koin Module - Brak Navigator w DI

```kotlin
val orderFeatureModule = module {
    // Use Cases
    factory { GetOrdersUseCase(get()) }
    factory { GetOrderByIdUseCase(get()) }
    
    // ViewModels - bez NavActionsEmitter (używa delegation)
    viewModel { OrderListViewModel(get()) }
    viewModel { OrderDetailViewModel(get()) }
}

val navigationModule = module {
    // Pusty - Navigator żyje w Compose
}
```

### NavRoot - Tworzenie Navigator

```kotlin
@Composable
fun NavRoot(modifier: Modifier = Modifier) {
    // Navigator z rememberNavBackStack
    val navigator = rememberNavigator(startDestination = Order.List)
    
    NavDisplay(
        backStack = navigator.backStack,
        modifier = modifier,
        onBack = { navigator.navigateUp() },
        entryProvider = entryProvider {
            entry<Order.List> {
                OrderListScreen(navigator = navigator)
            }
            
            entry<Order.Detail> { route ->
                OrderDetailScreen(
                    orderId = route.orderId,
                    navigator = navigator
                )
            }
            
            entry<Order.Add> {
                AddOrderScreen(navigator = navigator)
            }
        }
    )
}
```

## Rozwiązanie problemów

| Problem | Rozwiązanie |
|---------|-------------|
| **Race condition przy nawigacji** | `Channel.BUFFERED` - akcje czekają na UI |
| **Nawigacja gdy UI w tle** | `LaunchedEffect` cancelowany gdy UI znika |
| **Memory leak** | Brak referencji do Navigator w ViewModelu |
| **Trudne testowanie** | ViewModel emituje `Flow<NavAction>` - łatwo mockować |
| **Process death / rotacja** | `rememberNavBackStack` - automatyczna persistence |
| **System back** | `rememberNavBackStack` - automatyczna integracja |
| **ViewModels przeżyją rotację** | Navigator nie jest w ViewModelu |

## Korzyści architektury

### ✅ Separation of Concerns
- ViewModele: logika biznesowa + emitowanie akcji
- Navigator: tylko nawigacja
- Screens: UI + połączenie ViewModel↔Navigator
- NavRoute: bridge między abstrakcją a Nav3

### ✅ Testowalność
```kotlin
@Test
fun `clicking order navigates to detail`() = runTest {
    val viewModel = OrderListViewModel(mockUseCase)
    val actions = mutableListOf<NavAction>()
    
    viewModel.navActions.test {
        viewModel.onOrderClick("123")
        
        val action = awaitItem()
        assert(action is NavigateTo)
        assert((action as NavigateTo).route == Order.Detail("123"))
    }
}
```

### ✅ Brak Framework Dependencies w Domain Layer
- Pure Kotlin w domain layer
- NavActionsEmitter używa tylko Kotlin Coroutines
- NavRoute w `:core:navigation` - most między warstwami
- Łatwe przeniesienie do KMP

### ✅ Persistence i System Back "za darmo"
- `rememberNavBackStack` obsługuje to automatycznie
- Brak dodatkowego kodu

### ✅ Brak Race Conditions
- `Channel.BUFFERED` buforuje akcje
- `LaunchedEffect` zarządza lifecycle
- Bezpieczne w asynchronicznych scenariuszach

## Best Practices

1. **Zawsze używaj delegation pattern w ViewModelach**
   ```kotlin
   class MyViewModel : ViewModel(), NavActionsEmitter by NavActionsEmitter()
   ```

2. **Zawsze dodawaj NavActionsEffect w screenach**
   ```kotlin
   NavActionsEffect(
       actions = viewModel.navActions,
       navigator = navigator
   )
   ```

3. **Routes implementują NavRoute**
   ```kotlin
   @Serializable
   data class MyRoute(val id: String) : NavRoute
   ```

4. **Navigator tworzony tylko raz w NavRoot**
   ```kotlin
   val navigator = rememberNavigator(startDestination)
   ```

5. **Nie przechowuj Navigator w ViewModelu ani DI**
   - Żyje w Compose
   - Przekazywany do screenów przez parametry

## Migracja z innych rozwiązań

### Z Navigation Component 2.x
1. Zmień routes na `@Serializable` data classes
2. Implementuj `NavRoute` (zamiast tylko `Route`)
3. Zamień `NavController` na `Navigator`
4. Użyj `rememberNavBackStack` zamiast `rememberNavController`
5. ViewModele: dodaj `NavActionsEmitter by NavActionsEmitter()`
6. Screens: dodaj `NavActionsEffect()`

### Z bezpośrednią zależnością Navigator w ViewModelu
1. Usuń `Navigator` z parametrów konstruktora
2. Dodaj `NavActionsEmitter by NavActionsEmitter()`
3. Zamień `navigator.navigateTo()` na `emitNavAction(NavigateTo())`
4. Dodaj `NavActionsEffect()` w screenie

### Z implementacją Route + NavKey
1. Dodaj moduł `:core:navigation`
2. Zmień `Route, NavKey` na `NavRoute`
3. Zaktualizuj `Navigator` do użycia `NavBackStack<NavRoute>`
4. Wszystkie routes dziedziczą z `NavRoute`

## Przykłady

Zobacz:
- `/features/order` - kompletna implementacja feature'a
- `/compose/order` - screens z NavActionsEffect
- `/app/navigation` - Navigator i rememberNavigator
- `/libraries/navigation` - interfejsy i NavActionsEmitter
