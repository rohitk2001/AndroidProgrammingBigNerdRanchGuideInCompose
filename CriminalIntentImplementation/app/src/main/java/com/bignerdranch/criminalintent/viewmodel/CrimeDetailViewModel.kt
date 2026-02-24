package com.bignerdranch.criminalintent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.criminalintent.data.Crime
import com.bignerdranch.criminalintent.repository.CrimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class CrimeDetailViewModel(crimeId: String) : ViewModel() {
    private val crimeRepository = CrimeRepository.Companion.get()

    private val _crime = MutableStateFlow<Crime?>(null)
    val crime = _crime.asStateFlow()

    init {
        viewModelScope.launch {
            _crime.value = crimeRepository.getCrime(crimeId)
        }
    }

    fun updateTitle(title: String) {
        _crime.value = _crime.value?.copy(title = title)
    }

    fun updateIsSolved(solved: Boolean) {
        _crime.value = _crime.value?.copy(isSolved = solved)
    }

    fun updateDate(date: Date) {
        _crime.value = _crime.value?.copy(date = date)
    }

    override fun onCleared() {
        super.onCleared()
        crime.value?.let {
            crimeRepository.updateCrime(it)
        }
    }
}