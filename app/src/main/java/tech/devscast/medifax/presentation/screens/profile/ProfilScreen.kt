package tech.devscast.medifax.presentation.screens.profile

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import tech.devscast.medifax.R
import tech.devscast.medifax.presentation.components.ProgressLoader
import tech.devscast.medifax.presentation.navigation.BottomNavigationBar
import tech.devscast.medifax.presentation.navigation.Destination
import tech.devscast.medifax.presentation.screens.profile.components.ListItem
import tech.devscast.medifax.presentation.screens.profile.components.StatItem
import tech.devscast.medifax.presentation.theme.MedifaxTheme
import tech.devscast.medifax.presentation.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController = rememberNavController(),
    viewModel: ProfileViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = viewModel) {
        viewModel.fetchCurrentUser()
    }

    val uiState = viewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mon Profil") },
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
                .fillMaxSize()
        ) {
            when {
                !uiState.isLoggedIn -> { navController.navigate(Destination.SignIn.route) }
                uiState.isLoading -> { ProgressLoader() }
                uiState.patient != null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .size(100.dp)
                                .border(BorderStroke(5.dp, MaterialTheme.colorScheme.primary), CircleShape)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context = LocalContext.current)
                                    .data("https://medifax.devscast.tech/uploads/${uiState.patient.profileImage}")
                                    .crossfade(true)
                                    .build(),
                                contentDescription = uiState.patient.fullName,
                                placeholder = painterResource(id = R.drawable.doctor_svgrepo_com),
                                error = painterResource(id = R.drawable.doctor_svgrepo_com),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(100.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(uiState.patient.fullName, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem("Heart rate", "215bpm", Icons.Default.HeartBroken)
                VerticalDivider(
                    thickness = 1.dp,
                    color = Color.LightGray,
                    modifier = Modifier.height(50.dp)
                )
                StatItem("Calories", "756cal", Icons.Default.LocalFireDepartment)
                VerticalDivider(
                    thickness = 1.dp,
                    color = Color.LightGray,
                    modifier = Modifier.height(50.dp)
                )
                StatItem("Weight", "103lbs", Icons.Default.SportsGymnastics)
            }

            Spacer(modifier = Modifier.height(64.dp))
            Column {
                val context = LocalContext.current

                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                ListItem(
                    text = "Appointment",
                    icon = Icons.Default.CalendarMonth,
                    onClick = { navController.navigate(Destination.Appointment.route) }
                )
                ListItem(
                    text = "Payment Method",
                    icon = Icons.Default.Payment,
                    onClick = { Toast.makeText(context, "Indisponible", Toast.LENGTH_SHORT).show() }
                )
                ListItem(
                    text = "Logout",
                    icon = Icons.AutoMirrored.Filled.Logout,
                    onClick = {
                        viewModel.logout()
                        navController.navigate(Destination.Logout.route)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_8_pro")
@Composable
fun PreviewUserProfileScreen() {
    MedifaxTheme {
        ProfileScreen()
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_8_pro")
@Composable
fun PreviewProfileScreenDark() {
    MedifaxTheme(darkTheme = true) {
        Surface {
            ProfileScreen()
        }
    }
}