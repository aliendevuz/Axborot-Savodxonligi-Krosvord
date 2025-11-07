package uz.alien.askrosvord

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import uz.alien.askrosvord.ui.theme.AxborotSavodxonligiKrosvordTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setClearEdge()

        val src = assets.open("src.txt").reader().readText()

        setContent {
            AxborotSavodxonligiKrosvordTheme {
                CrosswordScreen(src)
            }
        }
    }
}

fun ComponentActivity.isNight(): Boolean {
    return (
            resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK
            ) == Configuration.UI_MODE_NIGHT_YES
}

fun ComponentActivity.setClearEdge() {
    if (isNight()) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.dark(
                scrim = Color.TRANSPARENT
            )
        )
    } else {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                darkScrim = Color.TRANSPARENT,
                scrim = Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                darkScrim = Color.TRANSPARENT,
                scrim = Color.TRANSPARENT
            )
        )
    }
}