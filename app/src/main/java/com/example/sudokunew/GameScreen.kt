package com.example.sudokunew

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: SudokuViewModel = viewModel(
        factory = SudokuViewModelFactory(
            SudokuDatabase.getDatabase(LocalContext.current)
        )
    ),
    navController: NavHostController,
    difficulty: Difficulty,
    gameId: Long? = null
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var showHelpDialog by rememberSaveable { mutableStateOf(false) }
    var showSaveDialog by rememberSaveable { mutableStateOf(false) }
    var showCompletedDialog by rememberSaveable { mutableStateOf(false) }
    var showSolutionConfirmDialog by rememberSaveable { mutableStateOf(false) }
    val layoutDirection = LocalLayoutDirection.current
    var gameStarted by rememberSaveable { mutableStateOf(false) }
    val soundPlayer = SoundPlayer(LocalContext.current)
    //val difficulty = state.difficulty

    val colors = MaterialTheme.colorScheme

    val shakeDetector = remember {
        ShakeDetector {
            Log.d("GameScreen", "Shake detected, showing dialog")
            showSolutionConfirmDialog = true
        }
    }

    DisposableEffect(context) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer == null) {
            Log.e("GameScreen", "No accelerometer sensor found on device")
        } else {
            val registered = sensorManager.registerListener(
                shakeDetector,
                accelerometer,
                SensorManager.SENSOR_DELAY_GAME // Use SENSOR_DELAY_GAME for better responsiveness
            )

            if (!registered) {
                Log.e("GameScreen", "Failed to register sensor listener")
            } else {
                Log.d("GameScreen", "Successfully registered shake detector")
            }
        }

        onDispose {
            Log.d("GameScreen", "Unregistering shake detector")
            sensorManager.unregisterListener(shakeDetector)
        }
    }

    LaunchedEffect(Unit) {
        soundPlayer.release()
        if (!gameStarted) {
            if (gameId != null) {
                // Load saved game
                viewModel.setGameId(gameId)
                viewModel.loadGame(gameId)
            } else {
                // Start new game
                viewModel.startNewGame(difficulty)
            }
            gameStarted = true
        }
    }

    /*BackHandler {
        viewModel.saveGame()
        navController.navigate("start?gameJustSaved=true") {
            popUpTo("start") { inclusive = true }
        }
    }*/

    BackHandler {
        showSaveDialog = true
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text(stringResource(R.string.save_game_prompt), color = colors.onBackground) },
            text = {
                Text(
                    stringResource(R.string.save_game_message),
                    color = colors.onBackground
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        Log.d("GameScreen", "Saving game")
                        viewModel.saveGame()
                        showSaveDialog = false
                        navController.navigate("start?gameJustSaved=true") {
                            popUpTo("start") { inclusive = true }
                        }
                    }
                ) {
                    Text(stringResource(R.string.save), color = colors.primary)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        Log.d("GameScreen", "Not saving game")
                        showSaveDialog = false
                        navController.navigate("start?gameJustSaved=false") {
                            popUpTo("start") { inclusive = true }
                        }
                    }
                ) {
                    Text(stringResource(R.string.dont_save), color = colors.primary)
                }
            }
        )
    }

    if (showSolutionConfirmDialog && !showSaveDialog) {
        Log.d("GameScreen", "Showing shake confirmation dialog")
        AlertDialog(
            onDismissRequest = {
                Log.d("GameScreen", "Dialog dismissed")
                showSolutionConfirmDialog = false
            },
            title = { Text(stringResource(R.string.show_solution_prompt), color = colors.onBackground) },
            text = {
                Text(
                    stringResource(R.string.show_solution_message),
                    color = colors.onBackground
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        Log.d("GameScreen", "Showing solution")
                        showSolutionConfirmDialog = false
                        viewModel.showSolution()
                    }
                ) {
                    Text(
                        stringResource(R.string.show_solution),
                        color = colors.primary)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        Log.d("GameScreen", "Dialog cancelled")
                        showSolutionConfirmDialog = false
                    }
                ) {
                    Text(stringResource(R.string.cancel), color = colors.primary)
                }
            }
        )
    }

    //viewModel.startNewGame(difficulty)

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = WindowInsets.safeDrawing
                    .asPaddingValues()
                    .calculateStartPadding(layoutDirection),
                end = WindowInsets.safeDrawing
                    .asPaddingValues()
                    .calculateEndPadding(layoutDirection)
            ),
        containerColor = colors.background, contentColor = colors.onBackground, topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                    navigationIconContentColor = colors.onBackground,
                    titleContentColor = colors.onBackground,
                    actionIconContentColor = colors.onBackground
                ),
                title = {
                    Text(
                        "Sudoku - " + getDifficulty(state.difficulty),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = colors.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        /*viewModel.saveGame()
                        navController.navigate("start?gameJustSaved=true") {
                            popUpTo("start") { inclusive = true }
                        }*/
                        showSaveDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "",
                            tint = colors.onBackground
                        )
                    }
                }, actions = {
                    IconButton(onClick = { showHelpDialog = true }) {
                        Icon(
                            Icons.AutoMirrored.Filled.Help,
                            contentDescription = "",
                            tint = colors.onBackground
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.time) + " ${formatTime(state.timer)}",
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = colors.onBackground
            )
            SudokuGrid(
                board = state.board,
                onCellSelected = viewModel::onCellSelected
            )
            NumberPad(
                onNumberSelected = viewModel::onNumberInput,
                isNotesMode = state.isNotesMode
            )
            Toolbar(
                isNotesMode = state.isNotesMode,
                onNotesClicked = viewModel::toggleNotesMode,
                onClearClicked = viewModel::clearCell,
                onHintsClicked = viewModel::showHint,
                onUndoClicked = viewModel::undo
            )
            ShowSolutionButton(
                onSolutionClicked = { showSolutionConfirmDialog = true }
            )
        }

        LaunchedEffect(state.isComplete) {
            if (state.isComplete) {
                showCompletedDialog = true
                soundPlayer.playSound(R.raw.win_effect)
            }
        }

        // Show completion dialog if game is complete
        if (state.isComplete) {
            ShowConfetti()
        }
        if (showCompletedDialog) {
            AlertDialog(
                onDismissRequest = { showCompletedDialog = false },
                title = { Text(stringResource(R.string.congratulations), color = colors.onBackground) },
                text = {
                    Text(
                        stringResource(R.string.completed_message) + "${formatTime(state.timer)}!",
                        color = colors.onBackground
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        showCompletedDialog = false
                        viewModel.startNewGame(difficulty)
                    }) {
                        Text(stringResource(R.string.new_game), color = colors.primary)
                    }
                }
            )
        }
    }
    // Information dialog
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text(stringResource(R.string.icon_actions), color = colors.onBackground) },
            text = {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo",
                            modifier = Modifier.size(24.dp),
                            tint = colors.onBackground
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(stringResource(R.string.undo_action), color = colors.onBackground)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Clear",
                            modifier = Modifier.size(24.dp),
                            tint = colors.onBackground
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(stringResource(R.string.clear_action), color = colors.onBackground)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Notes",
                            modifier = Modifier.size(24.dp),
                            tint = colors.onBackground
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            stringResource(R.string.notes_action),
                            color = colors.onBackground
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Lightbulb,
                            contentDescription = "Hints",
                            modifier = Modifier.size(24.dp),
                            tint = colors.onBackground
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(stringResource(R.string.hints_action), color = colors.onBackground)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Vibration,
                            contentDescription = "Shake Phone",
                            modifier = Modifier.size(24.dp),
                            tint = colors.onBackground
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(stringResource(R.string.shake_action), color = colors.onBackground)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text(stringResource(R.string.ok), color = colors.primary)
                }
            }
        )
    }
}

