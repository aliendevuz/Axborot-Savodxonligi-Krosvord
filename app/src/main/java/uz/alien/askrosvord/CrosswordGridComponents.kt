package uz.alien.askrosvord

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.alien.askrosvord.ui.theme.CrosswordColors

@Composable
fun CrosswordGrid(
    puzzle: CrosswordPuzzle,
    cellStates: Map<Pair<Int, Int>, CellState>,
    selectedCell: Pair<Int, Int>?,
    selectedWord: CrosswordWord?,
    onCellClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val cellSize = remember(puzzle.cols) {
        val effectiveCols = (puzzle.cols + 1) / 2
        (300f / effectiveCols.coerceAtLeast(1)).coerceIn(24f, 48f).dp
    }

    val wallThickness = 2.5.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        puzzle.grid.forEachIndexed { rowIndex, row ->
            val isOddRow = rowIndex % 2 == 1

            if (isOddRow) {
                Row(
                    modifier = Modifier.height(wallThickness),
                    horizontalArrangement = Arrangement.Center
                ) {
                    row.forEachIndexed { colIndex, cell ->
                        val isOddCol = colIndex % 2 == 1
                        val isDark = isSystemInDarkTheme()

                        if (isOddCol) {
                            Box(
                                modifier = Modifier
                                    .width(wallThickness)
                                    .height(wallThickness)
                                    .background(
                                        if (cell.char == '|') {
                                            if (isDark) CrosswordColors.DarkWall else CrosswordColors.LightWall
                                        } else Color.Transparent
                                    )
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .width(cellSize)
                                    .height(wallThickness)
                                    .background(
                                        if (cell.char == '|') {
                                            if (isDark) CrosswordColors.DarkWall else CrosswordColors.LightWall
                                        } else Color.Transparent
                                    )
                            )
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.height(cellSize),
                    horizontalArrangement = Arrangement.Center
                ) {
                    row.forEachIndexed { colIndex, cell ->
                        val isOddCol = colIndex % 2 == 1
                        val isDark = isSystemInDarkTheme()

                        if (isOddCol) {
                            Box(
                                modifier = Modifier
                                    .width(wallThickness)
                                    .height(cellSize)
                                    .background(
                                        if (cell.char == '|') {
                                            if (isDark) CrosswordColors.DarkWall else CrosswordColors.LightWall
                                        } else Color.Transparent
                                    )
                            )
                        } else {
                            CrosswordCell(
                                cell = cell,
                                cellState = cellStates[Pair(rowIndex, colIndex)],
                                isSelected = selectedCell == Pair(rowIndex, colIndex),
                                isInSelectedWord = isInWord(rowIndex, colIndex, selectedWord),
                                cellSize = cellSize,
                                onClick = { onCellClick(rowIndex, colIndex) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CrosswordCell(
    cell: CrosswordCell,
    cellState: CellState?,
    isSelected: Boolean,
    isInSelectedWord: Boolean,
    cellSize: Dp,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()

    val backgroundBrush = when {
        cell.isWall -> Brush.linearGradient(
            colors = if (isDark) {
                listOf(CrosswordColors.DarkWallGradientStart, CrosswordColors.DarkWallGradientEnd)
            } else {
                listOf(CrosswordColors.LightWallGradientStart, CrosswordColors.LightWallGradientEnd)
            },
            start = Offset.Zero,
            end = Offset.Infinite
        )
        isSelected -> SolidColor(if (isDark) CrosswordColors.DarkSelectedCell else CrosswordColors.LightSelectedCell)
        isInSelectedWord -> SolidColor(if (isDark) CrosswordColors.DarkSelectedWordCell else CrosswordColors.LightSelectedWordCell)
        cellState?.isRevealed == true -> SolidColor(if (isDark) CrosswordColors.DarkRevealedCell else CrosswordColors.LightRevealedCell)
        cellState?.isCorrect == true -> SolidColor(if (isDark) CrosswordColors.DarkCorrectCell else CrosswordColors.LightCorrectCell)
        cellState?.isCorrect == false -> SolidColor(if (isDark) CrosswordColors.DarkIncorrectCell else CrosswordColors.LightIncorrectCell)
        else -> SolidColor(MaterialTheme.colorScheme.surface)
    }

    val borderColor = if (isDark) CrosswordColors.DarkCellBorder else CrosswordColors.LightCellBorder
    val textColor = when {
        isSelected -> Color.White
        isDark -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = Modifier
            .size(cellSize)
            .background(brush = backgroundBrush, shape = RoundedCornerShape(4.dp))
            .border(1.dp, borderColor, RoundedCornerShape(4.dp))
            .clickable(enabled = !cell.isWall) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (!cell.isWall) {
            val displayChar = cellState?.userInput ?: ""
            Text(
                text = displayChar.toString(),
                fontSize = (cellSize.value * 0.5f).sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun isInWord(row: Int, col: Int, word: CrosswordWord?): Boolean {
    if (word == null) return false
    if (row % 2 == 1 || col % 2 == 1) return false

    return if (word.isHorizontal) {
        row == word.startRow && col in word.startCol until (word.startCol + word.word.length * 2) step 2
    } else {
        col == word.startCol && row in word.startRow until (word.startRow + word.word.length * 2) step 2
    }
}

@Composable
fun WordNumberIndicator(
    puzzle: CrosswordPuzzle,
    cellSize: Dp
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        puzzle.grid.forEachIndexed { rowIndex, row ->
            if (rowIndex % 2 == 0) {
                Row(
                    modifier = Modifier.height(cellSize),
                    horizontalArrangement = Arrangement.Center
                ) {
                    row.forEachIndexed { colIndex, cell ->
                        if (colIndex % 2 == 0) {
                            Box(
                                modifier = Modifier
                                    .size(cellSize)
                                    .border(0.5.dp, Color.Transparent),
                                contentAlignment = Alignment.TopStart
                            ) {
                                if (!cell.isWall) {
                                    val wordNumber = findWordNumber(puzzle, rowIndex, colIndex)
                                    if (wordNumber != null) {
                                        Text(
                                            text = wordNumber.toString(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun findWordNumber(
    puzzle: CrosswordPuzzle,
    row: Int,
    col: Int
): Int? {
    return puzzle.words.find { word ->
        word.startRow == row && word.startCol == col
    }?.number
}