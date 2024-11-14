package com.example.sudokunew

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun SudokuApp(navController: NavHostController = rememberNavController()) {

    SudokuNavHost(navController = navController)

}