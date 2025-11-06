package uz.alien.crossword

object Crossword {

    init {
        System.loadLibrary("crossword")
    }

    external fun generateCrossword(
        gridSize: Int,
        maxWords: Int,
        wordList: String
    ): String
}