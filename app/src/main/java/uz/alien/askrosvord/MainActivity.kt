package uz.alien.askrosvord

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.alien.askrosvord.ui.theme.AxborotSavodxonligiKrosvordTheme
import uz.alien.crossword.Crossword

class MainActivity : ComponentActivity() {

    val src = """Animatsiya|Tasvirlarni ketma-ket ko'rsatish
Antivirus|Viruslarni zararsizlantiruvchi
Motherboard|Barcha qurilmalarni bog'lovchi
AVERAGE|O'rtacha qiymatni hisoblash (Excelda)
Bcc|Ko'rinmas nusxa manzili
Belgi|Harf, raqam yoki simvol
Bluetooth|Qisqa masofali simsiz aloqa
BMP|Windows rasm formati (paint)
Bookmark|Sevimli sahifani saqlash
Brauzer|Internet sahifalarini ko'rsatuvchi dastur
Cloud|Internetdagi masofaviy serverlar
Center|Matnni o'rtaga joylashtirish
Clipboard|Vaqtincha xotiraga nusxa olish
Crop|Tasvirning keraksiz qismini kesish
Cut|Matnni kesib bufer xotiraga olish
Debug|Dasturdagi xatolarni tuzatish
Diapazon|Bir-biriga tutash kataklar guruhi
Display|Tasvir va matnni ko'rsatuvchi ekran
Domen|IP manzilga qulay nom beruvchi yorliq
Ethernet|Simli tarmoq standarti
FAQ|Ko'p beriladigan savollar
Firewall|Tarmoqni himoya qiluvchi dastur
Fayl|Saqlangan ma'lumotlar to'plami
Filtrlash|Kerakli ma'lumotni tanlash
Flip|Tasvirni akslantirish
Footer|Sahifa pastki qismidagi matn
Format|Matn ko'rinishi yoki disk tayyorlash
Formula|Matematik hisoblash ko'rsatmasi
GIS|Geografik axborot tizimi
GUI|Foydalanuvchi grafik interfeysi
Header|Sahifa yuqori qismidagi matn
HTML|Veb sahifa yaratish tili
HTTP|Gipermatn uzatish protokoli
HTTPS|Xavfsiz gipermatn protokoli
Ikonka|Vazifani ifodalovchi belgi
Ilova|Xatga qo'shilgan hujjat
Import|Boshqa dasturdan ko'chirish
Inbox|Kiruvchi xatlar papkasi
Interfeys|Dastur yoki qurilma tashqi ko'rinishi
Internet|Global kompyuter tarmoqlari
Interpretator|Kodni satrmasatr o'qish
Run|Dasturni ishga tushirish
Keyboard|Matn kiritish qurilmasi
Monitor|Tasvirni ko'rsatuvchi ekran
Mouse|Ko'rsatgichni boshqarish qurilmasi
Network|Birbiriga ulangan kompyuterlar
Password|Maxfiy kirish kodi
Printer|Hujjatni qog'ozga chiqaruvchi
Program|Ko'rsatmalar ketmaketligi
RAM|Tezkor xotira
ROM|Faqat o'qish uchun xotira
Scanner|Rasm yoki matnni raqamlashtirish
Server|Ma'lumot saqlovchi markaziy kompyuter
Software|Dasturiy ta'minot
Spreadsheet|Jadval shaklidagi dastur
USB|Universal seriyali ulagich
Virus|Zararli dastur
WiFi|Simsiz internet aloqasi
Windows|Microsoft operatsion tizimi""".trimIndent()