@Composable
fun SudokuGrid(
    board: List<List<SudokuCell>>,
    onCellSelected: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val gridHeight = screenHeight * 0.7f
    val colors = MaterialTheme.colorScheme

    // Find the selected cell position
    val selectedPosition = board.flatMapIndexed { rowIndex, row ->
        row.mapIndexed { colIndex, cell ->
            if (cell.isSelected) Pair(rowIndex, colIndex) else null
        }
    }.filterNotNull().firstOrNull()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = gridHeight)
            .padding(8.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            items(9) { gridIndex ->
                val rowStart = (gridIndex / 3) * 3
                val colStart = (gridIndex % 3) * 3

                SudokuSubGrid(
                    board = board,
                    rowStart = rowStart,
                    colStart = colStart,
                    selectedPosition = selectedPosition,
                    onCellSelected = onCellSelected,
                    modifier = Modifier.border(2.dp, colors.onBackground)
                )
            }
        }
    }
}

@Composable
fun SudokuSubGrid(
    board: List<List<SudokuCell>>,
    rowStart: Int,
    colStart: Int,
    selectedPosition: Pair<Int, Int>?,
    onCellSelected: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.aspectRatio(1f)) {
        for (row in rowStart until rowStart + 3) {
            Row(modifier = Modifier.weight(1f)) {
                for (col in colStart until colStart + 3) {
                    val cell = board[row][col]
                    val isInSelectedRow = selectedPosition?.first == row
                    val isInSelectedColumn = selectedPosition?.second == col

                    SudokuCell(
                        cell = cell,
                        isInSelectedLine = isInSelectedRow || isInSelectedColumn,
                        onClick = { onCellSelected(row, col) },
                        modifier = Modifier
                            .weight(1f)
                            .border(0.5.dp, Color.LightGray)
                    )
                }
            }
        }
    }
}

