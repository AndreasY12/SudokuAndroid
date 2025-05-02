package com.example.sudokunew.model

import kotlinx.serialization.Serializable

enum class Difficulty(val cellsToRemove:Int) {
    EASY(30),
    MEDIUM(40),
    HARD(50)
}

@Serializable
data class SudokuCell(
    val value: Int = 0,
    val isOriginal: Boolean = false,
    val notes: Set<Int> = emptySet(),
    val isSelected: Boolean = false,
    val isValid: Boolean = true
)

@Serializable
data class SudokuState(
    val board: List<List<SudokuCell>> = List(9) { List(9) { SudokuCell() } },
    val selectedCell: Pair<Int, Int>? = null,
    val difficulty: Difficulty = Difficulty.EASY,
    val isNotesMode: Boolean = false,
    val isComplete: Boolean = false,
    val timer:Long = 0L,
    val highlightedHintCell: Pair<Int, Int>? = null
)