    // Hints matni
    val hintsText = """
=== GORIZONTAL ===
1. Hujjatni qog'ozga chiqaruvchi (7) → [13,5]
5. Internetdagi masofaviy serverlar (5) → [19,15]
7. Saqlangan ma'lumotlar to'plami (4) → [9,17]
9. Bir-biriga tutash kataklar guruhi (8) → [23,5]
10. Tasvirni akslantirish (4) → [11,7]
13. Kerakli ma'lumotni tanlash (9) → [3,7]
16. Foydalanuvchi grafik interfeysi (3) → [1,19]
18. Matn ko'rinishi yoki disk tayyorlash (6) → [9,1]
24. Windows rasm formati (paint) (3) → [25,11]
25. Ko'rinmas nusxa manzili (3) → [7,3]

=== VERTIKAL ===
2. Matematik hisoblash ko'rsatmasi (7) → [9,17]
3. Vazifani ifodalovchi belgi (6) → [13,9]
4. Kiruvchi xatlar papkasi (5) → [11,11]
6. Zararli dastur (5) → [13,21]
8. Veb sahifa yaratish tili (4) → [3,23]
11. Simsiz internet aloqasi (4) → [17,7]
12. Tasvirning keraksiz qismini kesish (4) → [7,5]
14. Gipermatn uzatish protokoli (4) → [1,13]
15. Universal seriyali ulagich (3) → [1,21]
17. Faqat o'qish uchun xotira (3) → [3,15]
19. Matn kiritish qurilmasi (8) → [1,3]
20. Tezkor xotira (3) → [21,13]
21. Microsoft operatsion tizimi (7) → [13,23]
22. Geografik axborot tizimi (3) → [1,9]
23. Sahifa pastki qismidagi matn (6) → [9,1]
    """.trimIndent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AxborotSavodxonligiKrosvordTheme {
                // Loading state bilan boshlash
                var puzzleOutput by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    // Background thread da generate qilish
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

                // UI ko'rsatish
                when {
                    puzzleOutput != null -> {
                        CrosswordApp(consoleOutput = puzzleOutput!!)
                    }
                    else -> {
                        // Loading screen
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

//enum class Screen {
//    MENU, GAME, ABOUT
//}
//
//data class CrosswordData(
//    val grid: List<List<Char>>,
//    val cluesHorizontal: List<Pair<Int, String>>,
//    val cluesVertical: List<Pair<Int, String>>,
//    val rows: Int,
//    val cols: Int,
//    val numberGrid: List<List<Int>> // Raqamlar uchun grid
//)
//
//data class CellState(
//    val char: Char = ' ',
//    val isBlocked: Boolean = false,
//    val userChar: Char = ' ',
//    val isCorrect: Boolean = false,
//    val number: Int = 0 // Katakdagi raqam
//)
//
//// Raqamlarni hisoblash funksiyasi
//fun calculateNumberGrid(
//    grid: List<List<Char>>,
//    cluesHorizontal: List<Pair<Int, String>>,
//    cluesVertical: List<Pair<Int, String>>,
//    rows: Int,
//    cols: Int
//): List<List<Int>> {
//    val numberGrid = MutableList(rows) { MutableList(cols) { 0 } }
//    var currentNumber = 1
//
//    for (row in 0 until rows) {
//        for (col in 0 until cols) {
//            if (grid[row][col] != '#') {
//                var needsNumber = false
//
//                // Gorizontal so'z boshimi?
//                if ((col == 0 || grid[row][col - 1] == '#') &&
//                    col < cols - 1 && grid[row][col + 1] != '#') {
//                    needsNumber = true
//                }
//
//                // Vertikal so'z boshimi?
//                if ((row == 0 || grid[row - 1][col] == '#') &&
//                    row < rows - 1 && grid[row + 1][col] != '#') {
//                    needsNumber = true
//                }
//
//                if (needsNumber) {
//                    numberGrid[row][col] = currentNumber
//                    currentNumber++
//                }
//            }
//        }
//    }
//
//    return numberGrid
//}
//
//fun parseCrosswordResult(result: String): CrosswordData? {
//    try {
//        val lines = result.lines()
//        var gridStartIndex = -1
//        var horizontalStartIndex = -1
//        var verticalStartIndex = -1
//
//        lines.forEachIndexed { index, line ->
//            when {
//                line.contains("=== CROSSWORD ===") -> gridStartIndex = index + 1
//                line.contains("=== GORIZONTAL ===") -> horizontalStartIndex = index + 1
//                line.contains("=== VERTIKAL ===") -> verticalStartIndex = index + 1
//            }
//        }
//
//        if (gridStartIndex == -1 || horizontalStartIndex == -1 || verticalStartIndex == -1) {
//            return null
//        }
//
//        val gridLines = mutableListOf<String>()
//        for (i in gridStartIndex until horizontalStartIndex - 2) {
//            val line = lines[i].trim()
//            if (line.isNotEmpty() && !line.startsWith("===")) {
//                gridLines.add(line)
//            }
//        }
//
//        if (gridLines.isEmpty()) return null
//
//        val rows = gridLines.size
//        val cols = gridLines.maxOf { it.length }
//
//        val grid = gridLines.map { line ->
//            val chars = line.toList()
//            chars + List(cols - chars.size) { '#' }
//        }
//
//        val horizontalClues = mutableListOf<Pair<Int, String>>()
//        for (i in horizontalStartIndex until verticalStartIndex - 2) {
//            val line = lines[i].trim()
//            if (line.isEmpty() || line.startsWith("===")) continue
//
//            val parts = line.split(".", limit = 2)
//            if (parts.size == 2) {
//                val number = parts[0].trim().toIntOrNull() ?: continue
//                val clue = parts[1].trim()
//                horizontalClues.add(number to clue)
//            }
//        }
//
//        val verticalClues = mutableListOf<Pair<Int, String>>()
//        for (i in verticalStartIndex until lines.size) {
//            val line = lines[i].trim()
//            if (line.isEmpty() || line.startsWith("===")) continue
//
//            val parts = line.split(".", limit = 2)
//            if (parts.size == 2) {
//                val number = parts[0].trim().toIntOrNull() ?: continue
//                val clue = parts[1].trim()
//                verticalClues.add(number to clue)
//            }
//        }
//
//        val numberGrid = calculateNumberGrid(grid, horizontalClues, verticalClues, rows, cols)
//
//        return CrosswordData(
//            grid = grid,
//            cluesHorizontal = horizontalClues,
//            cluesVertical = verticalClues,
//            rows = rows,
//            cols = cols,
//            numberGrid = numberGrid
//        )
//    } catch (e: Exception) {
//        Log.e("CrosswordParser", "Parse error", e)
//        return null
//    }
//}
//
//@Composable
//fun CrosswordApp(wordSource: String) {
//    var currentScreen by remember { mutableStateOf(Screen.MENU) }
//    var crosswordData by remember { mutableStateOf<CrosswordData?>(null) }
//
//    Surface(
//        modifier = Modifier.fillMaxSize(),
//        color = MaterialTheme.colorScheme.background
//    ) {
//        when (currentScreen) {
//            Screen.MENU -> MenuScreen(
//                onPlayClick = {
//                    val result = Crossword.generateCrossword(11, 15, wordSource)
//                    Log.d("CrosswordGen", result)
//                    crosswordData = parseCrosswordResult(result)
//                    if (crosswordData != null) {
//                        currentScreen = Screen.GAME
//                    }
//                },
//                onAboutClick = { currentScreen = Screen.ABOUT }
//            )
//            Screen.GAME -> crosswordData?.let {
//                GameScreen(
//                    crosswordData = it,
//                    onBack = { currentScreen = Screen.MENU },
//                    onReplay = {
//                        val result = Crossword.generateCrossword(11, 15, wordSource)
//                        Log.d("CrosswordGen", result)
//                        crosswordData = parseCrosswordResult(result)
//                    }
//                )
//            }
//            Screen.ABOUT -> AboutScreen(onBack = { currentScreen = Screen.MENU })
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MenuScreen(onPlayClick: () -> Unit, onAboutClick: () -> Unit) {
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Axborot Savodxonligi") },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
//                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
//                )
//            )
//        }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//                .padding(32.dp),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(
//                text = "Krossvord O'yini",
//                style = MaterialTheme.typography.headlineLarge,
//                fontWeight = FontWeight.Bold,
//                color = MaterialTheme.colorScheme.primary
//            )
//
//            Spacer(modifier = Modifier.height(48.dp))
//
//            Button(
//                onClick = onPlayClick,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(56.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.primary
//                )
//            ) {
//                Icon(Icons.Default.PlayArrow, contentDescription = null)
//                Spacer(modifier = Modifier.width(8.dp))
//                Text("O'ynash", fontSize = 18.sp)
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            OutlinedButton(
//                onClick = onAboutClick,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(56.dp)
//            ) {
//                Icon(Icons.Default.Info, contentDescription = null)
//                Spacer(modifier = Modifier.width(8.dp))
//                Text("Dastur haqida", fontSize = 18.sp)
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AboutScreen(onBack: () -> Unit) {
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Dastur haqida") },
//                navigationIcon = {
//                    IconButton(onClick = onBack) {
//                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Orqaga")
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
//                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
//                )
//            )
//        }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//                .padding(24.dp)
//        ) {
//            Text(
//                text = "Axborot Savodxonligi Krossvord",
//                style = MaterialTheme.typography.headlineSmall,
//                fontWeight = FontWeight.Bold,
//                color = MaterialTheme.colorScheme.primary
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Text(
//                text = "Versiya: 1.0.0",
//                style = MaterialTheme.typography.bodyLarge
//            )
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            Text(
//                text = "Bu dastur axborot texnologiyalari sohasidagi bilimlarni o'stirish uchun yaratilgan interaktiv krossvord o'yini.",
//                style = MaterialTheme.typography.bodyMedium
//            )
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                colors = CardDefaults.cardColors(
//                    containerColor = MaterialTheme.colorScheme.secondaryContainer
//                )
//            ) {
//                Column(modifier = Modifier.padding(16.dp)) {
//                    Text(
//                        text = "Mualliflar",
//                        style = MaterialTheme.typography.titleMedium,
//                        fontWeight = FontWeight.Bold,
//                        color = MaterialTheme.colorScheme.onSecondaryContainer
//                    )
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Text(
//                        text = "Ishlab chiqildi: Alien Team\nMurojaat: info@alien.uz",
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onSecondaryContainer
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Text(
//                text = "© 2025 Barcha huquqlar himoyalangan",
//                style = MaterialTheme.typography.bodySmall,
//                color = MaterialTheme.colorScheme.onSurfaceVariant
//            )
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun GameScreen(
//    crosswordData: CrosswordData,
//    onBack: () -> Unit,
//    onReplay: () -> Unit
//) {
//    val gridState = remember(crosswordData) {
//        mutableStateOf(
//            List(crosswordData.rows) { row ->
//                List(crosswordData.cols) { col ->
//                    val char = crosswordData.grid[row][col]
//                    CellState(
//                        char = char,
//                        isBlocked = char == '#',
//                        number = crosswordData.numberGrid[row][col]
//                    )
//                }
//            }
//        )
//    }
//
//    var selectedRow by remember { mutableIntStateOf(-1) }
//    var selectedCol by remember { mutableIntStateOf(-1) }
//    var isKeyboardVisible by remember { mutableStateOf(false) }
//    var isWon by remember { mutableStateOf(false) }
//    var elapsedTime by remember { mutableLongStateOf(0L) }
//    var isTimerRunning by remember { mutableStateOf(true) }
//
//    val scope = rememberCoroutineScope()
//
//    LaunchedEffect(isTimerRunning, crosswordData) {
//        elapsedTime = 0L
//        isTimerRunning = true
//        selectedRow = -1
//        selectedCol = -1
//
//        scope.launch {
//            while (isTimerRunning) {
//                delay(1000)
//                elapsedTime++
//            }
//        }
//    }
//
//    fun checkWin() {
//        val allCorrect = gridState.value.flatten().all { cell ->
//            cell.isBlocked || cell.char.uppercaseChar() == cell.userChar.uppercaseChar()
//        }
//        val hasUserInput = gridState.value.flatten().any { !it.isBlocked && it.userChar != ' ' }
//        if (allCorrect && hasUserInput) {
//            isWon = true
//            isTimerRunning = false
//        }
//    }
//
//    fun updateCell(row: Int, col: Int, char: Char) {
//        if (row < 0 || col < 0 || row >= crosswordData.rows || col >= crosswordData.cols) return
//
//        val cell = gridState.value[row][col]
//        if (cell.isBlocked) return
//
//        // Agar to'g'ri javob berilgan bo'lsa, o'zgartirmaslik
//        if (cell.isCorrect && cell.userChar != ' ') return
//
//        val newGrid = gridState.value.map { it.toMutableList() }.toMutableList()
//        newGrid[row][col] = cell.copy(
//            userChar = char.uppercaseChar(),
//            isCorrect = char.uppercaseChar() == cell.char.uppercaseChar()
//        )
//        gridState.value = newGrid
//
//        checkWin()
//    }
//
//    fun deleteCell() {
//        if (selectedRow >= 0 && selectedCol >= 0) {
//            val cell = gridState.value[selectedRow][selectedCol]
//            if (!cell.isBlocked && cell.userChar != ' ') {
//                updateCell(selectedRow, selectedCol, ' ')
//            }
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text("Vaqt: ${elapsedTime / 60}:${String.format("%02d", elapsedTime % 60)}")
//                },
//                navigationIcon = {
//                    IconButton(onClick = onBack) {
//                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Orqaga")
//                    }
//                },
//                actions = {
//                    IconButton(onClick = onReplay) {
//                        Icon(Icons.Default.Refresh, contentDescription = "Qayta o'ynash")
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
//                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
//                )
//            )
//        }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//        ) {
//            if (isWon) {
//                Card(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp),
//                    colors = CardDefaults.cardColors(
//                        containerColor = Color(0xFF4CAF50)
//                    )
//                ) {
//                    Column(
//                        modifier = Modifier.padding(16.dp),
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        Text(
//                            text = "🎉 Tabriklaymiz! 🎉",
//                            style = MaterialTheme.typography.headlineSmall,
//                            color = Color.White,
//                            fontWeight = FontWeight.Bold
//                        )
//                        Text(
//                            text = "Vaqt: ${elapsedTime / 60}:${String.format("%02d", elapsedTime % 60)}",
//                            style = MaterialTheme.typography.bodyLarge,
//                            color = Color.White
//                        )
//                        Spacer(modifier = Modifier.height(8.dp))
//                        Button(
//                            onClick = onReplay,
//                            colors = ButtonDefaults.buttonColors(
//                                containerColor = Color.White,
//                                contentColor = Color(0xFF4CAF50)
//                            )
//                        ) {
//                            Icon(Icons.Default.Refresh, contentDescription = null)
//                            Spacer(modifier = Modifier.width(8.dp))
//                            Text("Qayta o'ynash")
//                        }
//                    }
//                }
//            }
//
//            Box(modifier = Modifier.weight(1f)) {
//                Column {
//                    CrosswordGrid(
//                        gridState = gridState.value,
//                        selectedRow = selectedRow,
//                        selectedCol = selectedCol,
//                        rows = crosswordData.rows,
//                        cols = crosswordData.cols,
//                        onCellClick = { row, col ->
//                            if (!gridState.value[row][col].isBlocked) {
//                                selectedRow = row
//                                selectedCol = col
//                                isKeyboardVisible = true
//                            }
//                        }
//                    )
//
//                    HorizontalDivider(
//                        modifier = Modifier.padding(vertical = 8.dp),
//                        thickness = DividerDefaults.Thickness,
//                        color = DividerDefaults.color
//                    )
//
//                    CluesSection(
//                        horizontalClues = crosswordData.cluesHorizontal,
//                        verticalClues = crosswordData.cluesVertical,
//                        modifier = Modifier.weight(1f)
//                    )
//                }
//            }
//
//            // Keyboard toggle button
//            Button(
//                onClick = { isKeyboardVisible = !isKeyboardVisible },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp, vertical = 8.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = if (isKeyboardVisible)
//                        MaterialTheme.colorScheme.secondary
//                    else
//                        MaterialTheme.colorScheme.primary
//                )
//            ) {
//                Icon(
//                    if (isKeyboardVisible) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
//                    contentDescription = null
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Text(if (isKeyboardVisible) "Klaviaturani yashirish" else "Klaviaturani ko'rsatish")
//            }
//
//            // Custom Keyboard
//            AnimatedVisibility(
//                visible = isKeyboardVisible,
//                enter = slideInVertically(initialOffsetY = { it }),
//                exit = slideOutVertically(targetOffsetY = { it })
//            ) {
//                CustomKeyboard(
//                    onKeyPress = { char ->
//                        if (selectedRow >= 0 && selectedCol >= 0) {
//                            updateCell(selectedRow, selectedCol, char)
//                        }
//                    },
//                    onDelete = { deleteCell() }
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun CustomKeyboard(
//    onKeyPress: (Char) -> Unit,
//    onDelete: () -> Unit
//) {
//    val keyboard = listOf(
//        listOf('A', 'B', 'D', 'E', 'F', 'G', 'H', 'I', 'J'),
//        listOf('K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S'),
//        listOf('T', 'U', 'V', 'X', 'Y', 'Z', "O\'", "G\'", "SH", "CH")
//    )
//
//    Surface(
//        modifier = Modifier.fillMaxWidth(),
//        color = MaterialTheme.colorScheme.surfaceVariant,
//        tonalElevation = 8.dp
//    ) {
//        Column(
//            modifier = Modifier.padding(8.dp)
//        ) {
//            keyboard.forEach { row ->
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(4.dp)
//                ) {
//                    row.forEach { key ->
//                        Button(
//                            onClick = {
//                                when (key) {
//                                    "O\'" -> onKeyPress('Ў')
//                                    "G\'" -> onKeyPress('Ғ')
//                                    else -> onKeyPress(key as Char)
//                                }
//                            },
//                            modifier = Modifier
//                                .weight(1f)
//                                .height(48.dp),
//                            colors = ButtonDefaults.buttonColors(
//                                containerColor = MaterialTheme.colorScheme.primaryContainer
//                            )
//                        ) {
//                            Text(
//                                text = key.toString(),
//                                fontSize = 16.sp,
//                                fontWeight = FontWeight.Bold
//                            )
//                        }
//                    }
//                }
//                Spacer(modifier = Modifier.height(4.dp))
//            }
//
//            // Delete button
//            Button(
//                onClick = onDelete,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(48.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.errorContainer,
//                    contentColor = MaterialTheme.colorScheme.onErrorContainer
//                )
//            ) {
//                Icon(Icons.Default.Clear, contentDescription = "O'chirish")
//                Spacer(modifier = Modifier.width(8.dp))
//                Text("O'chirish", fontSize = 16.sp)
//            }
//        }
//    }
//}
//
//@Composable
//fun CrosswordGrid(
//    gridState: List<List<CellState>>,
//    selectedRow: Int,
//    selectedCol: Int,
//    rows: Int,
//    cols: Int,
//    onCellClick: (Int, Int) -> Unit
//) {
//    BoxWithConstraints(
//        modifier = Modifier
//            .fillMaxWidth()
//            .aspectRatio(cols.toFloat() / rows.toFloat())
//            .padding(8.dp)
//    ) {
//        val cellSize = minOf(maxWidth / cols, maxHeight / rows)
//
//        Column(
//            modifier = Modifier.align(Alignment.Center)
//        ) {
//            gridState.forEachIndexed { rowIndex, row ->
//                Row {
//                    row.forEachIndexed { colIndex, cell ->
//                        Box(
//                            modifier = Modifier
//                                .size(cellSize)
//                                .border(
//                                    width = if (rowIndex == selectedRow && colIndex == selectedCol) 3.dp else 1.dp,
//                                    color = when {
//                                        rowIndex == selectedRow && colIndex == selectedCol -> Color(
//                                            0xFF0080FF
//                                        )
//                                        cell.isCorrect && cell.userChar != ' ' -> Color(0xFF4CAF50)
//                                        else -> Color.Gray
//                                    }
//                                )
//                                .background(
//                                    if (cell.isBlocked) Color.Black else Color.White
//                                )
//                                .clickable { onCellClick(rowIndex, colIndex) }
//                        ) {
//                            if (!cell.isBlocked) {
//                                // Raqamni chap yuqori burchakda ko'rsatish
//                                if (cell.number > 0) {
//                                    Text(
//                                        text = cell.number.toString(),
//                                        fontSize = (cellSize.value * 0.25).sp,
//                                        color = Color.Gray,
//                                        modifier = Modifier
//                                            .align(Alignment.TopStart)
//                                            .padding(2.dp)
//                                    )
//                                }
//
//                                // Foydalanuvchi kiritgan harfni markazda ko'rsatish
//                                if (cell.userChar != ' ') {
//                                    Text(
//                                        text = cell.userChar.toString(),
//                                        fontSize = (cellSize.value * 0.5).sp,
//                                        fontWeight = FontWeight.Bold,
//                                        color = Color.Black,
//                                        textAlign = TextAlign.Center,
//                                        modifier = Modifier.align(Alignment.Center)
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun CluesSection(
//    horizontalClues: List<Pair<Int, String>>,
//    verticalClues: List<Pair<Int, String>>,
//    modifier: Modifier = Modifier
//) {
//    LazyColumn(
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp)
//    ) {
//        item {
//            Text(
//                text = "Gorizontal",
//                style = MaterialTheme.typography.titleMedium,
//                fontWeight = FontWeight.Bold,
//                color = MaterialTheme.colorScheme.primary,
//                modifier = Modifier.padding(vertical = 8.dp)
//            )
//        }
//
//        itemsIndexed(horizontalClues) { _, clue ->
//            Text(
//                text = "${clue.first}. ${clue.second}",
//                style = MaterialTheme.typography.bodyMedium,
//                modifier = Modifier.padding(vertical = 4.dp)
//            )
//        }
//
//        item {
//            Spacer(modifier = Modifier.height(16.dp))
//            Text(
//                text = "Vertikal",
//                style = MaterialTheme.typography.titleMedium,
//                fontWeight = FontWeight.Bold,
//                color = MaterialTheme.colorScheme.primary,
//                modifier = Modifier.padding(vertical = 8.dp)
//            )
//        }
//
//        itemsIndexed(verticalClues) { _, clue ->
//            Text(
//                text = "${clue.first}. ${clue.second}",
//                style = MaterialTheme.typography.bodyMedium,
//                modifier = Modifier.padding(vertical = 4.dp)
//            )
//        }
//
//        item {
//            Spacer(modifier = Modifier.height(16.dp))
//        }
//    }
//}
