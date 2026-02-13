package com.bignerdranch.geoquiz

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.SavedStateHandle
import com.bignerdranch.geoquiz.ui.theme.GeoQuizTheme
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {

    private val quizViewModel: QuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        enableEdgeToEdge()
        setContent {
            GeoQuizTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        quizViewModel
                    )
                }
            }
        }
        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

}

@Composable
fun MainScreen(modifier: Modifier = Modifier, quizViewModel: QuizViewModel) {
    var showToast by rememberSaveable { mutableStateOf(false) }
    var message by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(true) }
    var score by rememberSaveable { mutableIntStateOf(0) }
    var showScore by rememberSaveable { mutableStateOf(false) }
    val currentIndex by quizViewModel.getCurrentIndexFlow().collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(quizViewModel.currentQuestionText),
            modifier = modifier.clickable(
                onClick = {
                    quizViewModel.moveToNext()
                }
            )
        )
        Row {
            Button(
                enabled = active,
                onClick = {
                    active = false
                    showToast = true
                    score = if (checkAnswer(quizViewModel.currentQuestionAnswer, true)) {
                        score + 1
                    } else {
                        score
                    }
                    message = if (checkAnswer(quizViewModel.currentQuestionAnswer, true)) {
                        "Well Done"
                    } else {
                        "Try Again"
                    }
                }
            ) {
                Text("TRUE")
            }
            Button(
                enabled = active,
                onClick = {
                    active = false
                    score = if (checkAnswer(quizViewModel.currentQuestionAnswer, false)) {
                        score + 1
                    } else {
                        score
                    }
                    message = if (checkAnswer(quizViewModel.currentQuestionAnswer, false)) {
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
                }
            ) {
                Text("FALSE")
            }
        }

        Row {
            Button(
                onClick = {
                    active = true
                    quizViewModel.moveToPrev()
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
                    active = true
                    showScore = currentIndex == 3
                    quizViewModel.moveToNext()
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

        if (showScore) {
            val context = LocalContext.current
            Toast.makeText(
                context,
                "Your score is : $score",
                Toast.LENGTH_SHORT
            ).show()
            score = 0
            showScore = false
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

private fun checkAnswer(question: Boolean, answer: Boolean): Boolean {
    return question == answer
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GeoQuizTheme {
        MainScreen(Modifier, quizViewModel = QuizViewModel(savedStateHandle = SavedStateHandle()))
    }
}