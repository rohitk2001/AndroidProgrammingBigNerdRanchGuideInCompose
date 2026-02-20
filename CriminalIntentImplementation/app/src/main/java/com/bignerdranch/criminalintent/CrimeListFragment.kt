package com.bignerdranch.criminalintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import java.util.Date
import java.util.UUID
import android.text.format.DateFormat

class CrimeListFragment : Fragment() {
    private val crimeListViewModel: CrimeListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                val crimes by crimeListViewModel.crimes.collectAsState()
                CrimeList(crimes = crimes)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}

@Composable
private fun CrimeList(crimes: List<Crime>) {
    LazyColumn(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        items(items = crimes, key = { crime -> crime.id }) { crime ->
            CrimeListItem(crime = crime)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrimeListItem(crime: Crime) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .clickable { showDialog = true }
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

        if (crime.isSolved) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                tint = Color.Gray,
                contentDescription = "Arrow_Icon"
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Crime Clicked") },
            text = { Text(text = "You clicked on ${crime.title}.") },
            confirmButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@Preview
@Composable
private fun CrimeListPreview() {
    CrimeList(
        crimes = listOf(
            Crime(id = UUID.randomUUID(), title = "Crime #1", date = Date(), isSolved = false),
            Crime(id = UUID.randomUUID(), title = "Crime #2", date = Date(), isSolved = false),
            Crime(id = UUID.randomUUID(), title = "Crime #3", date = Date(), isSolved = false),
            Crime(id = UUID.randomUUID(), title = "Crime #4", date = Date(), isSolved = false)
        )
    )
}