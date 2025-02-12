package com.example.sudokunew

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SudokuGameDao {
    @Query("SELECT * FROM sudoku_games ORDER BY createdAt DESC")
    fun getAllGames(): Flow<List<SudokuGameEntity>>

    @Query("SELECT * FROM sudoku_games WHERE id = :gameId")
    suspend fun getGameById(gameId: Long): SudokuGameEntity?

    //@Insert(onConflict = OnConflictStrategy.REPLACE)
    //suspend fun insertOrUpdateGame(game: SudokuGameEntity): Long

    @Query("DELETE FROM sudoku_games")
    suspend fun deleteAllGames()

    @Upsert
    suspend fun upsertGame(game: SudokuGameEntity): Long

    @Delete
    suspend fun deleteGame(game: SudokuGameEntity)
}