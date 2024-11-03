package com.example.sudokunew

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Stack

class SudokuViewModel : ViewModel() {
    private val _state = MutableStateFlow(SudokuState())
    val state: StateFlow<SudokuState> = _state.asStateFlow()
    private var solutionBoard = Array(9) { Array(9) { 0 } }
    private val history = Stack<SudokuState>()
    private val difficulty = Difficulty.MEDIUM
    private var timerJob: Job? = null

    init {
        startNewGame(difficulty)
    }

    fun startNewGame(difficulty: Difficulty) {
        //originalBoard : complete and solved Sudoku puzzle
        //solutionBoard : represents the solution to the Sudoku puzzle to be used with ShowHint().
        //initialBoard :  represents the Sudoku puzzle as it will be presented to the player.
        // Defines the game state that the player interacts with, while preserving the original values for validation and gameplay logic

        val originalBoard = Array(9) { Array(9) { 0 } }
        fillBoard(originalBoard)
        solutionBoard = originalBoard.map { it.copyOf() }.toTypedArray()
        removeNumbers(originalBoard, difficulty.cellsToRemove)

        val initialBoard = List(9) { row ->
            List(9) { col ->
                SudokuCell(
                    value = originalBoard[row][col],
                    isOriginal = originalBoard[row][col] != 0
                )
            }
        }

        _state.value = SudokuState(board = initialBoard,difficulty = difficulty)
        history.clear()
        startTimer()
    }



    private fun fillBoard(board: Array<Array<Int>>): Boolean {
        for (row in 0 until 9) {
            for (col in 0 until 9) {
                if (board[row][col] == 0) {
                    val numbers = (1..9).shuffled()
                    for (number in numbers) {
                        if (isValid(board, row, col, number)) {
                            board[row][col] = number
                            if (fillBoard(board)) {
                                return true
                            }
                            board[row][col] = 0
                        }
                    }
                    return false
                }
            }
        }
        return true //Board filled
    }

    private fun removeNumbers(board: Array<Array<Int>>, cellsToRemove: Int) {
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

    private fun isValid(board: Array<Array<Int>>, row: Int, col: Int, number: Int): Boolean {
        // Check row and column
        for (i in 0 until 9) {
            if (board[row][i] == number || board[i][col] == number) {
                return false
            }
        }

        // Check 3x3 box
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
            val newState = if (currentState.isNotesMode) {
                handleNoteInput(currentState, row, col, number)
            } else {
                handleNumberInput(currentState, row, col, number)
            }
            history.push(currentState.copy(isNotesMode = false))
            newState
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
        } else if (currentCell.notes.size < 6) {
            currentCell.notes + number
        } else currentCell.notes

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

        val isValid = isValid(currentState.board.map { it.map { cell -> cell.value }.toTypedArray() }.toTypedArray(), row, col, number)
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

    private fun checkCompletion() {
        val isComplete = _state.value.board.all { row ->
            row.all { cell ->
                cell.value != 0 && cell.isValid
            }
        }

        if (isComplete) {
            timerJob?.cancel()
            _state.update { it.copy(isComplete = true) }
        }
    }

    fun showHint() {
        if (_state.value.isComplete) return

        _state.update { currentState ->
            var hintApplied = false

            val newBoard = currentState.board.mapIndexed { r, rowCells ->
                rowCells.mapIndexed { c, cell ->
                    if (!cell.isOriginal && cell.value == 0 && !hintApplied) {
                        val correctNumber = solutionBoard[r][c]
                        if (correctNumber != 0) {
                            hintApplied = true
                            cell.copy(value = correctNumber, notes = emptySet(), isValid = true, isOriginal = true)
                        } else cell
                    } else cell
                }
            }

            if (!hintApplied) {
                history.push(currentState.copy(isNotesMode = false))
                currentState
            } else {
                history.push(currentState.copy(isNotesMode = false))
                currentState.copy(
                    board = newBoard,
                )
            }

        }

        checkCompletion()
    }

    fun showSolution() {
        _state.update { currentState ->

            val newBoard = currentState.board.mapIndexed { r, rowCells ->
                rowCells.mapIndexed { c, cell ->
                    if (!cell.isOriginal && cell.value == 0 ) {
                        val correctNumber = solutionBoard[r][c]
                        if (correctNumber != 0) {
                            cell.copy(value = correctNumber, notes = emptySet(), isValid = true, isOriginal = true)
                        } else cell
                    } else cell
                }
            }

            currentState.copy(
                board = newBoard,
            )
        }

        //checkCompletion()
    }

    fun clearCell() {
        val (row, col) = _state.value.selectedCell ?: return
        if (_state.value.board[row][col].isOriginal) return

        _state.update { currentState ->
           val newState= currentState.copy(
                board = currentState.board.mapIndexed { r, rowCells ->
                    rowCells.mapIndexed { c, cell ->
                        if (r == row && c == col) {
                            cell.copy(value = 0, notes = emptySet(), isValid = true)
                        } else cell
                    }
                }
            )
            history.push(currentState.copy(isNotesMode = false))
            newState
        }
    }

    fun undo(){
        if (history.isNotEmpty()) {
            _state.value = history.pop()
        }
    }

    private fun startTimer(){
        timerJob?.cancel()
        _state.update { it.copy(timer = 0L) } // Reset timer

        timerJob = viewModelScope.launch(Dispatchers.Main) {
            while (!_state.value.isComplete) {
                delay(1000) // Wait for 1 second
                _state.update { currentState ->
                    currentState.copy(timer = currentState.timer + 1000)
                }
            }
        }

    }
}
