package com.mudita.nav3sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.mudita.nav3sample.navigation.NavRoot
import com.mudita.nav3sample.ui.theme.Nav3SampleTheme

/**
 * MainActivity - entry point
 * 
 * Navigator is created in NavRoot using rememberNavigator()
 * ViewModels use NavActionsEmitter pattern (no Navigator dependency)
 */
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Nav3SampleTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NavRoot(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}
