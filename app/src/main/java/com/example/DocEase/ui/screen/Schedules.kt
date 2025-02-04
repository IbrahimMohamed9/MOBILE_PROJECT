package com.example.DocEase.ui.screen

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.DocEase.R
import com.example.DocEase.model.enums.Disease
import com.example.DocEase.model.models.Schedules
import com.example.DocEase.ui.screen.navigation.DocBottomNavBar
import com.example.DocEase.ui.screen.navigation.NavigationDestination
import com.example.DocEase.ui.viewModel.AppViewModelProvider
import com.example.DocEase.ui.viewModel.screens.SchedulesViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

object SchedulesDestination : NavigationDestination {
    override val route = "schedule"
    override val title = "schedules"
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SchedulesScreenNavigation(
    navigateToProfile: () -> Unit,
    navigateToPatients: () -> Unit,
    navigateToSchedule: (Int) -> Unit
) {
    Scaffold(
        floatingActionButton = { FloatingActionButtonFun() },
        content = { SchedulesScreen(navigateToSchedule) },
        bottomBar = {
            DocBottomNavBar(
                navigateToProfile, { }, navigateToPatients
            )
        },
    )
}


@SuppressLint("SimpleDateFormat")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulesScreen(
    navigateToSchedule: (Int) -> Unit,
    viewModel: SchedulesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val tomorrow = LocalDate.now().plusDays(1)
    val state = rememberDatePickerState()
    var openCalendar by remember { mutableStateOf(false) }
    var showDate by remember { mutableStateOf(false) }
    var scheduleDate by remember { mutableStateOf("${tomorrow.month} ${tomorrow.dayOfMonth}, ${tomorrow.year} Schedules") }
    var scheduleDateSearch by remember { mutableStateOf("${tomorrow.dayOfMonth}-${tomorrow.monthValue}-${tomorrow.year}") }

    val todaySchedulesUiStates by viewModel.TodaySchedulesUiStates.collectAsState()
    val schedulesByDateUiStates by viewModel.getSchedulesByDate(scheduleDateSearch).collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .padding(bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.DateRange,
            contentDescription = "calender Icon",
            modifier = Modifier
                .clickable { openCalendar = true }
                .align(Alignment.Start))
        Spacer(modifier = Modifier.height(10.dp))
        if (openCalendar) {
            DatePickerDialog(onDismissRequest = { openCalendar = false }, confirmButton = {
                Button(onClick = { showDate = true; openCalendar = false }) {
                    Text(text = "Confirm")
                }
            }) {
                DatePicker(
                    state = state
                )
            }
        }

        Text(text = "Today Schedules", fontSize = 20.sp, modifier = Modifier.align(Alignment.Start))
        LazyRow {
            items(todaySchedulesUiStates.scheduleList) { schedule ->
                ScheduleCard(schedule = schedule, navigateToSchedule)
            }
        }
        Divider(modifier = Modifier.padding(15.dp))
        if (showDate) {
            val dateString = state.selectedDateMillis?.let {
                Date(
                    it
                )
            }?.let { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it) }

            val selectedDate = LocalDate.parse(dateString)
            scheduleDate =
                "${selectedDate.month} ${selectedDate.dayOfMonth}, ${selectedDate.year} Schedules"
            scheduleDateSearch =
                "${selectedDate.dayOfMonth}-${selectedDate.monthValue}-${selectedDate.year}"
        }

        Text(
            text = scheduleDate, fontSize = 20.sp, modifier = Modifier.align(Alignment.Start)
        )


