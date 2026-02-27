package com.bignerdranch.criminalintent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bignerdranch.criminalintent.data.Crime
import com.bignerdranch.criminalintent.navigation.CrimeIntentDestinations
import com.bignerdranch.criminalintent.ui.theme.CriminalIntentTheme
import com.bignerdranch.criminalintent.viewmodel.CrimeListViewModel
import com.bignerdranch.criminalintent.views.CrimeDetailScreen
import com.bignerdranch.criminalintent.views.CrimeListScreen
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CriminalIntentTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val coroutineScope = rememberCoroutineScope()

                    NavHost(
                        navController = navController,
                        startDestination = CrimeIntentDestinations.CRIME_LIST_ROUTE
                    ) {
                        composable(CrimeIntentDestinations.CRIME_LIST_ROUTE) {
                            val listViewModel: CrimeListViewModel = viewModel()
                            CrimeListScreen(
                                onCrimeClick = { crimeId ->
                                    navController.navigate(
                                        "${CrimeIntentDestinations.CRIME_DETAIL_ROUTE}/${crimeId}"
                                    )
                                },
                                showNewCrime = {
                                    coroutineScope.launch {
                                        val newCrime = Crime(
                                            id = UUID.randomUUID().toString(),
                                            title = "New Crime",
                                            date = Date(),
                                            isSolved = false,
                                        )
                                        listViewModel.addCrime(newCrime)
                                        navController.navigate("${CrimeIntentDestinations.CRIME_DETAIL_ROUTE}/${newCrime.id}")
                                    }
                                },
                                onDeleteCrime = { crime ->
                                    coroutineScope.launch {
                                        listViewModel.deleteCrime(crime)
                                    }
                                }
                            )
                        }

                        composable(
                            route = "${CrimeIntentDestinations.CRIME_DETAIL_ROUTE}/{${CrimeIntentDestinations.CRIME_ID_ARG}}",
                            arguments = listOf(
                                navArgument(CrimeIntentDestinations.CRIME_ID_ARG) {
                                    type = NavType.StringType
                                }
                            )
                        ) { backStackEntry ->
                            val crimeId =
                                backStackEntry.arguments?.getString(CrimeIntentDestinations.CRIME_ID_ARG)
                            CrimeDetailScreen(
                                crimeId = crimeId
                            )
                        }
                    }
                }
            }
        }
    }
}