package com.example.sudokunew

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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


@Preview(showBackground = true)
@Composable
fun SudokuApp() {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding() // Ensure content is not covered by the status bar
            .navigationBarsPadding(),
        containerColor = Color.Transparent,
        contentColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(modifier = Modifier.size(16.dp))
            SudokuGrid()
            Spacer(modifier = Modifier.size(8.dp))
            NumberPad()
            Spacer(modifier = Modifier.size(8.dp))
            Toolbar()
        }
    }
}


@Composable
fun SudokuCell(number: Int, modifier: Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .border(1.dp, Color.Black), contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (number == 0) "" else number.toString(),
            fontSize = 30.sp,
        )
    }
}


@Composable
fun SudokuGrid(modifier: Modifier = Modifier) {
    val sudokuBoard = SudokuBoard() // Create an instance of SudokuBoard
    val board = sudokuBoard.getBoard() // Get the board

    LazyVerticalGrid(
        columns = GridCells.Fixed(9),
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        items(81) { index ->
            val row = index / 9
            val col = index % 9
            val number = board[row][col]

            SudokuCell(
                number = number,
                modifier = Modifier
            )
        }
    }
}

@Composable
fun NumberPad(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        val numbers = (1..9).chunked(3)
        numbers.forEach { numberRow ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            )
            {
                numberRow.forEach { number ->
                    TextButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = number.toString(),
                            fontSize = 30.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Toolbar(modifier: Modifier = Modifier){
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly)
    {
        IconButton(onClick = { /* Handle undo */ }) {
            Icon(Icons.Default.Refresh, contentDescription = "Undo")
        }
        IconButton(onClick = { /* Handle erase */ }) {
            Icon(Icons.Default.Delete, contentDescription = "Erase")
        }
        IconButton(onClick = { /* Handle notes */ }) {
            Icon(Icons.Default.Edit, contentDescription = "Notes")
        }
        IconButton(onClick = { /* Handle hints */ }) {
            Icon(Icons.Default.Done, contentDescription = "Hints")
        }
    }

}