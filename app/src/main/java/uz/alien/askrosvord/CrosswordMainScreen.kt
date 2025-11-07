package uz.alien.askrosvord

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
    src: String,
    viewModel: CrosswordViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val cellStates = viewModel.cellStates

    LaunchedEffect(src) {
        viewModel.initializeIfNeeded(src)
    }

    if (uiState.isCompleted) {
        CompletionDialog(
            onDismiss = {
                viewModel.resetPuzzle()
                viewModel.regeneratePuzzle()
            }
        )
    }

    Scaffold { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(Modifier.height(16.dp))
                            Text("Krossvord yuklanmoqda...")
                        }
                    }
                }

                uiState.error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Error", style = MaterialTheme.typography.displayLarge)
                            Spacer(Modifier.height(8.dp))
                            Text(uiState.error ?: "Noma'lum xatolik", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                uiState.puzzle != null -> {
                    val puzzle = uiState.puzzle!!

                    Column(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                        ) {
                            // Progress panel
                            ControlPanel(
                                correctCells = uiState.correctCells,
                                totalCells = uiState.totalCells,
                                modifier = Modifier.padding(top = paddingValues.calculateTopPadding())
                            )

                            Spacer(Modifier.height(12.dp))

                            // Krossvord grid
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CrosswordGrid(
                                    puzzle = puzzle,
                                    cellStates = cellStates,
                                    selectedCell = uiState.selectedCell,
                                    selectedWord = uiState.selectedWord,
                                    onCellClick = { row, col -> viewModel.selectCell(row, col) }
                                )
                            }

                            Spacer(Modifier.height(16.dp))

                            // Hint ko‘rsatilganda
                            if (uiState.showHints && uiState.selectedWord != null) {
                                HintItem(
                                    word = uiState.selectedWord!!,
                                    isSelected = true,
                                    onClick = {
                                        viewModel.selectCell(
                                            uiState.selectedWord!!.startRow,
                                            uiState.selectedWord!!.startCol
                                        )
                                    },
                                    onReveal = { viewModel.revealWord(uiState.selectedWord!!) }
                                )
                                Spacer(Modifier.height(8.dp))
                            }

                            // UCHTA ASOSIY TUGMA: RESET, TOGGLE, REGENERATE
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 1. RESET
                                Button(
                                    onClick = { viewModel.resetPuzzle() },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary
                                    )
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null)
                                }

                                // 2. TOGGLE DIRECTION
                                Button(
                                    onClick = { viewModel.toggleDirection() },
                                    modifier = Modifier.weight(2f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(
                                        imageVector = if (uiState.isHorizontalMode)
                                            Icons.AutoMirrored.Filled.ArrowForward
                                        else
                                            Icons.Default.ArrowDownward,
                                        contentDescription = null
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(if (uiState.isHorizontalMode) "Gorizontal" else "Vertikal")
                                }

                                // 3. REGENERATE
                                Button(
                                    onClick = { viewModel.regeneratePuzzle() },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary
                                    )
                                ) {
                                    Icon(Icons.Default.Extension, contentDescription = null)
                                }
                            }

                            Spacer(Modifier.height(16.dp))
                        }

                        // Virtual Keyboard
                        var isKeyboardVisible by remember { mutableStateOf(true) }
                        VirtualKeyboard(
                            onKeyPress = { viewModel.inputChar(it) },
                            onDelete = { viewModel.deleteChar() },
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