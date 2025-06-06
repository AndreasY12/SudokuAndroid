package com.example.sudokunew.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sudokunew.data.SudokuDatabase
import com.example.sudokunew.data.SudokuGameEntity
import com.example.sudokunew.utils.toEntity
import com.example.sudokunew.utils.toState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Stack

class SudokuViewModel(private val database: SudokuDatabase) : ViewModel() {
    private val _state = MutableStateFlow(SudokuState())
    val state: StateFlow<SudokuState> = _state.asStateFlow()
    private var solutionBoard = Array(9) { Array(9) { 0 } }
    private val history = Stack<SudokuState>()

    //private var difficulty = Difficulty.MEDIUM
    private var timerJob: Job? = null
    private var gameId: Long? = null

    fun setGameId(gameId: Long) {
        this.gameId = gameId
    }

    /*
    init {
        startNewGame(difficulty)
    }
     */

    fun loadGame(gameId: Long) {
        viewModelScope.launch {
            database.sudokuGameDao().getGameById(gameId)?.let { gameEntity ->
                _state.value = gameEntity.toState()
                startTimer()
                history.clear()
                fillBoard(solutionBoard)
            }
        }
    }

    fun getSavedGames(): Flow<List<SudokuGameEntity>> {
        return database.sudokuGameDao().getAllGames()
    }

    fun saveGame() {
        val game = _state.value.toEntity(gameId)
        viewModelScope.launch {
            database.sudokuGameDao().upsertGame(game)
            //Log.d("SudokuViewModel", "Game saved with id: $id")
        }
    }


    fun deleteSavedGame(game: SudokuGameEntity) {
        viewModelScope.launch {
            database.sudokuGameDao().deleteGame(game)
        }
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

        _state.value = SudokuState(board = initialBoard, difficulty = difficulty)
        history.clear()
        startTimer()
    }

    private fun solve(board: Array<Array<Int>>, stopAt: Int = 1): Int {
        var solutionsFound = 0

        fun backtrack(row: Int = 0, col: Int = 0): Boolean {
            val numbers = (1..9).shuffled() // Shuffle to add randomness in solving paths

            // If we've reached past the last row, the board is filled and valid
            if (row == 9) {
                solutionsFound++
                // Return false if we've found enough solutions (to stop early)
                return solutionsFound < stopAt
            }

            // Move to the next cell
            val nextRow = if (col == 8) row + 1 else row
            val nextCol = if (col == 8) 0 else col + 1

            // If current cell is already filled, skip to the next one
            if (board[row][col] != 0) {
                return backtrack(nextRow, nextCol)
            }

            // Try placing numbers 1–9 in this empty cell
            for (num in numbers) {
                if (isValid(board, row, col, num)) {
                    board[row][col] = num // Tentatively place the number

                    // Recursively try to solve the rest of the board
                    // If it returns false, it means we've found enough solutions
                    // So we return false too to stop further exploration
                    if (!backtrack(nextRow, nextCol)) {
                        return false
                    }

                    board[row][col] = 0 // Backtrack: undo the placement
                }
            }

            // If no number fits, or we’re still under stopAt, continue backtracking
            return true
        }

        backtrack()
        return solutionsFound
    }



    private fun fillBoard(board: Array<Array<Int>>) {
        solve(board)
    }

    private fun removeNumbers(board: Array<Array<Int>>, cellsToRemove: Int): Int {
        val positions = mutableListOf<Triple<Int, Int, Int>>() // row, col, original value

        // First, collect all filled positions with their values
        for (row in 0 until 9) {
            for (col in 0 until 9) {
                positions.add(Triple(row, col, board[row][col]))
            }
        }

        // Shuffle positions to randomize removal attempts
        positions.shuffle()

        var removedCount = 0
        var posIndex = 0

        while (removedCount < cellsToRemove && posIndex < positions.size) {
            val (row, col, originalValue) = positions[posIndex]

            // Try removing this number
            board[row][col] = 0

            if (hasUniqueSolution(board)) {
                removedCount++
            } else {
                // If multiple solutions exist, restore the number
                board[row][col] = originalValue
            }

            posIndex++
        }

        return removedCount
    }

