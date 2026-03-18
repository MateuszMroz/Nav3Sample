package com.mudita.nav3sample.di

import com.mudita.core.domain.constants.AppConfig
import com.mudita.nav3sample.constants.AppConfigImpl
import com.mudita.nav3sample.presentation.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Main app module - app-level dependencies only
 * Domain and feature-specific logic is in their own modules
 */
val appModule = module {
    // App Config
    single<AppConfig> { AppConfigImpl() }
    
    // Main ViewModel
    viewModel { MainViewModel(get()) }
}
