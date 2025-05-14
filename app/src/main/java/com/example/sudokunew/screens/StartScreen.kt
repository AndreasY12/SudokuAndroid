package com.example.sudokunew.screens

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.sudokunew.R
import com.example.sudokunew.model.Difficulty
import com.example.sudokunew.utils.LanguageChangeHelper
import kotlinx.coroutines.delay


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun StartScreen(
    navController: NavHostController,
    gameJustSaved: Boolean = false,
    onNewGameStart: (Difficulty) -> Unit
) {

    // States for staggered animation
    var showLogo by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }
    var showDifficultyDialog by remember { mutableStateOf(false) }
    var showSnackbar by rememberSaveable { mutableStateOf(gameJustSaved) }
    val snackbarHostState = remember { SnackbarHostState() }
    val message = stringResource(R.string.game_saved)

    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            showSnackbar = false
        }
    }

    LaunchedEffect(Unit) {
        delay(100)
        showLogo = true
        delay(300)
        showButtons = true
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .padding(WindowInsets.navigationBars.asPaddingValues())
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                LanguageSelector()


                Spacer(modifier = Modifier.height(32.dp))

                AnimatedVisibility(
                    visible = showLogo,
                    enter = fadeIn() + slideInVertically(
                        initialOffsetY = { -50 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                    )
                ) {
                    Logo()
                }

                Spacer(modifier = Modifier.height(48.dp))

                AnimatedVisibility(
                    visible = showButtons,
                    enter = fadeIn() + slideInVertically(
                        initialOffsetY = { 50 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                    )
                ) {
                    ButtonGroup(
                        navController = navController,
                        onNewGameClick = { showDifficultyDialog = true }
                    )
                }
                if (showDifficultyDialog) {
                    DifficultySelectionDialog(
                        onDismiss = { showDifficultyDialog = false },
                        onDifficultySelected = { difficulty ->
                            onNewGameStart(difficulty)
                            showDifficultyDialog = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LanguageSelector(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val languageChangeHelper by lazy {
        LanguageChangeHelper()
    }
    val currentLanguageCode = languageChangeHelper.getLanguageCode(context)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.padding(top = 80.dp))
        Icon(
            imageVector = Icons.Default.Language,
            contentDescription = "Language",
            tint = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(R.string.language),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Language buttons
        LanguageButton(
            emoji = "🇬🇧",
            isSelected = currentLanguageCode == "en",
            onClick = {
                LanguageChangeHelper().changeLanguage(context, "en")
                activity?.let{
                    restartActivity(it)
                }
            }
        )

        LanguageButton(
            emoji = "🇬🇷",
            isSelected = currentLanguageCode == "el",
            onClick = {
                LanguageChangeHelper().changeLanguage(context, "el")
                activity?.let{
                    restartActivity(it)
                }
            }
        )

        LanguageButton(
            emoji = "🇩🇪",
            isSelected = currentLanguageCode == "de",
            onClick = {
                LanguageChangeHelper().changeLanguage(context, "de")
                activity?.let{
                    restartActivity(it)
                }
            }
        )

        LanguageButton(
            emoji = "🇪🇸",
            isSelected = currentLanguageCode == "es",
            onClick = {
                LanguageChangeHelper().changeLanguage(context, "es")
                activity?.let{
                    restartActivity(it)
                }
            }
        )
    }
}

@Composable
private fun LanguageButton(
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.height(32.dp),
        contentPadding = PaddingValues(4.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onBackground
            }
        )
    ) {
        Text(
            text = emoji,
            fontSize = 16.sp,
            modifier = Modifier.alpha(if (isSelected) 1f else 0.6f)
        )
    }
}


@Composable
fun Logo() {
    Text(
        text = "SUDOKU",
        fontSize = 64.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.displayLarge.copy(
            letterSpacing = 4.sp
        )
    )
}

@Composable
fun ButtonGroup(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onNewGameClick: () -> Unit
) {
    val buttonShape = RoundedCornerShape(12.dp)
    val buttonModifier = Modifier
        .fillMaxWidth()
        .height(56.dp)
        .clip(buttonShape)

    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ElevatedButton(
            onClick = onNewGameClick,
            modifier = buttonModifier,
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = stringResource(R.string.new_game),
                style = MaterialTheme.typography.titleMedium
            )
        }

        OutlinedButton(
            onClick = {
                navController.navigate("load")
            },
            modifier = buttonModifier,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = stringResource(R.string.load_game),
                style = MaterialTheme.typography.titleMedium
            )
        }

        TextButton(
            onClick = { navController.navigate("rules") },
            modifier = buttonModifier,
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = stringResource(R.string.sudoku_rules),
                style = MaterialTheme.typography.titleMedium
            )
        }

        TextButton(
            onClick = { navController.navigate("about") },
            modifier = buttonModifier,
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = stringResource(R.string.about),
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        FilledTonalButton(
            onClick = {
                if (context is Activity) {
                    context.finish()
                }
            },
            modifier = buttonModifier,
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Text(
                text = stringResource(R.string.exit),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun DifficultySelectionDialog(
    onDismiss: () -> Unit,
    onDifficultySelected: (Difficulty) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.select_difficulty),
                color = colors.onBackground,
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DifficultyButton(stringResource(R.string.easy), Difficulty.EASY, onDifficultySelected)
                DifficultyButton(stringResource(R.string.medium), Difficulty.MEDIUM, onDifficultySelected)
                DifficultyButton(stringResource(R.string.hard), Difficulty.HARD, onDifficultySelected)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = colors.primary)
            }
        }
    )
}

@Composable
fun DifficultyButton(
    label: String,
    difficulty: Difficulty,
    onDifficultySelected: (Difficulty) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Button(
        onClick = { onDifficultySelected(difficulty) },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.primary,
            contentColor = colors.onPrimary
        )
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

private fun restartActivity(activity: Activity) {
    val intent = activity.intent
    activity.finish()
    activity.startActivity(intent)
    //activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
}