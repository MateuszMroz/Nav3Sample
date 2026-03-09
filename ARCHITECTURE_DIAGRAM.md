# Diagram architektury projektu

## Zależności między modułami

```
┌─────────────────────────────────────────────────────────────┐
│                         APP MODULE                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ MainActivity │  │   NavGraph   │  │  AppModule   │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│         │                 │                  │               │
│         └─────────────────┴──────────────────┘               │
└─────────────────────────────────────────────────────────────┘
         │                  │                  │
         ▼                  ▼                  ▼
┌─────────────────┐  ┌──────────────────┐  ┌──────────────┐
│ compose/order   │  │ features/order   │  │  core/data   │
│ ┌─────────────┐ │  │ ┌──────────────┐ │  │ ┌──────────┐ │
│ │ OrderList   │ │  │ │ OrderList    │ │  │ │Repository│ │
│ │ Screen      │ │  │ │ ViewModel    │ │  │ │   Impl   │ │
│ └─────────────┘ │  │ └──────────────┘ │  │ └──────────┘ │
│ ┌─────────────┐ │  │ ┌──────────────┐ │  └──────────────┘
│ │ OrderDetail │ │  │ │ OrderDetail  │ │         │
│ │ Screen      │ │  │ │ ViewModel    │ │         ▼
│ └─────────────┘ │  │ └──────────────┘ │  ┌──────────────┐
└─────────────────┘  └──────────────────┘  │ core/domain  │
         │                     │            │ ┌──────────┐ │
         │                     │            │ │  Model   │ │
         │                     │            │ │Repository│ │
         │                     │            │ │ UseCase  │ │
         │                     │            │ └──────────┘ │
         │                     │            └──────────────┘
         │                     │                   │
         └─────────────────────┴───────────────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │ libraries/navigation │
                    │ ┌──────────────────┐ │
                    │ │ Navigator        │ │
                    │ │ NavigationCmd    │ │
                    │ │ Destination      │ │
                    │ └──────────────────┘ │
                    └──────────────────────┘
```

## Przepływ nawigacji

```
┌──────────────────────────────────────────────────────────────┐
│ 1. USER INTERACTION                                           │
│    OrderListScreen: User clicks on order                      │
└────────────────────────────┬─────────────────────────────────┘
                             │
                             ▼
┌──────────────────────────────────────────────────────────────┐
│ 2. INTENT                                                     │
│    viewModel.handleIntent(OrderListIntent.OnOrderClick(id))   │
└────────────────────────────┬─────────────────────────────────┘
                             │
                             ▼
┌──────────────────────────────────────────────────────────────┐
│ 3. VIEWMODEL PROCESSING                                       │
│    OrderListViewModel: Process intent → Emit effect          │
│    _effect.emit(NavigateToOrderDetail(orderId))              │
└────────────────────────────┬─────────────────────────────────┘
                             │
                             ▼
┌──────────────────────────────────────────────────────────────┐
│ 4. EFFECT COLLECTION                                          │
│    LaunchedEffect { viewModel.effect.collect { ... } }       │
└────────────────────────────┬─────────────────────────────────┘
                             │
                             ▼
┌──────────────────────────────────────────────────────────────┐
│ 5. NAVIGATION CALLBACK                                        │
│    onNavigateToDetail(orderId) called                        │
└────────────────────────────┬─────────────────────────────────┘
                             │
                             ▼
┌──────────────────────────────────────────────────────────────┐
│ 6. NAV CONTROLLER                                             │
│    navController.navigate(Destination.OrderDetail(id).key)    │
└────────────────────────────┬─────────────────────────────────┘
                             │
                             ▼
┌──────────────────────────────────────────────────────────────┐
│ 7. BACK STACK UPDATE                                          │
│    backStack.add("order_detail/123")                         │
│    currentDestination = "order_detail/123"                   │
└────────────────────────────┬─────────────────────────────────┘
                             │
                             ▼
┌──────────────────────────────────────────────────────────────┐
│ 8. RECOMPOSITION                                              │
│    NavGraph recomposes with new destination                  │
│    → OrderDetailScreen is displayed                          │
└──────────────────────────────────────────────────────────────┘
```

