package com.mudita.core.data.di

import com.mudita.core.data.repository.OrderRepositoryImpl
import com.mudita.core.domain.repository.OrderRepository
import org.koin.dsl.module

/**
 * DI module for Data layer
 * Contains repositories implementations
 */
val dataModule = module {
    
    // Repositories
    single<OrderRepository> { OrderRepositoryImpl() }
}
