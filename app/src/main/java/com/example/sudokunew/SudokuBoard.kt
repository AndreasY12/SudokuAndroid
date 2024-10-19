package com.example.sudokunew

class SudokuBoard {
    private val board: Array<Array<Int>> = arrayOf(
        arrayOf(9, 0, 0, 0, 0, 8, 0, 0, 0),
        arrayOf(3, 4, 0, 0, 8, 0, 0, 5, 2),
        arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
        arrayOf(5, 0, 0, 0, 6, 0, 3, 8, 0),
        arrayOf(0, 0, 6, 2, 0, 0, 1, 9, 4),
        arrayOf(0, 7, 0, 1, 0, 0, 0, 0, 9),
        arrayOf(0, 0, 0, 0, 2, 5, 0, 0, 0),
        arrayOf(6, 4, 0, 2, 1, 0, 0, 0, 0),
        arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 9)
    )

    fun getBoard(): Array<Array<Int>> {
        return board
    }
}