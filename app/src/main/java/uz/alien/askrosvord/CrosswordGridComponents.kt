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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    // Dinamik o'lcham hisoblash - faqat harf kataklar uchun
    val cellSize = remember(puzzle.cols) {
        val effectiveCols = (puzzle.cols + 1) / 2 // Juft indexdagi kataklar
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
                // Toq qatorlar - faqat gorizontal devorlar
                Row(
                    modifier = Modifier.height(wallThickness),
                    horizontalArrangement = Arrangement.Center
                ) {
                    row.forEachIndexed { colIndex, cell ->
                        val isOddCol = colIndex % 2 == 1

                        if (isOddCol) {
                            // Vertikal devor joyi
                            Box(
                                modifier = Modifier
                                    .width(wallThickness)
                                    .height(wallThickness)
                                    .background(if (cell.char == '|') Color.Black else Color.White)
                            )
                        } else {
                            // Gorizontal devor
                            Box(
                                modifier = Modifier
                                    .width(cellSize)
                                    .height(wallThickness)
                                    .background(if (cell.char == '|') Color.Black else Color.White)
                            )
                        }
                    }
                }
            } else {
                // Juft qatorlar - harflar va vertikal devorlar
                Row(
                    modifier = Modifier.height(cellSize),
                    horizontalArrangement = Arrangement.Center
                ) {
                    row.forEachIndexed { colIndex, cell ->
                        val isOddCol = colIndex % 2 == 1

                        if (isOddCol) {
                            // Vertikal devor
                            Box(
                                modifier = Modifier
                                    .width(wallThickness)
                                    .height(cellSize)
                                    .background(if (cell.char == '|') Color.Black else Color.White)
                            )
                        } else {
                            // Harf katagi
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
    cellSize: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        cell.isWall -> Color.Black
        isSelected -> Color(0xFF4CAF50)
        isInSelectedWord -> Color(0xFFE8F5E9)
        cellState?.isRevealed == true -> Color(0xFFFFEB3B)
        cellState?.isCorrect == true -> Color(0xFFC8E6C9)
        cellState?.isCorrect == false -> Color(0xFFFFCDD2)
        else -> Color.White
    }

    Box(
        modifier = Modifier
            .size(cellSize)
            .background(backgroundColor)
            .border(0.5.dp, Color.Gray)
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

    // Faqat juft indexdagi kataklar harf saqlaydi
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
            if (rowIndex % 2 == 0) { // Faqat juft qatorlar
                Row(
                    modifier = Modifier.height(cellSize),
                    horizontalArrangement = Arrangement.Center
                ) {
                    row.forEachIndexed { colIndex, cell ->
                        if (colIndex % 2 == 0) { // Faqat juft ustunlar
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