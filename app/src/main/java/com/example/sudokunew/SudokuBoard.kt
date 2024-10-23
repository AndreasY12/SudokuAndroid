package com.example.sudokunew

enum class Difficulty(val cellsToRemove: Int) {
    EASY(30),     // 30 cells removed
    MEDIUM(40),   // 40 cells removed,
    HARD(50);     // 50 cells removed
}

class SudokuBoard(private val difficulty: Difficulty) {

    private val board: Array<Array<Int>> = Array(9) { Array(9) { 0 } }

    init {
        generateSudoku()
    }

    private fun generateSudoku() {
        fillBoard()
        removeNumbers()
    }

    fun getBoard(): Array<Array<Int>> {
        return board
    }

    private fun isValid(row: Int, col: Int, number: Int): Boolean {
        for (i in 0 until 9) {
            if (board[row][i] == number || board[i][col] == number) {
                return false
            }
        }

        val boxRowStart = row - row % 3
        val boxColStart = col - col % 3
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                if (board[boxRowStart + i][boxColStart + j] == number) {
                    return false
                }
            }
        }
        return true
    }

    private fun fillBoard(): Boolean {
        for (row in 0 until 9) {
            for (col in 0 until 9) {
                if (board[row][col] == 0) {
                    val numbers = (1..9).shuffled()
                    for (number in numbers) {
                        if (isValid(row, col, number)) {
                            board[row][col] = number
                            if (fillBoard()) {
                                return true
                            }
                            board[row][col] = 0 // Backtrack
                        }
                    }
                    return false // No valid number found
                }
            }
        }
        return true // Board is filled
    }

    private fun removeNumbers() {
        val cellsToRemove  = difficulty.cellsToRemove
        var count = 0
        while (count < cellsToRemove) {
            val row = (0 until 9).random()
            val col = (0 until 9).random()
            if (board[row][col] != 0) {
                board[row][col] = 0
                count++
            }
        }
    }


}