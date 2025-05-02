package com.example.sudokunew.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.sudokunew.model.Difficulty
import com.example.sudokunew.model.SudokuCell
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Entity(tableName = "sudoku_games")
data class SudokuGameEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val board: List<List<SudokuCell>>,
    val selectedCell: Pair<Int, Int>?,
    val difficulty: Difficulty,
    val isNotesMode: Boolean,
    val isComplete: Boolean,
    val timer: Long,
    val createdAt: Long = System.currentTimeMillis()
)

class Converters {
    @TypeConverter
    fun fromBoard(board: List<List<SudokuCell>>): String {
        return Json.encodeToString(board)
    }

    @TypeConverter
    fun toBoard(value: String): List<List<SudokuCell>> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromSelectedCell(selectedCell: Pair<Int, Int>?): String? {
        return selectedCell?.let { (row, col) -> "$row,$col" }
    }

    @TypeConverter
    fun toSelectedCell(value: String?): Pair<Int, Int>? {
        return value?.split(",")?.let { Pair(it[0].toInt(), it[1].toInt()) }
    }

    @TypeConverter
    fun fromDifficulty(difficulty: Difficulty): String {
        return difficulty.name
    }

    @TypeConverter
    fun toDifficulty(value: String): Difficulty {
        return Difficulty.valueOf(value)
    }
}