package com.example.sudokunew

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.sudokunew.navigation.SudokuNavHost

@Composable
fun SudokuApp(navController: NavHostController = rememberNavController()) {

    SudokuNavHost(navController = navController)

}