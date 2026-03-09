package com.bignerdranch.criminalintent.views

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.ContactsContract
import android.text.format.DateFormat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.bignerdranch.criminalintent.R
import com.bignerdranch.criminalintent.data.Crime
import com.bignerdranch.criminalintent.getScaledBitmap
import com.bignerdranch.criminalintent.viewmodel.CrimeDetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Date

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

    val takePhoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { didTakePhoto: Boolean ->
        if (didTakePhoto) {
            viewModel.pendingPhotoFileName?.let { fileName ->
                viewModel.updatePhotoFileName(fileName)
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
        val takePhotoIntent = takePhoto.contract.createIntent(
            context, Uri.EMPTY
        )
        val canShowPhotoButton = canResolveIntent(takePhotoIntent, context)

        CrimeDetailContent(
            crime = it,
            canShowSuspectButton = canShowSuspectButton,
            canShowPhotoButton = canShowPhotoButton,
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
            },
            onCameraClick = {
                val newFileName = "IMG_${System.currentTimeMillis()}.JPG"
                viewModel.pendingPhotoFileName = newFileName // Save to VM

                val photoFile = File(context.filesDir, newFileName)
                val photoUri = FileProvider.getUriForFile(
                    context,
                    "com.bignerdranch.android.criminalintent.fileprovider",
                    photoFile
                )
                takePhoto.launch(photoUri)
            }
        )
    }
}

private fun canResolveIntent(intent: Intent, context: Context): Boolean {
    val packageManager = context.packageManager
    val resolvedActivity = packageManager.resolveActivity(
        intent,
        PackageManager.MATCH_DEFAULT_ONLY
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
    canShowPhotoButton: Boolean = false,
    onTitleChange: (String) -> Unit,
    onSolvedChange: (Boolean) -> Unit,
    onDateChange: (Date) -> Unit,
    onSendReport: () -> Unit,
    onPickSuspect: () -> Unit,
    onCameraClick: () -> Unit
) {
    var showDateDialog by rememberSaveable { mutableStateOf(false) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showZoomedPhoto by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(crime.photoFileName) {
        if (!crime.photoFileName.isNullOrBlank()) {
            val photoFile = File(context.filesDir, crime.photoFileName)
            if (photoFile.exists()) {
                withContext(Dispatchers.IO) {
                    val density = context.resources.displayMetrics.density
                    val sizePx = (80 * density).toInt()
                    val scaledBitmap = getScaledBitmap(photoFile.path, sizePx, sizePx)
                    bitmap = scaledBitmap
                }
            }
        } else {
            bitmap = null
        }
    }

    Column(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row {
            Column {
                if (bitmap != null) {
                    AsyncImage(
                        // If photoFileName is null/empty, Coil handles it gracefully
                        model = if (crime.photoFileName.isNullOrBlank()) {
                            null
                        } else {
                            File(LocalContext.current.filesDir, crime.photoFileName)
                        },
                        contentDescription = "Crime photo",
                        modifier = Modifier
                            .size(80.dp)
                            .padding(4.dp)
                            .clickable(onClick = {
                                showZoomedPhoto = true
                            }),
                        contentScale = ContentScale.Crop,
                        // Replace with your actual drawable resource name
                        error = ColorPainter(Color.LightGray),
                        fallback = ColorPainter(Color.LightGray)
                    )
                    IconButton(enabled = canShowPhotoButton, onClick = onCameraClick) {
                        Icon(Icons.Outlined.AddCircle, contentDescription = "Camera")
                    }
                } else {
                    Icon(Icons.Outlined.AddCircle, contentDescription = null)
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
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
            }
        }
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

    if (showZoomedPhoto && !crime.photoFileName.isNullOrBlank()) {
        Dialog(onDismissRequest = { showZoomedPhoto = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .clickable {
                        showZoomedPhoto = false
                    }

            ) {
                AsyncImage(
                    model = File(context.filesDir, crime.photoFileName),
                    contentDescription = "Zoomed crime photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f), // Adjust aspect ratio as needed
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Preview
@Composable
private fun CrimeDetailPreview() {
    Column(modifier = Modifier.fillMaxSize()) {
        CrimeDetailContent(
            crime = Crime(
                id = "2",
                title = "Sample Crime",
                date = Date(),
                isSolved = false,
                suspect = ""
            ),
            canShowSuspectButton = true,
            onTitleChange = {},
            onSolvedChange = {},
            onDateChange = {},
            onSendReport = {},
            onPickSuspect = {},
            onCameraClick = {}
        )
    }
}