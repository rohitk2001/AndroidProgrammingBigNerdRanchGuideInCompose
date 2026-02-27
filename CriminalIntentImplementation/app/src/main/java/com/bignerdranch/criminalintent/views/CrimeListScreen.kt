package com.bignerdranch.criminalintent.views

import android.text.format.DateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bignerdranch.criminalintent.data.Crime
import com.bignerdranch.criminalintent.viewmodel.CrimeListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrimeListScreen(
    onCrimeClick: (String) -> Unit = {},
    showNewCrime: () -> Unit = {},
    onDeleteCrime: (Crime) -> Unit,
    viewModel: CrimeListViewModel = viewModel()
) {
    val crimes by viewModel.crimes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Crimes")
                },
                actions = {
                    IconButton(onClick = {
                        showNewCrime()
                    }) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Crime")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (crimes.isEmpty()) {
            Column(
                modifier = Modifier.padding(innerPadding).fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = { showNewCrime() }) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Crime")
                    Text("No Crimes Exist! Please report if any")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding)
            ) {
                items(items = crimes, key = { crime -> crime.id }) { crime ->
                    CrimeListItem(
                        crime = crime,
                        onCrimeClick = onCrimeClick,
                        onDeleteCrime = onDeleteCrime
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrimeListItem(crime: Crime, onCrimeClick: (String) -> Unit, onDeleteCrime: (Crime) -> Unit) {
    Row(
        modifier = Modifier
            .clickable {
                onCrimeClick.invoke(crime.id)
            }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = crime.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                text = DateFormat.format("EEEE, MMM dd, yyyy", crime.date).toString()
            )
        }

        if (!crime.isSolved) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                tint = Color.Gray,
                contentDescription = "Arrow_Icon"
            )
        } else {
            IconButton(onClick = {
                onDeleteCrime.invoke(crime)
            }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    tint = Color.Gray,
                    contentDescription = "Delete_Icon"
                )
            }
        }
    }
}

@Preview
@Composable
private fun CrimeListPreview() {
    CrimeListScreen(
        onCrimeClick = {},
        showNewCrime = {},
        onDeleteCrime = {},
        viewModel = viewModel()
    )
}
