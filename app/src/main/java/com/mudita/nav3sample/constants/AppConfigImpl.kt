package com.mudita.nav3sample.constants

import com.mudita.core.domain.constants.AppConfig

class AppConfigImpl : AppConfig {
    override val appName: String = "Nav3Sample"
    override val apiBaseUrl: String = "https://api.example.com"
    override val debugMode: Boolean = true
}
