package uz.alien.askrosvord


data class CrosswordCell(
    val row: Int,
    val col: Int,
    val char: Char,
    val isWall: Boolean = false
)

data class CrosswordWord(
    val word: String,
    val hint: String,
    val startRow: Int,
    val startCol: Int,
    val isHorizontal: Boolean,
    val number: Int
)

data class CrosswordPuzzle(
    val grid: List<List<CrosswordCell>>,
    val words: List<CrosswordWord>,
    val rows: Int,
    val cols: Int
)

data class CellState(
    val userInput: Char? = null,
    val isRevealed: Boolean = false,
    val isCorrect: Boolean? = null
)

object CrosswordParser {

    fun parseConsoleOutput(output: String): CrosswordPuzzle? {
        try {
            val sections = output.split("===")
            if (sections.size < 3) return null

            val gridSection = sections[2].substringAfter("CROSSWORD").trim()
            val gridLines = gridSection.lines().filter { it.isNotEmpty() }
            if (gridLines.isEmpty()) return null

            val horizontalSection = sections[4].trim()
            val verticalSection = sections[6].trim()

            val grid = parseGrid(gridLines)
            if (grid.isEmpty()) return null

            val words = parseHints(horizontalSection, verticalSection)

            return CrosswordPuzzle(
                grid = grid,
                words = words,
                rows = grid.size,
                cols = grid.maxOfOrNull { it.size } ?: 0
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun parseGrid(lines: List<String>): List<List<CrosswordCell>> {
        val grid = mutableListOf<List<CrosswordCell>>()

        lines.forEachIndexed { rowIndex, line ->
            val row = mutableListOf<CrosswordCell>()

            line.forEachIndexed { colIndex, char ->
                val isOddRow = rowIndex % 2 == 1
                val isOddCol = colIndex % 2 == 1

                when {
                    char == '|' -> {
                        row.add(CrosswordCell(rowIndex, colIndex, '|', isWall = true))
                    }
                    char == ' ' -> {
                        if (isOddRow || isOddCol) {
                            row.add(CrosswordCell(rowIndex, colIndex, ' ', isWall = false))
                        } else {
                            row.add(CrosswordCell(rowIndex, colIndex, ' ', isWall = false))
                        }
                    }
                    else -> {
                        if (!isOddRow && !isOddCol) {
                            row.add(CrosswordCell(rowIndex, colIndex, char, isWall = false))
                        } else {
                            row.add(CrosswordCell(rowIndex, colIndex, ' ', isWall = false))
                        }
                    }
                }
            }

            if (row.isNotEmpty()) {
                grid.add(row)
            }
        }

        return grid
    }

    private fun parseHints(horizontalSection: String, verticalSection: String): List<CrosswordWord> {
        val words = mutableListOf<CrosswordWord>()

        parseHintSection(horizontalSection, true, words)
        parseHintSection(verticalSection, false, words)

        return words
    }

    private fun parseHintSection(
        section: String,
        isHorizontal: Boolean,
        words: MutableList<CrosswordWord>
    ) {
        val lines = section.lines()

        for (line in lines) {
            if (line.contains("→") && line.matches(Regex("^\\d+\\..*"))) {
                try {
                    val parts = line.split("→")
                    if (parts.size != 2) continue

                    val leftPart = parts[0].trim()
                    val coordPart = parts[1].trim()

                    val numberMatch = Regex("^(\\d+)\\.\\s*(.*)\\s*\\((\\d+)\\)").find(leftPart)
                    if (numberMatch == null) continue

                    val number = numberMatch.groupValues[1].toIntOrNull() ?: continue
                    val hint = numberMatch.groupValues[2].trim()
                    val length = numberMatch.groupValues[3].toIntOrNull() ?: continue

                    val coordMatch = Regex("\\[(\\d+),(\\d+)]").find(coordPart)
                    if (coordMatch == null) continue

                    val row = coordMatch.groupValues[1].toIntOrNull() ?: continue
                    val col = coordMatch.groupValues[2].toIntOrNull() ?: continue

                    words.add(
                        CrosswordWord(
                            word = "X".repeat(length),
                            hint = hint,
                            startRow = row - 1,
                            startCol = col - 1,
                            isHorizontal = isHorizontal,
                            number = number
                        )
                    )
                } catch (_: Exception) {
                    continue
                }
            }
        }
    }

    fun fillWordsFromGrid(puzzle: CrosswordPuzzle): CrosswordPuzzle {
        val updatedWords = puzzle.words.map { word ->
            val actualWord = extractWordFromGrid(
                puzzle.grid,
                word.startRow,
                word.startCol,
                word.isHorizontal,
                word.word.length
            )
            word.copy(word = actualWord)
        }

        return puzzle.copy(words = updatedWords)
    }

    private fun extractWordFromGrid(
        grid: List<List<CrosswordCell>>,
        startRow: Int,
        startCol: Int,
        isHorizontal: Boolean,
        length: Int
    ): String {
        val word = StringBuilder()

        for (i in 0 until length) {
            val row = if (isHorizontal) startRow else startRow + (i * 2)
            val col = if (isHorizontal) startCol + (i * 2) else startCol

            if (row < grid.size && col < grid[row].size) {
                val cell = grid[row][col]
                if (!cell.isWall && cell.char != ' ') {
                    word.append(cell.char)
                } else {
                    break
                }
            } else {
                break
            }
        }

        return word.toString()
    }
}