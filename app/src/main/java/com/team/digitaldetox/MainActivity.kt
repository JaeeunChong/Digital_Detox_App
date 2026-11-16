
package com.team.digitaldetox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.team.digitaldetox.navigation.AppNavGraph
import com.team.digitaldetox.ui.theme.DigitalDetoxTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DigitalDetoxTheme {
                AppNavGraph()
            }
        }
    }
}
