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
        composable("start"){
            StartScreen(navController = navController)
        }
        composable("game") {
            GameScreen()
        }
        composable("rules"){
            RulesScreen()
        }
        composable("about"){
            AboutScreen(navController = navController)
        }


    }
}