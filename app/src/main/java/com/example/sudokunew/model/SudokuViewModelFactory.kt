package com.example.sudokunew.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sudokunew.data.SudokuDatabase

class SudokuViewModelFactory(
    private val database: SudokuDatabase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SudokuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SudokuViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}