package com.example.sudokunew

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.sudokunew.ui.theme.SudokuNewTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController:NavHostController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "About") },
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
         LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp) // Add some padding around the content
                ) {
            item {
                // "Who Am I?" Section
                Text(
                    text = "Who Am I?",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Hello, I am Andreas Yiangou. I am from Cyprus and currently a fourth year BSc Computer Science student at the University of Nicosia. " +
                            "This app is the first app I have developed for Android and I am excited to share it with you.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.Justify
                )
            }

            item {
                // Spacer between sections
                Spacer(modifier = Modifier.padding(vertical = 16.dp))
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.portrait_placeholder),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(150.dp)
                            .align(Alignment.Center)

                    )
                }
            }

            item {
                // Spacer between sections
                Spacer(modifier = Modifier.padding(vertical = 16.dp))
            }

            item {
                // "About the Project" Section
                Text(
                    text = "About the Project",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "This Sudoku app aims to provide a simple yet elegant solution to solving" +
                            " and playing Sudoku puzzles. The main goal of the project is to offer an intuitive" +
                            " interface with features like easy puzzle generation, undo functionality, and a " +
                            "clean design. It was developed in collaboration with Dr. Andreas Savva as part " +
                            "of my final year project for my BSc in Computer Science at the University of Nicosia.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.Justify
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Thank you for checking out my app!",
                        color = Color.Blue,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .align(Alignment.Center)
                    )
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Enjoy!",
                        color = Color.Blue,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }
}
