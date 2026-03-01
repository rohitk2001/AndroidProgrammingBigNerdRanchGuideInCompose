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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import java.util.Date
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.text.format.DateFormat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import android.provider.ContactsContract

@Composable
fun CrimeDetailScreen(
    crimeId: String?,
    viewModel: CrimeDetailViewModel = viewModel(
        factory = CrimeDetailViewModelFactory(crimeId)
    )
) {
    val crime by viewModel.crime.collectAsState()
    val context = LocalContext.current

    val pickContactLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) { contactUri: Uri? ->
        contactUri?.let {
            // Query the contact name from the returned URI
            val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
            val cursor = context.contentResolver.query(contactUri, queryFields, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val suspect = it.getString(0)
                    // 2. Update the ViewModel with the selected name
                    viewModel.updateSuspect(suspect)
                }
            }
        }
    }

    crime?.let {
        val solvedString = if (it.isSolved) {
            stringResource(R.string.crime_report_solved)
        } else {
            stringResource(R.string.crime_report_unsolved)
        }
        val dateString = DateFormat.format("EEEE, MMM dd", it.date).toString()
        val suspectText = if (it.suspect.isBlank()) {
            stringResource(R.string.crime_report_no_suspect)
        } else {
            stringResource(R.string.crime_report_suspect, it.suspect)
        }
        val report = stringResource(
            R.string.crime_report,
            it.title, dateString, solvedString, suspectText
        )
        val subject = stringResource(R.string.crime_report_subject)

        val selectSuspectIntent = pickContactLauncher.contract.createIntent(
            context, null
        )
        val canShowSuspectButton = canResolveIntent(selectSuspectIntent, context)

        CrimeDetailContent(
            crime = it,
            canShowSuspectButton = canShowSuspectButton,
            onTitleChange = { newTitle ->
                viewModel.updateTitle(newTitle)
            },
            onSolvedChange = { isSolved ->
                viewModel.updateIsSolved(isSolved)
            },
            onDateChange = { date ->
                viewModel.updateDate(date)
            },
            onSendReport = {
                val reportIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, report)
                    putExtra(
                        Intent.EXTRA_SUBJECT,
                        subject
                    )
                }
                val chooserIntent = Intent.createChooser(
                    reportIntent,
                    context.getString(R.string.send_report)
                )
                context.startActivity(chooserIntent)
            },
            onPickSuspect = {
                pickContactLauncher.launch(null)
            }
        )
    }
}

private fun canResolveIntent(intent: Intent, context: android.content.Context): Boolean {
    val packageManager = context.packageManager
    val resolvedActivity = packageManager.resolveActivity(
        intent,
        android.content.pm.PackageManager.MATCH_DEFAULT_ONLY
    )
    return resolvedActivity != null
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrimeDetailContent(
    crime: Crime,
    canShowSuspectButton: Boolean = false,
    onTitleChange: (String) -> Unit,
    onSolvedChange: (Boolean) -> Unit,
    onDateChange: (Date) -> Unit,
    onSendReport: () -> Unit,
    onPickSuspect: () -> Unit
) {
    var showDateDialog by rememberSaveable { mutableStateOf(false) }

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
            value = crime.title,
            onValueChange = { text ->
                onTitleChange(text)
            },
            placeholder = { Text(stringResource(R.string.crime_title_hint)) }
        )
        Text(text = stringResource(R.string.crime_details_label))
        Button(enabled = true, onClick = {
            showDateDialog = true
        }) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = DateFormat.format("EEEE, MMM dd, yyyy", crime.date).toString()
            )
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

        Button(enabled = canShowSuspectButton, onClick = {
            onPickSuspect.invoke()
        }) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "CHOOSE SUSPECT".takeIf { crime.suspect.isEmpty() } ?: crime.suspect)
        }

        Button(enabled = true, onClick = {
            onSendReport.invoke()
        }) {
            Text(modifier = Modifier.fillMaxWidth(), text = "SEND CRIME REPORT")
        }

        if (showDateDialog) {
            val datePickerState =
                rememberDatePickerState(initialSelectedDateMillis = crime.date.time)
            DatePickerDialog(
                onDismissRequest = { showDateDialog = false },
                confirmButton = {
                    Button(
                        onClick = {
                            onDateChange(datePickerState.selectedDateMillis?.let { Date(it) }
                                ?: Date())
                            showDateDialog = false
                        }
                    ) {
                        Text(text = "Confirm")
                    }
                },
                modifier = Modifier,
                dismissButton = {
                    Button(onClick = { showDateDialog = false }) {
                        Text(text = "Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
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