package com.bignerdranch.geoquiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

const val CURRENT_INDEX_KEY = "CURRENT_INDEX_KEY"
const val IS_CHEATER_KEY = "IS_CHEATER_KEY"

class QuizViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val questionBank = listOf(
        Question(R.string.question_1, true),
        Question(R.string.question_2, false),
        Question(R.string.question_3, false),
        Question(R.string.question_4, true)
    )

    var isCheater: Boolean
        get() = savedStateHandle[IS_CHEATER_KEY] ?: false
        set(value) = savedStateHandle.set(IS_CHEATER_KEY, value)

    val currentIndex: StateFlow<Int> = savedStateHandle.getStateFlow(CURRENT_INDEX_KEY, 0)

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex.value].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex.value].textResId

    fun moveToNext() {
        savedStateHandle[CURRENT_INDEX_KEY] = (currentIndex.value + 1) % questionBank.size
        isCheater = false
    }

    fun moveToPrev() {
        val newIndex = if (currentIndex.value == 0) {
            questionBank.size - 1
        } else {
            currentIndex.value - 1
        }
        savedStateHandle[CURRENT_INDEX_KEY] = newIndex
    }

    fun getCurrentIndexFlow(): StateFlow<Int> = savedStateHandle.getStateFlow(CURRENT_INDEX_KEY, 0)
}