package uz.alien.askrosvord

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.alien.askrosvord.ui.theme.AxborotSavodxonligiKrosvordTheme
import uz.alien.crossword.Crossword

class MainActivity : ComponentActivity() {

    var src = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setClearEdge()

        src = assets.open("src.txt").reader().readText()

        setContent {
            AxborotSavodxonligiKrosvordTheme {
                var puzzleOutput by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    puzzleOutput = withContext(Dispatchers.IO) {
                        try {
                            val result = Crossword.generateCrossword(13, 25, src)
                            Log.d("CrosswordGen", "Generated successfully")
                            Log.d("CrosswordGen", result)
                            result
                        } catch (e: Exception) {
                            Log.e("CrosswordGen", "Error: ${e.message}", e)
                            null
                        }
                    }
                }

                when {
                    puzzleOutput != null -> {
                        CrosswordApp(consoleOutput = puzzleOutput!!)
                    }
                    else -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CircularProgressIndicator()
                                Text("Krossvord tayyorlanmoqda...")
                            }
                        }
                    }
                }
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