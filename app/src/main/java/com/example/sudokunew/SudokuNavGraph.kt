package com.example.sudokunew

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun SudokuNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
){
    NavHost(
        navController = navController,
        startDestination = "start",
        modifier = modifier
    ){
        composable("start") {
            StartScreen(navController = navController, onNewGameStart = { difficulty ->
                navController.navigate("game/${difficulty.name}")
            })
        }
        composable("game/{difficulty}") { backStackEntry ->
            val difficulty = Difficulty.valueOf(backStackEntry.arguments?.getString("difficulty") ?: "EASY")
            GameScreen(navController = navController, difficulty = difficulty)
        }
        composable("rules"){
            RulesScreen(navController = navController)
        }
        composable("about"){
            AboutScreen(navController = navController)
        }


    }
}