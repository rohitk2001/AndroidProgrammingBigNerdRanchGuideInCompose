package com.bignerdranch.criminalintent.repository

import android.content.Context
import androidx.room.Room
import com.bignerdranch.criminalintent.data.Crime
import com.bignerdranch.criminalintent.data.database.CrimeDatabase
import com.bignerdranch.criminalintent.data.database.migrationFromTwoToThree
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CrimeRepository @OptIn(DelicateCoroutinesApi::class)
private constructor(
    context: Context,
    private val coroutineScope: CoroutineScope = GlobalScope
) {
    // 1. Add an in-memory cache for the crime list
    private var inMemoryCrimes: List<Crime> = emptyList()
    private val database: CrimeDatabase = Room
        .databaseBuilder(
            context.applicationContext,
            CrimeDatabase::class.java,

            "crime-database"
        )
        .addMigrations(migrationFromTwoToThree)
        .build()

    fun getCrimes(): Flow<List<Crime>> = database.crimeDao().getCrimes()

    // 2. A function to populate the cache from the ViewModel
    fun updateCache(crimes: List<Crime>) {
        inMemoryCrimes = crimes
    }

    suspend fun getCrime(id: String): Crime {
        // 3. First, try to find the crime in the fast in-memory cache.
        val cachedCrime = inMemoryCrimes.find { it.id == id }
        if (cachedCrime != null) {
            return cachedCrime
        }

        // 4. If not found in cache, get it from the database (the single source of truth).
        return database.crimeDao().getCrime(id)
    }

    fun updateCrime(crime: Crime) {
        coroutineScope.launch {
            database.crimeDao().updateCrime(crime)
        }
    }

    fun addCrime(crime: Crime) {
        coroutineScope.launch {
            database.crimeDao().addCrime(crime)
        }
    }

    fun deleteCrime(crime: Crime) {
        coroutineScope.launch {
            database.crimeDao().deleteCrime(crime)
        }
    }

    companion object {
        private var INSTANCE: CrimeRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(context)
            }
        }

        fun get(): CrimeRepository {
            return INSTANCE ?: throw IllegalStateException("CrimeRepository must be initialized")
        }
    }
}