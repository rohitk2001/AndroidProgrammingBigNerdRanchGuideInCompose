package com.bignerdranch.criminalintent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.criminalintent.data.Crime
import com.bignerdranch.criminalintent.repository.CrimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class CrimeListViewModel : ViewModel() {

    private val crimeRepository = CrimeRepository.Companion.get()
    private val _crimes = MutableStateFlow<List<Crime>>(emptyList())
    val crimes: StateFlow<List<Crime>> = _crimes.asStateFlow()

    init {
        viewModelScope.launch {
            crimeRepository.getCrimes().collect { crimesFromDb ->
                val listToShow = crimesFromDb

                // Update the UI state
                _crimes.value = listToShow

                // IMPORTANT: Update the repository's cache with whatever list we decided to show
                crimeRepository.updateCache(listToShow)
            }
        }
    }

    fun addCrime(crime: Crime) {
        crimeRepository.addCrime(crime)
    }

    fun deleteCrime(crime: Crime) {
        crimeRepository.deleteCrime(crime)
    }
}