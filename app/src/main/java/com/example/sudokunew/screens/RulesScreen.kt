package com.example.sudokunew.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.sudokunew.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesScreen(
    navController: NavHostController
) {
    val isDarkTheme = isSystemInDarkTheme()
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.navigationBars.asPaddingValues()),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.sudoku_rules)) },
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
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Header
            Text(
                text = stringResource(R.string.how_to_play),
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isDarkTheme) {
                // Rules List
                RuleRow(
                    ruleText = stringResource(R.string.rule_1)
                )

                RuleRow(
                    ruleText = stringResource(R.string.rule_2),
                    image = R.drawable.row_dark,
                    imageSize = 256.dp
                )
                RuleRow(
                    ruleText = stringResource(R.string.rule_3),
                    image = R.drawable.column_dark,
                    imageSize = 256.dp
                )
                RuleRow(
                    ruleText = stringResource(R.string.rule_4),
                    image = R.drawable.box_dark,
                    imageSize = 128.dp
                )
            } else {
                // Rules List
                RuleRow(
                    ruleText = stringResource(R.string.rule_1)
                )

                RuleRow(
                    ruleText = stringResource(R.string.rule_2),
                    image = R.drawable.row_light,
                    imageSize = 256.dp
                )
                RuleRow(
                    ruleText = stringResource(R.string.rule_3),
                    image = R.drawable.column_light,
                    imageSize = 256.dp
                )
                RuleRow(
                    ruleText = stringResource(R.string.rule_4),
                    image = R.drawable.box_light,
                    imageSize = 128.dp
                )
            }
            //Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun RuleRow(
    modifier: Modifier = Modifier,
    ruleText: String,
    @DrawableRes image: Int? = null,
    imageSize: Dp = 48.dp
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = ruleText,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    //color = MaterialTheme.colors.onBackground
                ),
                modifier = Modifier.fillMaxWidth()
            )


            image?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier
                        .size(imageSize)
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp)
                )
            }
        }
    }
}