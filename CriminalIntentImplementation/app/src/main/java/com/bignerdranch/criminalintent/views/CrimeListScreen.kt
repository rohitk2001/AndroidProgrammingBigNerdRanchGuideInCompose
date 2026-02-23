package com.bignerdranch.criminalintent.views

import android.text.format.DateFormat
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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

@Composable
fun CrimeListScreen(
    onCrimeClick: (String) -> Unit = {},
    viewModel: CrimeListViewModel = viewModel()
) {
    val crimes by viewModel.crimes.collectAsState()

    LazyColumn(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        items(items = crimes, key = { crime -> crime.id }) { crime ->
            CrimeListItem(crime = crime, onCrimeClick = onCrimeClick)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrimeListItem(crime: Crime, onCrimeClick: (String) -> Unit) {
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

        if (crime.isSolved) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                tint = Color.Gray,
                contentDescription = "Arrow_Icon"
            )
        }
    }
}

@Preview
@Composable
private fun CrimeListPreview() {
    CrimeListScreen()
}
