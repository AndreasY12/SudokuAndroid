package com.example.sudokunew

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadGameScreen(
    navController: NavHostController,
    onGameSelected: (Long) -> Unit,
    viewModel: SudokuViewModel = viewModel(
        factory = SudokuViewModelFactory(
            SudokuDatabase.getDatabase(LocalContext.current)
        )
    )
) {
    val savedGames by viewModel.getSavedGames().collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Saved Games",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            if (savedGames.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No saved games found",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(savedGames) { game ->
                        SavedGameItem(
                            game = game,
                            onLoadGame = { onGameSelected(game.id) },
                            onDeleteGame = {
                                viewModel.deleteSavedGame(game)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedGameItem(
    game: SudokuGameEntity,
    onLoadGame: () -> Unit,
    onDeleteGame: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onLoadGame)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Difficulty: ${game.difficulty}",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Created: ${formatDate(game.createdAt)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Time: ${formatTime(game.timer)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            IconButton(onClick = onDeleteGame) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    //Make the icon red
                    tint = MaterialTheme.colorScheme.error,
                    contentDescription = "Delete game"
                )
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    return SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        .format(Date(timestamp))
}

private fun formatTime(milliseconds: Long): String {
    val seconds = (milliseconds / 1000) % 60
    val minutes = (milliseconds / 60000) % 60
    return String.format(Locale.UK, "%02d:%02d", minutes, seconds)
}