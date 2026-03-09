package com.mudita.features.order.di

import com.mudita.core.domain.usecase.GetOrderByIdUseCase
import com.mudita.core.domain.usecase.GetOrdersUseCase
import com.mudita.features.order.presentation.OrderDetailViewModel
import com.mudita.features.order.presentation.OrderListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * DI module for Order feature
 * Contains Use Cases and ViewModels for this feature
 * 
 * ViewModels use delegation pattern (by NavActionsEmitter()) so they don't need
 * NavActionsEmitter injected - they create their own instance
 */
val orderFeatureModule = module {
    
    // Use Cases - specific to Order feature
    factory { GetOrdersUseCase(get()) }
    factory { GetOrderByIdUseCase(get()) }
    
    // ViewModels - OrderListViewModel uses AuthRepository for logout
    viewModel { OrderListViewModel(get(), get()) }
    viewModel { OrderDetailViewModel(get()) }
}
