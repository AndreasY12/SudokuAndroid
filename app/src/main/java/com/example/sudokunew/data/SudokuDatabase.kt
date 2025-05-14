package com.example.sudokunew.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [SudokuGameEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SudokuDatabase : RoomDatabase() {
    abstract fun sudokuGameDao(): SudokuGameDao

    companion object {
        @Volatile
        private var INSTANCE: SudokuDatabase? = null

        fun getDatabase(context: Context): SudokuDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SudokuDatabase::class.java,
                    "sudoku_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}