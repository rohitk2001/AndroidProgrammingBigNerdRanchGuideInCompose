package com.bignerdranch.criminalintent.viewmodel

import androidx.compose.animation.core.copy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.criminalintent.data.Crime
import com.bignerdranch.criminalintent.repository.CrimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
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

    fun updateSuspect(suspect: String) {
        _crime.value = _crime.value?.copy(suspect = suspect)
    }

    var pendingPhotoFileName: String? = null

    fun updatePhotoFileName(fileName: String) {
        _crime.update { it?.copy(photoFileName = fileName) }
    }

    override fun onCleared() {
        super.onCleared()
        crime.value?.let {
            crimeRepository.updateCrime(it)
        }
    }
}