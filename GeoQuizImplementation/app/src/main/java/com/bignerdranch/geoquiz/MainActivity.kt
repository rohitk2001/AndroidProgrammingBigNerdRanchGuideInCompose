package com.bignerdranch.geoquiz


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.bignerdranch.geoquiz.ui.theme.GeoQuizTheme
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {

    private val quizViewModel: QuizViewModel by viewModels()

    // Activity Results API provides registerForActivityResult
    private val cheatLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            quizViewModel.isCheater =
                result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        enableEdgeToEdge()
        setContent {
            GeoQuizTheme {
                val currentIndex by quizViewModel.getCurrentIndexFlow().collectAsState()
                var score by rememberSaveable { mutableIntStateOf(0) }
                val scope = rememberCoroutineScope()
                val snackbarHostState = remember { SnackbarHostState() }

                val judgementToast = stringResource(R.string.judgement_toast)
                val correctToast = stringResource(R.string.correct_toast)
                val incorrectToast = stringResource(R.string.incorrect_toast)

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        currentIndex = currentIndex,
                        questionTextResId = quizViewModel.currentQuestionText,
                        onAnswerClick = { userAnswer ->
                            val isCorrect =
                                checkAnswer(quizViewModel.currentQuestionAnswer, userAnswer)
                            if (isCorrect) {
                                score++
                            }
                            val message = when {
                                quizViewModel.isCheater -> judgementToast
                                isCorrect -> correctToast
                                else -> incorrectToast
                            }
                            scope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        },
                        onNextClick = {
                            if (currentIndex == 3) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Your score is: $score")
                                    score = 0
                                }
                            }
                            quizViewModel.moveToNext()
                        },
                        onPrevClick = { quizViewModel.moveToPrev() },
                        onCheatClick = {
                            val intent =
                                CheatActivity.newIntent(this, quizViewModel.currentQuestionAnswer)
                            cheatLauncher.launch(intent)
                        }
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
fun MainScreen(
    modifier: Modifier = Modifier,
    currentIndex: Int,
    questionTextResId: Int,
    onAnswerClick: (Boolean) -> Unit,
    onNextClick: () -> Unit,
    onPrevClick: () -> Unit,
    onCheatClick: () -> Unit
) {
    var areAnswerButtonsEnabled by rememberSaveable { mutableStateOf(true) }
    LaunchedEffect(currentIndex) {
        areAnswerButtonsEnabled = true
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(questionTextResId),
            modifier = modifier.clickable(
                onClick = {
                    onNextClick.invoke()
                }
            )
        )
        Row {
            Button(
                enabled = areAnswerButtonsEnabled,
                onClick = {
                    areAnswerButtonsEnabled = false
                    onAnswerClick(true)
                }
            ) {
                Text("TRUE")
            }
            Button(
                enabled = areAnswerButtonsEnabled,
                onClick = {
                    areAnswerButtonsEnabled = false
                    onAnswerClick(false)
                }
            ) {
                Text("FALSE")
            }
        }

        Button(
            onClick = {
                onCheatClick.invoke()
            }
        ) {
            Text("Cheat!")
        }

        Row {
            Button(
                onClick = {
                    areAnswerButtonsEnabled = true
                    onPrevClick.invoke()
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
                    areAnswerButtonsEnabled = true
                    onNextClick.invoke()
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
    }
}

private fun checkAnswer(question: Boolean, answer: Boolean): Boolean {
    return question == answer
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GeoQuizTheme {
        MainScreen(
            currentIndex = 0,
            questionTextResId = R.string.question_1,
            onAnswerClick = {},
            onNextClick = {},
            onPrevClick = {},
            onCheatClick = {}
        )
    }
}