package com.example.sudokunew

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SudokuViewModel : ViewModel() {
    private val _state = MutableStateFlow(SudokuState())
    val state: StateFlow<SudokuState> = _state.asStateFlow()

    private var originalBoard: Array<Array<Int>>? = null

    init {
        startNewGame(Difficulty.MEDIUM)
    }

    fun startNewGame(difficulty: Difficulty) {
        val sudokuBoard = SudokuBoard(difficulty)
        originalBoard = sudokuBoard.getBoard()

        val initialBoard = List(9) { row ->
            List(9) { col ->
                SudokuCell(
                    value = originalBoard!![row][col],
                    isOriginal = originalBoard!![row][col] != 0
                )
            }
        }

        _state.value = SudokuState(board = initialBoard)
    }

    fun onCellSelected(row: Int, col: Int) {
        _state.update { currentState ->
            currentState.copy(
                selectedCell = row to col,
                board = currentState.board.mapIndexed { r, rowCells ->
                    rowCells.mapIndexed { c, cell ->
                        cell.copy(isSelected = r == row && c == col)
                    }
                }
            )
        }
    }

    fun onNumberInput(number: Int) {
        val (row, col) = _state.value.selectedCell ?: return

        _state.update { currentState ->
            if (currentState.isNotesMode) {
                handleNoteInput(currentState, row, col, number)
            } else {
                handleNumberInput(currentState, row, col, number)
            }
        }

        checkCompletion()
    }

    private fun handleNoteInput(
        currentState: SudokuState,
        row: Int,
        col: Int,
        number: Int
    ): SudokuState {
        val currentCell = currentState.board[row][col]
        if (currentCell.isOriginal) return currentState

        val updatedNotes = if (currentCell.notes.contains(number)) {
            currentCell.notes - number
        } else {
            currentCell.notes + number
        }

        return currentState.copy(
            board = currentState.board.mapIndexed { r, rowCells ->
                rowCells.mapIndexed { c, cell ->
                    if (r == row && c == col) {
                        cell.copy(notes = updatedNotes, value = 0)
                    } else cell
                }
            }
        )
    }

    private fun handleNumberInput(
        currentState: SudokuState,
        row: Int,
        col: Int,
        number: Int
    ): SudokuState {
        if (currentState.board[row][col].isOriginal) return currentState

        val isValid = isValidMove(row, col, number)
        return currentState.copy(
            board = currentState.board.mapIndexed { r, rowCells ->
                rowCells.mapIndexed { c, cell ->
                    if (r == row && c == col) {
                        cell.copy(value = number, notes = emptySet(), isValid = isValid)
                    } else cell
                }
            }
        )
    }

    fun toggleNotesMode() {
        _state.update { it.copy(isNotesMode = !it.isNotesMode) }
    }

    private fun isValidMove(row: Int, col: Int, number: Int): Boolean {
        // Check row
        for (c in 0..8) {
            if (c != col && _state.value.board[row][c].value == number) return false
        }

        // Check column
        for (r in 0..8) {
            if (r != row && _state.value.board[r][col].value == number) return false
        }

        // Check 3x3 box
        val boxRow = row - row % 3
        val boxCol = col - col % 3
        for (r in boxRow until boxRow + 3) {
            for (c in boxCol until boxCol + 3) {
                if ((r != row || c != col) && _state.value.board[r][c].value == number) {
                    return false
                }
            }
        }

        return true
    }

    private fun checkCompletion() {
        val isComplete = _state.value.board.all { row ->
            row.all { cell ->
                cell.value != 0 && cell.isValid
            }
        }

        if (isComplete) {
            _state.update { it.copy(isComplete = true) }
        }
    }

    fun clearCell() {
        val (row, col) = _state.value.selectedCell ?: return
        if (_state.value.board[row][col].isOriginal) return

        _state.update { currentState ->
            currentState.copy(
                board = currentState.board.mapIndexed { r, rowCells ->
                    rowCells.mapIndexed { c, cell ->
                        if (r == row && c == col) {
                            cell.copy(value = 0, notes = emptySet(), isValid = true)
                        } else cell
                    }
                }
            )
        }
    }
}