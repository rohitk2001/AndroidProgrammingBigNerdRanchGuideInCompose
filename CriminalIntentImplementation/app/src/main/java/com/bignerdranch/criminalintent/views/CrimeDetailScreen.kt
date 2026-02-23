package com.bignerdranch.criminalintent.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bignerdranch.criminalintent.R
import com.bignerdranch.criminalintent.data.Crime
import com.bignerdranch.criminalintent.viewmodel.CrimeDetailViewModel

@Composable
fun CrimeDetailScreen(
    crimeId: String?,
    viewModel: CrimeDetailViewModel = viewModel(
        factory = CrimeDetailViewModelFactory(crimeId)
    )
) {
    val crime by viewModel.crime.collectAsState()
    crime?.let {
        CrimeDetailContent(crime = it, onTitleChange = { newTitle ->
            viewModel.updateTitle(newTitle)
        }, onSolvedChange = { isSolved ->
            viewModel.updateIsSolved(isSolved)
        })
    }
}

class CrimeDetailViewModelFactory(
    private val crimeId: String?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CrimeDetailViewModel::class.java) && crimeId != null) {
            return CrimeDetailViewModel(crimeId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class or null crimeId")
    }
}

@Composable
fun CrimeDetailContent(
    crime: Crime, onTitleChange: (String) -> Unit,
    onSolvedChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = stringResource(R.string.crime_title_label))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = crime.title ?: stringResource(R.string.crime_title_hint),
            onValueChange = { text ->
                onTitleChange(text)
            }
        )
        Text(text = stringResource(R.string.crime_details_label))
        Button(enabled = false, onClick = {}) {
            Text(modifier = Modifier.fillMaxWidth(), text = crime.date.toString())
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = crime.isSolved,
                onCheckedChange = { isChecked ->
                    onSolvedChange(isChecked)
                }
            )
            Text(text = stringResource(R.string.crime_solved_label))
        }
    }
}

@Preview
@Composable
private fun CrimeDetailPreview() {
    Column(modifier = Modifier.fillMaxSize()) {
        CrimeDetailScreen(
            crimeId = "1"
        )
    }
}