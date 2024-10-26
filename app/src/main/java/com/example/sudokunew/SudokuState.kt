package com.example.sudokunew

data class SudokuCell(
    val value: Int = 0,
    val isOriginal: Boolean = false,
    val notes: Set<Int> = emptySet(),
    val isSelected: Boolean = false,
    val isValid: Boolean = true
)

data class SudokuState(
    val board: List<List<SudokuCell>> = List(9) { List(9) { SudokuCell() } },
    val selectedCell: Pair<Int, Int>? = null,
    val isNotesMode: Boolean = false,
    val isComplete: Boolean = false
)
