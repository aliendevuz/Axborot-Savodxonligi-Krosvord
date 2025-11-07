package uz.alien.askrosvord

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import uz.alien.crossword.Crossword

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

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .padding(32.dp)
                                .shadow(12.dp, RoundedCornerShape(24.dp)),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(32.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(56.dp),
                                    strokeWidth = 5.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.height(20.dp))
                                Text(
                                    "Krossvord yuklanmoqda...",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .padding(32.dp)
                                .shadow(12.dp, RoundedCornerShape(24.dp)),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Text(
                                    "Xatolik",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    uiState.error ?: "Noma'lum xatolik",
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }

                uiState.puzzle != null -> {
                    val puzzle = uiState.puzzle!!
                    val scrollState = rememberScrollState()

                    Column(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(scrollState)
                        ) {
                            Spacer(Modifier.height(paddingValues.calculateTopPadding() + 12.dp))

                            // Progress panel
                            ControlPanel(
                                isHorizontalMode = uiState.isHorizontalMode,
                                showHints = uiState.showHints,
                                correctCells = uiState.correctCells,
                                totalCells = uiState.totalCells,
                                onToggleDirection = { },
                                onToggleHints = { viewModel.toggleHints() },
                                onReset = { }
                            )

                            Spacer(Modifier.height(16.dp))

                            // Krossvord grid
                            CrosswordGrid(
                                puzzle = puzzle,
                                cellStates = cellStates,
                                selectedCell = uiState.selectedCell,
                                selectedWord = uiState.selectedWord,
                                onCellClick = { row, col -> viewModel.selectCell(row, col) }
                            )

                            Spacer(Modifier.height(16.dp))

                            // Hint
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
                                Spacer(Modifier.height(12.dp))
                            }

                            // Control buttons
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Reset
                                Button(
                                    onClick = { viewModel.resetPuzzle() },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp)
                                        .shadow(6.dp, RoundedCornerShape(14.dp)),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer,
                                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                                    ),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Reset",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }

                                // Toggle direction
                                Button(
                                    onClick = { viewModel.toggleDirection() },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp)
                                        .shadow(6.dp, RoundedCornerShape(14.dp)),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (uiState.isHorizontalMode)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.tertiary,
                                        contentColor = if (uiState.isHorizontalMode)
                                            MaterialTheme.colorScheme.onPrimary
                                        else
                                            MaterialTheme.colorScheme.onTertiary
                                    ),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Icon(
                                        imageVector = if (uiState.isHorizontalMode)
                                            Icons.AutoMirrored.Filled.ArrowForward
                                        else
                                            Icons.Default.ArrowDownward,
                                        contentDescription = null,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                // Regenerate
                                Button(
                                    onClick = { viewModel.regeneratePuzzle() },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp)
                                        .shadow(6.dp, RoundedCornerShape(14.dp)),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    ),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Yangi",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                            }

                            Spacer(Modifier.height(20.dp))
                        }

                        // Virtual Keyboard
                        var isKeyboardVisible by remember { mutableStateOf(true) }
                        VirtualKeyboard(
                            onKeyPress = { viewModel.inputChar(it) },
                            onDelete = { viewModel.deleteChar() },
                            onToggleVisibility = { isKeyboardVisible = !isKeyboardVisible },
                            isVisible = isKeyboardVisible,
                            modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CompletionDialog(onDismiss: () -> Unit) {
    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 300),
        label = "DialogAlpha"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(72.dp)
                    .alpha(alpha)
            )
        },
        title = {
            Text(
                "Tabriklaymiz! 🎉",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                "Siz krossvordni muvaffaqiyatli yechdingiz!",
                style = MaterialTheme.typography.bodyLarge
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(6.dp, RoundedCornerShape(14.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    "Yangi o'yin",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp)
    )
}