@Composable
fun SudokuCell(
    cell: SudokuCell,
    isInSelectedLine: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    // Define background color based on cell state
    val backgroundColor = when {
        cell.isSelected -> colors.secondary.copy(alpha = 0.3f)
        !cell.isValid -> colors.error.copy(alpha = 0.3f)
        isInSelectedLine -> colors.secondary.copy(alpha = 0.1f) // Subtle highlight for row/column
        else -> Color.Transparent
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (cell.value != 0) {
            Text(
                text = cell.value.toString(),
                fontSize = 24.sp,
                color = if (cell.isOriginal) colors.onBackground else colors.primary
            )
        } else if (cell.notes.isNotEmpty()) {
            NotesGrid(notes = cell.notes)
        }
    }
}

@Composable
fun NotesGrid(notes: Set<Int>) {
    val limitedNotes = notes.take(6).toSet()
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            limitedNotes.take(3).forEach { number ->
                Text(
                    text = number.toString(),
                    fontSize = 10.sp,
                    color = colors.onSurface, textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            limitedNotes.drop(3).take(3).forEach { number ->
                Text(
                    text = number.toString(),
                    fontSize = 10.sp,
                    color = colors.onSurface, textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}


@Composable
fun NumberPad(
    onNumberSelected: (Int) -> Unit,
    isNotesMode: Boolean,
    modifier: Modifier = Modifier
) {

    val colors = MaterialTheme.colorScheme

    Column(modifier = modifier.fillMaxWidth()) {
        val numbers = (1..9).chunked(3)
        numbers.forEach { numberRow ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                numberRow.forEach { number ->
                    TextButton(
                        onClick = { onNumberSelected(number) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = number.toString(),
                            fontSize = 30.sp,
                            color = if (isNotesMode) Color.Blue else colors.onBackground
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Toolbar(
    isNotesMode: Boolean,
    onNotesClicked: () -> Unit,
    onHintsClicked: () -> Unit,
    onClearClicked: () -> Unit,
    onUndoClicked: () -> Unit,
    modifier: Modifier = Modifier
) {

    val colors = MaterialTheme.colorScheme

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(onClick = onUndoClicked) {
            Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo")

        }
        IconButton(onClick = onClearClicked) {
            Icon(Icons.Default.Delete, contentDescription = "Clear")
        }
        IconButton(
            onClick = onNotesClicked
        ) {
            Icon(
                Icons.Default.Edit,
                contentDescription = "Notes",
                tint = if (isNotesMode) Color.Blue else colors.onBackground
            )
        }
        IconButton(onClick = onHintsClicked) {
            Icon(Icons.Default.Lightbulb, contentDescription = "Hints")
        }
    }
}

@Composable
fun ShowSolutionButton(
    onSolutionClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onSolutionClicked,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(stringResource(R.string.show_solution_button))
        }
    }
}

@Composable
fun ShowConfetti() {
    KonfettiView(
        modifier = Modifier.fillMaxSize(),
        parties = listOf(
            Party(
                emitter = Emitter(duration = 5, TimeUnit.SECONDS).perSecond(30)
            )
        )
    )
}


@Composable
private fun getDifficulty(difficulty: Difficulty): String {
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