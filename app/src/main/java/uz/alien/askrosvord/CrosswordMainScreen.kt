package uz.alien.askrosvord

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrosswordScreen(
    consoleOutput: String,
    viewModel: CrosswordViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val cellStates = viewModel.cellStates

    LaunchedEffect(consoleOutput) {
        viewModel.initializePuzzle(consoleOutput)
    }

    if (uiState.isCompleted) {
        CompletionDialog(
            onDismiss = { viewModel.resetPuzzle() }
        )
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator()
                            Text("Krossvord yuklanmoqda...")
                        }
                    }
                }
                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(
                                text = "❌",
                                style = MaterialTheme.typography.displayLarge
                            )
                            Text(
                                text = uiState.error ?: "Noma'lum xatolik",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                uiState.puzzle != null -> {
                    val puzzle = uiState.puzzle!!

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                        ) {
                            ControlPanel(
                                isHorizontalMode = uiState.isHorizontalMode,
                                showHints = uiState.showHints,
                                correctCells = uiState.correctCells,
                                totalCells = uiState.totalCells,
                                onToggleDirection = { viewModel.toggleDirection() },
                                onToggleHints = { viewModel.toggleHints() },
                                onReset = { viewModel.resetPuzzle() },
                                modifier = Modifier.padding(top = paddingValues.calculateTopPadding())
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CrosswordGrid(
                                    puzzle = puzzle,
                                    cellStates = cellStates,
                                    selectedCell = uiState.selectedCell,
                                    selectedWord = uiState.selectedWord,
                                    onCellClick = { row, col ->
                                        viewModel.selectCell(row, col)
                                    }
                                )
                            }

                            if (uiState.showHints) {
                                HintsPanel(
                                    words = puzzle.words,
                                    selectedWord = uiState.selectedWord,
                                    showHints = true,
                                    onWordClick = { word ->
                                        viewModel.selectCell(word.startRow, word.startCol)
                                    },
                                    onRevealWord = { word ->
                                        viewModel.revealWord(word)
                                    }
                                )
                            }
                        }

                        var isKeyboardVisible by remember { mutableStateOf(true) }

                        VirtualKeyboard(
                            onKeyPress = { char ->
                                viewModel.inputChar(char)
                            },
                            onDelete = {
                                viewModel.deleteChar()
                            },
                            onToggleVisibility = { isKeyboardVisible = !isKeyboardVisible },
                            isVisible = isKeyboardVisible,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(bottom = paddingValues.calculateBottomPadding())
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CompletionDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )
        },
        title = {
            Text("Tabriklaymiz! 🎉")
        },
        text = {
            Text("Siz krossvordni muvaffaqiyatli yechdingiz!")
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Qaytadan boshlash")
            }
        }
    )
}

@Composable
fun CrosswordApp(consoleOutput: String) {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            CrosswordScreen(consoleOutput = consoleOutput)
        }
    }
}