    private fun hasUniqueSolution(board: Array<Array<Int>>): Boolean {
        val tempBoard = Array(9) { row -> Array(9) { col -> board[row][col] } }

        val solutionCount = solve(tempBoard, stopAt = 2)
        if(solutionCount > 1) {
            return false // More than one solution found
        }
        return true // Unique solution found
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

        val isValid =
            isValid(currentState.board.map { it.map { cell -> cell.value }.toTypedArray() }
                .toTypedArray(), row, col, number)
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

    /**
     * Reveals a hint by filling in a random empty cell with its correct value.
     * The hint is only shown if the puzzle is not complete.
     * The revealed cell is marked as original to prevent modification.
     */
    fun showHint() {
        // Don't show hints if the puzzle is already complete
        if (_state.value.isComplete) return

        _state.update { currentState ->
            // Find all empty non-original cells that have a solution
            val emptyCells = currentState.board.flatMapIndexed { row, rowCells ->
                rowCells.mapIndexedNotNull { col, cell ->
                    if (!cell.isOriginal && cell.value == 0 && solutionBoard[row][col] != 0) {
                        Pair(row, col)
                    } else null
                }
            }

            // If no valid cells for hints are found, return the current state
            if (emptyCells.isEmpty()) {
                return@update currentState
            }

            // Save the current state to history before applying the hint
            history.push(currentState.copy(isNotesMode = false))

            // Select a random empty cell and reveal its correct value
            val (row, col) = emptyCells.random()
            val newBoard = currentState.board.mapIndexed { r, rowCells ->
                rowCells.mapIndexed { c, cell ->
                    if (r == row && c == col) {
                        cell.copy(
                            value = solutionBoard[r][c],
                            notes = emptySet(),
                            isValid = true,
                            isOriginal = true
                        )
                    } else cell
                }
            }

            // Return the new state with the updated board
            currentState.copy(board = newBoard, highlightedHintCell = Pair(row, col))
        }

        viewModelScope.launch {
            delay(2000) // Show the hint for 2 seconds
            _state.update { currentState ->
                currentState.copy(highlightedHintCell = null)
            }
        }

        // Check if the puzzle is now complete after applying the hint
        checkCompletion()
    }

    fun showSolution() {
        _state.update { currentState ->

            val newBoard = currentState.board.mapIndexed { r, rowCells ->
                rowCells.mapIndexed { c, cell ->
                    if (!cell.isOriginal) {
                        val correctNumber = solutionBoard[r][c]
                        if (correctNumber != 0) {
                            cell.copy(
                                value = correctNumber,
                                notes = emptySet(),
                                isValid = true,
                                isOriginal = true
                            )
                        } else cell
                    } else cell
                }
            }

            currentState.copy(
                board = newBoard,
            )
        }
        timerJob?.cancel()
        //checkCompletion()
    }

    fun clearCell() {
        val (row, col) = _state.value.selectedCell ?: return
        if (_state.value.board[row][col].isOriginal) return

        _state.update { currentState ->
            val newState = currentState.copy(
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

    fun undo() {
        if (history.isNotEmpty()) {
            _state.value = history.pop()
        }
    }

    private fun startTimer() {

        if (gameId == null) {
            timerJob?.cancel()
            _state.update { it.copy(timer = 0L) } // Reset timer
        }


        timerJob = viewModelScope.launch(Dispatchers.Main) {
            while (!_state.value.isComplete) {
                delay(1000) // Wait for 1 second
                _state.update { currentState ->
                    currentState.copy(timer = currentState.timer + 1000)
                }
            }
        }


    }

    fun deleteAllSavedGames() {
        viewModelScope.launch {
            database.sudokuGameDao().deleteAllGames()
        }
    }


}
