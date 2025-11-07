package uz.alien.askrosvord

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class CrosswordUiState(
    val puzzle: CrosswordPuzzle? = null,
    val selectedCell: Pair<Int, Int>? = null,
    val selectedWord: CrosswordWord? = null,
    val isHorizontalMode: Boolean = true,
    val showHints: Boolean = true,
    val isCompleted: Boolean = false,
    val correctCells: Int = 0,
    val totalCells: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

class CrosswordViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CrosswordUiState(isLoading = true))
    val uiState: StateFlow<CrosswordUiState> = _uiState.asStateFlow()

    private val _cellStates: SnapshotStateMap<Pair<Int, Int>, CellState> = mutableStateMapOf()
    val cellStates: Map<Pair<Int, Int>, CellState> = _cellStates

    fun initializePuzzle(consoleOutput: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val puzzle = withContext(Dispatchers.IO) {
                    val parsedPuzzle = CrosswordParser.parseConsoleOutput(consoleOutput)
                    parsedPuzzle?.let { CrosswordParser.fillWordsFromGrid(it) }
                }

                if (puzzle == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Krossvord yuklanmadi"
                    )
                    return@launch
                }

                _uiState.value = _uiState.value.copy(
                    puzzle = puzzle,
                    totalCells = countNonWallCells(puzzle),
                    isLoading = false
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Xatolik: ${e.message}"
                )
            }
        }
    }

    private fun countNonWallCells(puzzle: CrosswordPuzzle): Int {
        return puzzle.words.size
    }

    fun selectCell(row: Int, col: Int) {
        val puzzle = _uiState.value.puzzle ?: return

        if (row % 2 == 1 || col % 2 == 1) return

        if (row < 0 || row >= puzzle.rows) return
        if (col < 0 || col >= puzzle.grid[row].size) return

        val cell = puzzle.grid[row][col]
        if (cell.isWall) return

        val word = findWordAtPosition(row, col, _uiState.value.isHorizontalMode)

        _uiState.value = _uiState.value.copy(
            selectedCell = Pair(row, col),
            selectedWord = word
        )
    }

    fun toggleDirection() {
        val current = _uiState.value.selectedCell ?: return
        val newMode = !_uiState.value.isHorizontalMode

        val word = findWordAtPosition(current.first, current.second, newMode)

        _uiState.value = _uiState.value.copy(
            isHorizontalMode = newMode,
            selectedWord = word
        )
    }

    fun inputChar(char: Char) {
        val selected = _uiState.value.selectedCell ?: return
        val puzzle = _uiState.value.puzzle ?: return

        if (selected.first >= puzzle.rows) return
        if (selected.second >= puzzle.grid[selected.first].size) return

        val cell = puzzle.grid[selected.first][selected.second]

        _cellStates[selected] = CellState(
            userInput = char.uppercaseChar(),
            isCorrect = char.uppercaseChar() == cell.char.uppercaseChar()
        )

        moveToNextCell()
        updateCorrectCount()
        checkCompletion()
    }

    fun deleteChar() {
        val selected = _uiState.value.selectedCell ?: return
        _cellStates.remove(selected)
        updateCorrectCount()
    }

    private fun moveToNextCell() {
        val current = _uiState.value.selectedCell ?: return
        val puzzle = _uiState.value.puzzle ?: return
        val isHorizontal = _uiState.value.isHorizontalMode

        var nextRow = current.first
        var nextCol = current.second

        var attempts = 0
        val maxAttempts = puzzle.rows * puzzle.cols

        while (attempts < maxAttempts) {
            if (isHorizontal) {
                nextCol += 2
                if (nextCol >= puzzle.cols) return
            } else {
                nextRow += 2
                if (nextRow >= puzzle.rows) return
            }

            if (nextRow >= puzzle.rows || nextCol >= puzzle.grid[nextRow].size) return

            val nextCell = puzzle.grid[nextRow][nextCol]

            if (!nextCell.isWall && nextCell.char != ' ') {
                selectCell(nextRow, nextCol)
                return
            }

            attempts++
        }
    }

    private fun findWordAtPosition(row: Int, col: Int, isHorizontal: Boolean): CrosswordWord? {
        val puzzle = _uiState.value.puzzle ?: return null

        return puzzle.words.find { word ->
            if (word.isHorizontal != isHorizontal) return@find false

            if (isHorizontal) {
                row == word.startRow && col >= word.startCol && col < word.startCol + (word.word.length * 2) && (col - word.startCol) % 2 == 0
            } else {
                col == word.startCol && row >= word.startRow && row < word.startRow + (word.word.length * 2) && (row - word.startRow) % 2 == 0
            }
        }
    }

    fun revealWord(word: CrosswordWord) {
        word.word.forEachIndexed { index, char ->
            val row = if (word.isHorizontal) word.startRow else word.startRow + (index * 2)
            val col = if (word.isHorizontal) word.startCol + (index * 2) else word.startCol

            _cellStates[Pair(row, col)] = CellState(
                userInput = char,
                isRevealed = true,
                isCorrect = true
            )
        }

        updateCorrectCount()
        checkCompletion()
    }

    private fun updateCorrectCount() {
        val puzzle = _uiState.value.puzzle ?: return
        val completedWords = puzzle.words.count { word ->
            isWordComplete(word)
        }
        _uiState.value = _uiState.value.copy(correctCells = completedWords)
    }

    private fun isWordComplete(word: CrosswordWord): Boolean {
        return word.word.indices.all { index ->
            val row = if (word.isHorizontal) word.startRow else word.startRow + (index * 2)
            val col = if (word.isHorizontal) word.startCol + (index * 2) else word.startCol
            val cellState = _cellStates[Pair(row, col)]
            cellState?.isCorrect == true
        }
    }

    private fun checkCompletion() {
        val total = _uiState.value.totalCells
        val correct = _uiState.value.correctCells

        if (correct == total && total > 0) {
            _uiState.value = _uiState.value.copy(isCompleted = true)
        }
    }

    fun toggleHints() {
        _uiState.value = _uiState.value.copy(
            showHints = !_uiState.value.showHints
        )
    }

    fun resetPuzzle() {
        _cellStates.clear()
        _uiState.value = _uiState.value.copy(
            selectedCell = null,
            selectedWord = null,
            isCompleted = false,
            correctCells = 0
        )
    }

    fun revealRandomLetter(word: CrosswordWord) {
        val unrevealedIndices = word.word.indices.filter { index ->
            val row = if (word.isHorizontal) word.startRow else word.startRow + (index * 2)
            val col = if (word.isHorizontal) word.startCol + (index * 2) else word.startCol
            val cellState = _cellStates[Pair(row, col)]
            cellState?.isCorrect != true
        }

        if (unrevealedIndices.isNotEmpty()) {
            val randomIndex = unrevealedIndices.random()
            val row = if (word.isHorizontal) word.startRow else word.startRow + (randomIndex * 2)
            val col = if (word.isHorizontal) word.startCol + (randomIndex * 2) else word.startCol
            
            _cellStates[Pair(row, col)] = CellState(
                userInput = word.word[randomIndex],
                isRevealed = true,
                isCorrect = true
            )
        }

        updateCorrectCount()
        checkCompletion()
    }
}