package com.mudita.features.auth.di

import com.mudita.features.auth.data.AuthRepositoryImpl
import com.mudita.features.auth.domain.AuthManager
import com.mudita.features.auth.domain.AuthRepository
import com.mudita.features.auth.presentation.LoginViewModel
import com.mudita.features.auth.presentation.RegisterViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authFeatureModule = module {
    
    // AuthManager - singleton (persists across screens)
    single { AuthManager() }
    
    // Repository
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    
    // ViewModels
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
}
