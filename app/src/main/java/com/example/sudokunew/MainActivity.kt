package com.example.sudokunew

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sudokunew.ui.theme.SudokuNewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SudokuNewTheme {
                SudokuApp()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SudokuApp(viewModel: SudokuViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val state by viewModel.state.collectAsState()
    var showDialog by rememberSaveable { mutableStateOf(false) }
    val layoutDirection = LocalLayoutDirection.current

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
            //.statusBarsPadding()
            //.navigationBarsPadding(),
        containerColor = Color.Transparent,
        contentColor = Color.Black,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                    navigationIconContentColor = Color.Black,
                    titleContentColor = Color.Black,
                    actionIconContentColor = Color.Black
                ),
                title = {
                    Text(
                        "Sudoku",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = ""
                        )
                    }
                }, actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(
                            Icons.AutoMirrored.Filled.Help,
                            contentDescription = ""
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
            //Spacer(modifier = Modifier.size(8.dp))
            SudokuGrid(
                board = state.board,
                onCellSelected = viewModel::onCellSelected
            )
            Spacer(modifier = Modifier.size(8.dp))
            NumberPad(
                onNumberSelected = viewModel::onNumberInput,
                isNotesMode = state.isNotesMode
            )
            Spacer(modifier = Modifier.size(8.dp))
            Toolbar(
                isNotesMode = state.isNotesMode,
                onNotesClicked = viewModel::toggleNotesMode,
                onClearClicked = viewModel::clearCell,
                onHintsClicked =  viewModel::showHint,
                onUndoClicked = viewModel::undo
            )
            Spacer(modifier = Modifier.size(8.dp))
            ShowSolutionButton(
                onSolutionClicked = viewModel::showSolution
            )
        }

        // Show completion dialog if game is complete
        if (state.isComplete) {
            AlertDialog(
                onDismissRequest = { /* Handle dismiss */ },
                title = { Text("Congratulations!") },
                text = { Text("You've completed the puzzle!") },
                confirmButton = {
                    TextButton(onClick = { viewModel.startNewGame(Difficulty.MEDIUM) }) {
                        Text("New Game")
                    }
                }
            )
        }
        // Information dialog
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Icon Actions") },
                text = {
                    Column {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Undo,
                                    contentDescription = "Undo",
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text("Undo: Reverses the last action.")
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Clear",
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text("Clear: Removes all numbers entered.")
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Notes",
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text("Notes: Allows you to enter numbers as notes in a cell.")
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Lightbulb,
                                    contentDescription = "Hints",
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text("Hints: Provides a helpful tip.")
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
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
                    onCellSelected = onCellSelected,
                    modifier = Modifier.border(2.dp, Color.Black)
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
    onCellSelected: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.aspectRatio(1f)) {
        for (row in rowStart until rowStart + 3) {
            Row(modifier = Modifier.weight(1f)) {
                for (col in colStart until colStart + 3) {
                    val cell = board[row][col]
                    SudokuCell(
                        cell = cell,
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            //.border(1.dp, Color.Black)
            .clickable(onClick = onClick)
            .background(
                when {
                    cell.isSelected -> Color.Gray.copy(alpha = 0.3f)
                    !cell.isValid -> Color.Red.copy(alpha = 0.1f)
                    else -> Color.Transparent
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (cell.value != 0) {
            Text(
                text = cell.value.toString(),
                fontSize = 24.sp,
                color = if (cell.isOriginal) Color.Black else Color.Blue
            )
        } else if (cell.notes.isNotEmpty()) {
            NotesGrid(notes = cell.notes)
        }
    }
}

/*TODO Fix notes not appearing correctly on some screens*/
@Composable
fun NotesGrid(notes: Set<Int>) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        for (row in 0..2) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (col in 0..2) {
                    val number = row * 3 + col + 1
                    if (notes.contains(number)) {
                        Text(
                            text = number.toString(),
                            fontSize = 9.sp,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
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
                            color = if (isNotesMode) Color.Blue else Color.Black
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
                tint = if (isNotesMode) Color.Blue else Color.Black
            )
        }
        IconButton(onClick = onHintsClicked) {
            Icon(Icons.Default.Lightbulb, contentDescription = "Clear")
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
            onClick = onSolutionClicked ,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Show Solution")
        }
    }
}