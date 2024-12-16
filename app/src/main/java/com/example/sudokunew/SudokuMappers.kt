import com.example.sudokunew.SudokuGameEntity
import com.example.sudokunew.SudokuState

/**
 * Extension functions to convert between domain models and database entities
 */

/**
 * Converts a SudokuState (domain model) to a SudokuGameEntity (database entity)
 * Used when saving game state to the database
 */
fun SudokuState.toEntity(gameId: Long? = null): SudokuGameEntity {
    return SudokuGameEntity(
        id = gameId ?: 0, // Use existing ID if available, otherwise use 0 for new games
        board = board,
        selectedCell = selectedCell,
        difficulty = difficulty,
        isNotesMode = isNotesMode,
        isComplete = isComplete,
        timer = timer
    )
}

/**
 * Converts a SudokuGameEntity (database entity) to a SudokuState (domain model)
 * Used when loading game state from the database
 */
fun SudokuGameEntity.toState() = SudokuState(
    board = board,
    selectedCell = selectedCell,
    difficulty = difficulty,
    isNotesMode = isNotesMode,
    isComplete = isComplete,
    timer = timer
)