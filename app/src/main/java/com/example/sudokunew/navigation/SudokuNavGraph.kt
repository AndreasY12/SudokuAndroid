package com.example.sudokunew.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.sudokunew.model.Difficulty
import com.example.sudokunew.screens.AboutScreen
import com.example.sudokunew.screens.GameScreen
import com.example.sudokunew.screens.LoadGameScreen
import com.example.sudokunew.screens.RulesScreen
import com.example.sudokunew.screens.StartScreen

@Composable
fun SudokuNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = "start",
        modifier = modifier
    ) {
        composable(
            route = "start?gameJustSaved={gameJustSaved}",
            arguments = listOf(
                navArgument("gameJustSaved") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val gameJustSaved = backStackEntry.arguments?.getBoolean("gameJustSaved") ?: false
            StartScreen(
                navController = navController,
                onNewGameStart = { difficulty ->
                    navController.navigate("game/new/${difficulty.name}")
                },
                gameJustSaved = gameJustSaved
            )
        }

        // Route for new games
        composable(
            route = "game/new/{difficulty}",
            arguments = listOf(
                navArgument("difficulty") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val difficulty = Difficulty.valueOf(
                backStackEntry.arguments?.getString("difficulty") ?: "EASY"
            )
            GameScreen(
                navController = navController,
                difficulty = difficulty
            )
        }

        // Route for loading saved games
        composable(
            route = "game/load/{gameId}",
            arguments = listOf(
                navArgument("gameId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getLong("gameId")
            requireNotNull(gameId) { "Game ID cannot be null" }
            GameScreen(
                navController = navController,
                difficulty = Difficulty.EASY, // This will be overridden by loaded game data
                gameId = gameId
            )
        }

        composable("load") {
            LoadGameScreen(
                navController = navController,
                onGameSelected = { gameId ->
                    navController.navigate("game/load/$gameId") {
                        // Pop up to start destination to avoid stacking screens
                        popUpTo("start") { saveState = true }
                    }
                }
            )
        }

        composable("rules") {
            RulesScreen(navController = navController)
        }

        composable("about") {
            AboutScreen(navController = navController)
        }
    }
}
