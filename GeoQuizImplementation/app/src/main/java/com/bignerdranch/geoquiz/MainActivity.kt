package com.bignerdranch.geoquiz

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.bignerdranch.geoquiz.ui.theme.GeoQuizTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val questionBank = listOf(
        Question(R.string.question_1, true),
        Question(R.string.question_2, false),
        Question(R.string.question_3, false),
        Question(R.string.question_4, true)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GeoQuizTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        questionBank
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier, questionBank: List<Question>) {
    var showToast by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var questionIndex by remember { mutableIntStateOf(0) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(questionBank[questionIndex].textResId),
            modifier = modifier.clickable(
                onClick = {
                    questionIndex = (questionIndex + 1) % questionBank.size
                }
            )
        )
        Row {
            Button(onClick = {
                showToast = true
                message = if (checkAnswer(questionBank[questionIndex], true)) {
                    "Well Done"
                } else {
                    "Try Again"
                }
            }) {
                Text("TRUE")
            }
            Button(onClick = {
                message = if (checkAnswer(questionBank[questionIndex], false)) {
                    "Well Done"
                } else {
                    "Try Again"
                }
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = message,
                        duration = SnackbarDuration.Short // Ensures auto-dismissal
                    )
                }
            }) {
                Text("FALSE")
            }
        }

        Row {
            Button(
                onClick = {
                    questionIndex =
                        if (questionIndex == 0) questionBank.size - 1 else (questionIndex - 1)
                }
            ) {
                Row {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Prev_Icon",
                        tint = Color.Black
                    )
                    Text("Prev")
                }
            }

            Button(
                onClick = {
                    questionIndex = (questionIndex + 1) % questionBank.size
                }
            ) {
                Row {
                    Text("Next")
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next_Icon",
                        tint = Color.Black
                    )
                }
            }
        }

        if (showToast) {
            val context = LocalContext.current
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_SHORT
            ).show()
            showToast = false
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

private fun checkAnswer(question: Question, answer: Boolean): Boolean {
    return question.answer == answer
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GeoQuizTheme {
        MainScreen(Modifier, emptyList())
    }
}