## MVI Pattern w Feature

```
┌─────────────────────────────────────────────────────────────┐
│                         ViewModel                            │
│                                                              │
│  ┌──────────────┐         ┌──────────────┐                 │
│  │   Intent     │────────▶│   Reducer    │                 │
│  └──────────────┘         └──────┬───────┘                 │
│                                   │                          │
│                                   ▼                          │
│                            ┌──────────────┐                 │
│                            │    State     │────────┐        │
│                            └──────────────┘        │        │
│                                   │                │        │
│                                   │                │        │
│                                   ▼                ▼        │
│                            ┌──────────────┐  ┌─────────┐   │
│                            │   UseCase    │  │ Effect  │   │
│                            └──────────────┘  └─────────┘   │
└─────────────────────────────────────────────────────────────┘
                                   │                │
                                   ▼                ▼
                            ┌──────────────┐  ┌─────────────┐
                            │ Repository   │  │   Screen    │
                            └──────────────┘  │ (Compose)   │
                                              └─────────────┘
```

## Separacja odpowiedzialności

```
┌────────────────────────────────────────────────────────────┐
│ LIBRARIES (Reusable, framework-agnostic)                   │
│ • navigation - abstrakcja nawigacji                        │
│ • design-system (future)                                   │
│ • network (future)                                         │
└────────────────────────────────────────────────────────────┘
                             ▲
                             │ uses
┌────────────────────────────────────────────────────────────┐
│ CORE (Shared business logic)                               │
│ • core/domain - modele, interfejsy, use cases             │
│ • core/data - implementacje repozytoriów                  │
└────────────────────────────────────────────────────────────┘
                             ▲
                             │ uses
┌────────────────────────────────────────────────────────────┐
│ FEATURES (Business features - independent)                 │
│ • features/order - logika zamówień (MVI)                  │
│ • features/auth (future)                                   │
│ • features/products (future)                               │
└────────────────────────────────────────────────────────────┘
                             ▲
                             │ uses
┌────────────────────────────────────────────────────────────┐
│ COMPOSE (UI layer)                                          │
│ • compose/order - ekrany zamówień                          │
│ • compose/auth (future)                                    │
└────────────────────────────────────────────────────────────┘
                             ▲
                             │ integrates
┌────────────────────────────────────────────────────────────┐
│ APP (Integration & Configuration)                           │
│ • DI setup                                                  │
│ • Navigation setup (NavGraph)                              │
│ • Entry point (MainActivity)                               │
└────────────────────────────────────────────────────────────┘
```

## Zasady zależności

### ✅ Dozwolone zależności:

- `app` → wszystkie moduły
- `compose/*` → `features/*`, `core/*`, `libraries/*`
- `features/*` → `core/*`, `libraries/*`
- `core/data` → `core/domain`
- wszystkie → `libraries/*`

### ❌ Zabronione zależności:

- `libraries/*` ↛ żaden moduł aplikacji
- `core/*` ↛ `features/*`
- `core/*` ↛ `compose/*`
- `features/order` ↛ `features/auth`
- `core/domain` ↛ `core/data`

## Przykład dodania nowego feature'a

```
1. Stwórz features/products/
   └── presentation/
       ├── ProductListContract.kt
       └── ProductListViewModel.kt

2. Stwórz compose/products/
   └── ProductListScreen.kt

3. Dodaj model w core/domain/
   └── model/Product.kt

4. Rozszerz AppModule:
   └── provideProductListViewModel()

5. Dodaj destination w app:
   └── navigation/Destination.kt
       sealed class Destination {
           data object ProductList : Destination("product_list")
       }

6. Zaktualizuj NavGraph:
   └── when (currentRoute) {
           "product_list" -> ProductListScreen(...)
       }
```

Gotowe! Nowy feature nie wpływa na istniejące moduły.
