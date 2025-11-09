package com.goldcompany.edicon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.goldcompany.edicon.ui.navigation.EdiconApp
import com.goldcompany.edicon.ui.theme.EdiConTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EdiConTheme {
                EdiconApp()
            }
        }
    }
}