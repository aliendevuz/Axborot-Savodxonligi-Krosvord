package uz.alien.askrosvord

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
        (300f / effectiveCols.coerceAtLeast(1)).coerceIn(20f, 40f).dp
    }

    val wallThickness = 2.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
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

                        if (isOddCol) {
                            Box(
                                modifier = Modifier
                                    .width(wallThickness)
                                    .height(wallThickness)
                                    .background(if (cell.char == '|') Color(0x80909AAB) else Color(0x00FFFFFF))
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .width(cellSize)
                                    .height(wallThickness)
                                    .background(if (cell.char == '|') Color(0x80909AAB) else Color(0x00FFFFFF))
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

                        if (isOddCol) {
                            Box(
                                modifier = Modifier
                                    .width(wallThickness)
                                    .height(cellSize)
                                    .background(if (cell.char == '|') Color(0x80909AAB) else Color(0x00FFFFFF))
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
    val backgroundBrush = when {
        cell.isWall -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF334155),
                Color(0xFF1E293B)
            ),
            start = Offset.Zero,
            end = Offset.Infinite
        )
        isSelected -> SolidColor(Color(0xFF4CAF50))
        isInSelectedWord -> SolidColor(Color(0xFFE8F5E9))
        cellState?.isRevealed == true -> SolidColor(Color(0xFFFFEB3B))
        cellState?.isCorrect == true -> SolidColor(Color(0xFFC8E6C9))
        cellState?.isCorrect == false -> SolidColor(Color(0xFFFFCDD2))
        else -> SolidColor(Color(0x00FFFFFF))
    }

    Box(
        modifier = Modifier
            .size(cellSize)
            .background(brush = backgroundBrush)
            .border(0.5.dp, Color(0x80BAC3D3))
            .clickable(enabled = !cell.isWall) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (!cell.isWall) {
            val displayChar = cellState?.userInput ?: ""
            Text(
                text = displayChar.toString(),
                fontSize = (cellSize.value * 0.5f).sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color.Black,
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
    cellSize: androidx.compose.ui.unit.Dp
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
                                            fontSize = 8.sp,
                                            color = Color.Gray,
                                            modifier = Modifier.padding(1.dp)
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