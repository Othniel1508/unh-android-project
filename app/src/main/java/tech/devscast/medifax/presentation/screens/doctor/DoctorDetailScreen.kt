package tech.devscast.medifax.presentation.screens.doctor

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import tech.devscast.medifax.presentation.components.EmptyState
import tech.devscast.medifax.presentation.components.ProgressLoader
import tech.devscast.medifax.presentation.navigation.BottomNavigationBar
import tech.devscast.medifax.presentation.screens.doctor.components.DoctorListItem
import tech.devscast.medifax.presentation.screens.profile.components.AppointmentScreen
import tech.devscast.medifax.presentation.screens.profile.components.ConfirmationDialog
import tech.devscast.medifax.presentation.theme.MedifaxTheme
import tech.devscast.medifax.presentation.theme.poppinsFontFamily
import tech.devscast.medifax.presentation.viewmodel.DoctorDetailViewModel
import tech.devscast.validable.NotEmptyValidable
import tech.devscast.validable.withValidable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDetailScreen(
    doctorId: String,
    navController: NavController = rememberNavController(),
    viewModel: DoctorDetailViewModel = hiltViewModel(),

    ) {
    LaunchedEffect(key1 = viewModel) {
        viewModel.fetchDoctor(doctorId)
    }

    val uiState = viewModel.uiState

    var message = remember { NotEmptyValidable() }
    var dateTime = remember { NotEmptyValidable() }

    var showDialogState by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prendre Rendez-vous") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .padding(32.dp)
        ) {

            when {
                uiState.isLoading -> {
                    ProgressLoader()
                }

                uiState.createdAppointment != null -> {

                }

                uiState.errorMessage != null -> {
                    EmptyState(message = "Un problème est survenue")
                    Toast.makeText(LocalContext.current, uiState.errorMessage, Toast.LENGTH_SHORT)
                        .show()
                }

                uiState.doctor != null -> {
                    DoctorListItem(doctor = uiState.doctor) {}
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "A propos",
                        fontFamily = poppinsFontFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Lorem ipsum dolor sit amet, consectetur adipi elit, sed do eiusmod tempor incididunt ut laore et dolore magna aliqua. Ut enim ad minim veniam... Read more",
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.fillMaxWidth()
                    )

                    //Spacer(modifier = Modifier.height(12.dp))
                    Spacer(modifier = Modifier.weight(1f))
                    OutlinedTextField(
                        value = dateTime.value,
                        onValueChange = {
                            dateTime.value = it
                        },
                        label = { Text(text = "Date et Heure") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    // AnimatedVisibility(visible = uiState.doctor.isAvailable) {
                    OutlinedTextField(
                        value = message.value,
                        onValueChange = { if (it.length <= 255) message.value = it },
                        label = { Text(text = "Description") },
                        modifier = Modifier
                            .height(150.dp)
                            .fillMaxWidth(),
                        supportingText = {
                            Text(
                                text = "${message.value.length} / 255",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                        },
                        shape = MaterialTheme.shapes.medium,
                    )
                    // }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            showDialogState = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        enabled = uiState.doctor.isAvailable
                    ) {
                        Text(
                            text = "Prendre un rendez-vous",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
            }
            AppointmentScreen(
                showDialog = {
                    showDialogState = true
                }
            )
            if (showDialogState) {
                ConfirmationDialog(
                    onConfirm = {
                        withValidable {
                            viewModel.createAppointment(doctorId, message.value , dateTime.value)
                        }

                        message = NotEmptyValidable()
                        dateTime = NotEmptyValidable()

                        showDialogState = false
                        navController.popBackStack()
                    },
                    onDismiss = {
                        showDialogState = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_8_pro", showSystemUi = true)
@Composable
fun PreviewAppointmentScreen() {
    MedifaxTheme {
        DoctorDetailScreen("")
    }
}

@Preview(showBackground = true, device = "id:pixel_8_pro", showSystemUi = true)
@Composable
fun PreviewAppointmentScreenDark() {
    MedifaxTheme(darkTheme = true) {
        Surface {
            DoctorDetailScreen("")
        }
    }
}