        LazyColumn {
            items(schedulesByDateUiStates.scheduleList) { schedule ->
                ScheduleCard(schedule = schedule, navigateToSchedule)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleCard(
    schedule: Schedules,
    navigateToSchedule: (Int) -> Unit,
    viewModel: SchedulesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    viewModel.getPatient(schedule.patientId)
    val patient = viewModel.patientsUiState.patientsDetails

    Card(
        modifier = Modifier
            .padding(5.dp)
            .width(350.dp)
            .height(110.dp)
            .clickable { navigateToSchedule(schedule.scheduleId) }
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.padding(5.dp)
        ) {
            Image(
                painter = painterResource(id = getScheduleImage(schedule.disease)),
                contentDescription = "schedule image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(7.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(text = "${patient.name} ${patient.surname}", fontSize = 18.sp)
                Text(text = schedule.disease.value, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = schedule.description,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "${schedule.price} KM", fontSize = 16.sp)
                    Text(text = schedule.date, fontSize = 16.sp)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FloatingActionButtonFun() {
    var showDialog by remember { mutableStateOf(false) }

    SmallFloatingActionButton(
        onClick = { showDialog = true },
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.secondary
    ) {
        Icon(Icons.Filled.Add, "Small floating action button.")
    }

    if (showDialog) {
        ScheduleDialog(onDismiss = { showDialog = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleDialog(
    onDismiss: () -> Unit,
    viewModel: SchedulesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                var patientId: Int by remember { mutableStateOf(0) }
                var patientIdText by remember { mutableStateOf("") }
                var patientIdError by remember { mutableStateOf(false) }
                var price by remember { mutableStateOf(100) }
                var description by remember { mutableStateOf("") }
                var dropDownItem by remember { mutableStateOf("") }
                var expandedItems by remember { mutableStateOf(false) }

                val tomorrow = LocalDate.now().plusDays(1)
                var date by remember { mutableStateOf("${tomorrow.dayOfMonth}-${tomorrow.monthValue}-${tomorrow.year}") }
                var openCalendar by remember { mutableStateOf(false) }
                val timeState = rememberDatePickerState()

                val coroutineScope = rememberCoroutineScope()
                val uiState = viewModel.schedulesUiState
                val detailsState = uiState.schedulesDetails

                //design values
                val keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                )
                val shape = RoundedCornerShape(10.dp)

                Text(
                    text = "Enter Schedule Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = patientIdText,
                    onValueChange = {
                        patientIdText = it
                        patientId = patientIdText.toIntOrNull() ?: 0
                        viewModel.updateUiState(detailsState.copy(patientId = patientId))
                    },
                    shape = shape,
                    label = { Text("Patient ID") },
                    keyboardOptions = keyboardOptions,
                    isError = patientIdError
                )

                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                ) {
                    TextField(
                        modifier = Modifier
                            .border(width = 1.dp, color = Color.Gray, shape = shape)
                            .fillMaxWidth(),
                        value = dropDownItem,

                        onValueChange = { },
                        readOnly = true,
                        placeholder = {
                            Text(text = "Disease")
                        },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            unfocusedIndicatorColor = Color.White
                        ),
                        trailingIcon = {
                            Icon(Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                modifier = Modifier.clickable { expandedItems = true })
                        },
                    )
                    DropdownMenu(
                        expanded = expandedItems,
                        onDismissRequest = { expandedItems = false },
                        modifier = Modifier.width(280.dp)
                    ) {
                        Disease.entries.map {
                            DropdownMenuItem(text = {
                                Text(text = it.value)
                            }, onClick = {
                                dropDownItem = it.value
                                expandedItems = false
                                viewModel.updateUiState(detailsState.copy(disease = it))
                            }, modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    shape = shape,
                    value = date,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Date Of Schedule") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(
                            onClick = { openCalendar = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                        }
                    },

                    )
                if (openCalendar) {
                    DatePickerDialog(
                        onDismissRequest = { openCalendar = false },
                        confirmButton = {
                            Button(onClick = {
                                openCalendar = false
                                val dateString =
                                    SimpleDateFormat("yyyy-MM-dd").format(timeState.selectedDateMillis)
                                val selectedDate = LocalDate.parse(dateString)
                                date =
                                    "${selectedDate.dayOfMonth}-${selectedDate.monthValue}-${selectedDate.year}"
                                viewModel.updateUiState(detailsState.copy(date = date))
                            }

                            ) {
                                Text(text = "Confirm")
                            }
                        }) {
                        DatePicker(
                            state = timeState
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    shape = shape,
                    value = price.toString(),
                    onValueChange = {
                        price = it.toIntOrNull() ?: 0
                        viewModel.updateUiState(detailsState.copy(price = price))
                    },
                    label = { Text("Price") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = keyboardOptions
                )

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    shape = shape,
                    value = description,
                    onValueChange = {
                        description = it
                        viewModel.updateUiState(detailsState.copy(description = description))
                    },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = { onDismiss() }) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        if (patientIdText.toIntOrNull() == null) {
                            patientIdError = true
                        } else {
                            coroutineScope.launch {
                                //To insure the patientId is existing in the patients table
                                if (viewModel.checkPatientID(detailsState.patientId)) {
                                    viewModel.addSchedule()
                                    onDismiss()
                                }
                            }
                            //if not existing we will show error in patientId field
                            patientIdError = true
                        }
                    }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

fun getScheduleImage(disease: Disease): Int {
    return when (disease) {
        Disease.BACKPAIN -> R.drawable.what_to_do_back_pain_1200x628
        Disease.TEETHPAIN -> R.drawable.toothache_scaled
        Disease.ARMPAIN -> R.drawable.shoulder_pain_495x400
        Disease.LEGPAIN -> R.drawable.sciatica
        Disease.HEADACHE -> R.drawable.headache
        Disease.SORETHROAT -> R.drawable.sore_throat
        Disease.COUGH -> R.drawable.cough
        Disease.STOMACHACHE -> R.drawable.stomachache
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun SchedulesScreenPreview() {
    SchedulesScreen({})
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun SchedulesScreenNavigationPreview() {
    SchedulesScreenNavigation({}, {}, {})
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun ScheduleDialogPreview() {
    ScheduleDialog({})
}
