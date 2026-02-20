package com.bignerdranch.criminalintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import java.util.Date
import java.util.UUID

class CrimeDetailFragment : Fragment() {
    private lateinit var crime: Crime

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime(
            id = UUID.randomUUID(),
            title = "",
            date = Date(),
            isSolved = false
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                CrimeDetail(
                    crime,
                    onCheckBoxClick = {
                        crime = crime.copy(isSolved = it)
                    }
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //binding = null
    }

}

@Composable
fun CrimeDetail(crime: Crime, onCheckBoxClick: (Boolean) -> Unit) {
    Column(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val titleString = stringResource(R.string.crime_title_label)
        var title by rememberSaveable { mutableStateOf(titleString) }
        var date by rememberSaveable { mutableStateOf(crime.date) }

        Text(text = title)
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = stringResource(R.string.crime_title_hint),
            onValueChange = { text ->
                title = text
            }
        )
        Text(text = stringResource(R.string.crime_details_label))
        Button(enabled = false, onClick = {}) {
            Text(modifier = Modifier.fillMaxWidth(), text = date.toString())
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = false,
                onCheckedChange = {
                    onCheckBoxClick(it)
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
        CrimeDetail(
            crime = Crime(
                id = TODO(),
                title = TODO(),
                date = TODO(),
                isSolved = TODO()
            ), onCheckBoxClick = {})
    }
}