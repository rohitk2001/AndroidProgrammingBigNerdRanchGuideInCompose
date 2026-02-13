package com.bignerdranch.geoquiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

const val CURRENT_INDEX_KEY = "CURRENT_INDEX_KEY"

class QuizViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val questionBank = listOf(
        Question(R.string.question_1, true),
        Question(R.string.question_2, false),
        Question(R.string.question_3, false),
        Question(R.string.question_4, true)
    )

    // Use StateFlow from savedStateHandle to automatically save/restore the index
    private var currentIndexStateFlow: StateFlow<Int> = savedStateHandle.getStateFlow(CURRENT_INDEX_KEY, 0)

    private var currentIndex: Int
        get() = currentIndexStateFlow.value
        set(value) {
            savedStateHandle[CURRENT_INDEX_KEY] = value
        }

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun moveToPrev() {
        currentIndex = if (currentIndex == 0) questionBank.size - 1 else (currentIndex - 1)
    }

    fun getCurrentIndexFlow(): StateFlow<Int> = savedStateHandle.getStateFlow(CURRENT_INDEX_KEY, 0)
}