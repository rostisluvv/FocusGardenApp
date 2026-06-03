package com.example.focusgarden

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.focusgarden.data.datastore.AppSettings
import com.example.focusgarden.navigation.FocusGardenNavHost
import com.example.focusgarden.ui.theme.FocusGardenTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appContainer = (application as FocusGardenApplication).container
        setContent {
            val settings by appContainer.settingsRepository.settings.collectAsState(initial = AppSettings())
            FocusGardenTheme(themeMode = settings.themeMode) {
                FocusGardenNavHost(appContainer = appContainer)
            }
        }
    }
}
