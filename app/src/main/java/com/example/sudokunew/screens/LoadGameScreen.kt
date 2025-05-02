package com.example.sudokunew.screens

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.sudokunew.R
import com.example.sudokunew.data.SudokuDatabase
import com.example.sudokunew.data.SudokuGameEntity
import com.example.sudokunew.model.Difficulty
import com.example.sudokunew.model.SudokuViewModel
import com.example.sudokunew.model.SudokuViewModelFactory
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
    var showDialog by remember { mutableStateOf(false) }
    var gameToDelete by remember { mutableStateOf<SudokuGameEntity?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    if(showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text(stringResource(R.string.delete_all_games_title)) },
            text = {
                Text(stringResource(R.string.delete_all_games_message))
            },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteAllSavedGames(); showConfirmDialog = false }) {
                    Text(stringResource(R.string.yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text(stringResource(R.string.no))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.saved_games),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (savedGames.isNotEmpty()) {
                        FilledTonalButton(
                            onClick = { showConfirmDialog = true },
                            content = { Text(stringResource(R.string.delete_all)) },
                            modifier = Modifier.padding(16.dp)
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
                        text = stringResource(R.string.no_saved_games),
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
                                gameToDelete = game
                                showDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDialog && gameToDelete != null) {
        ConfirmationDialog(
            onConfirm = {
                viewModel.deleteSavedGame(gameToDelete!!)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun ConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_game_title)) },
        text = {
            Text(stringResource(R.string.delete_game_message))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.yes))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.no))
            }
        }
    )
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
                    text = stringResource(R.string.difficulty)+" ${formatDifficulty( game.difficulty)}",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.created) + " ${formatDate(game.createdAt)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(R.string.time) + " ${formatTime(game.timer)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            IconButton(onClick = onDeleteGame) {
                Icon(
                    imageVector = Icons.Default.Delete,
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

@Composable
private fun formatDifficulty(difficulty: Difficulty): String {
    val easy = stringResource(R.string.easy)
    val medium = stringResource(R.string.medium)
    val hard = stringResource(R.string.hard)
    return when (difficulty) {
        Difficulty.EASY -> easy
        Difficulty.MEDIUM -> medium
        Difficulty.HARD -> hard
    }
}

private fun formatTime(milliseconds: Long): String {
    val seconds = (milliseconds / 1000) % 60
    val minutes = (milliseconds / 60000) % 60
    return String.format(Locale.UK, "%02d:%02d", minutes, seconds)
}