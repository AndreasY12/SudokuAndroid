package com.example.sudokunew

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SudokuGameDao {
    @Query("SELECT * FROM sudoku_games ORDER BY createdAt DESC")
    fun getAllGames(): Flow<List<SudokuGameEntity>>

    @Query("SELECT * FROM sudoku_games WHERE id = :gameId")
    suspend fun getGameById(gameId: Long): SudokuGameEntity?

    @Insert
    suspend fun insertGame(game: SudokuGameEntity): Long

    @Update
    suspend fun updateGame(game: SudokuGameEntity)

    @Delete
    suspend fun deleteGame(game: SudokuGameEntity)
}