package com.bignerdranch.geoquiz

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.bignerdranch.geoquiz.ui.theme.GeoQuizTheme
import androidx.compose.runtime.LaunchedEffect

private const val EXTRA_ANSWER_IS_TRUE = "com.bignerdranch.android.geoquiz.answer_is_true"
private var answerIsTrue = false
const val EXTRA_ANSWER_SHOWN = "com.bignerdranch.android.geoquiz.answer_shown"

class CheatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)
        enableEdgeToEdge()
        setContent {
            GeoQuizTheme {
                // This survives rotation
                var hasCheated by rememberSaveable { mutableStateOf(false) }

                // Send result whenever hasCheated changes
                LaunchedEffect(hasCheated) {
                    setAnswerShownResult(hasCheated)
                }

                CheatScreen(answerIsTrue, handleCheatButtonClick = {
                   hasCheated = true
                })
            }
        }
    }

    companion object {
        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }

    private fun setAnswerShownResult(hasCheated: Boolean) {
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, hasCheated)
        }
        setResult(RESULT_OK, data)
    }
}

@Composable
fun CheatScreen(answerIsTrue: Boolean, handleCheatButtonClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var showAnswerText by rememberSaveable { mutableStateOf(false) }
        Text(text = stringResource(R.string.warning_text))
        if (showAnswerText) {
            Text(text = if (answerIsTrue) "TRUE" else "FALSE")
        }
        Button(onClick = {
            showAnswerText = true
            handleCheatButtonClick.invoke()
        }) {
            Text(text = stringResource(R.string.cheat_button))
        }
    }
}

@Preview
@Composable
fun CheatScreenPreview() {
    GeoQuizTheme {
        CheatScreen(true, {})
    }
}