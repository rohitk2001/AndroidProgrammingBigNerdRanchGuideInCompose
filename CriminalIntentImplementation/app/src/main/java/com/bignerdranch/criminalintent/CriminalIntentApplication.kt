package com.bignerdranch.criminalintent

import android.app.Application
import com.bignerdranch.criminalintent.repository.CrimeRepository

class CriminalIntentApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(this)
    }
}