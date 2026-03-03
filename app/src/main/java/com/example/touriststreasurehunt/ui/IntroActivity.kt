package com.example.touriststreasurehunt.ui

// Important imports
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.touriststreasurehunt.data.ProgressManager

class IntroActivity : ComponentActivity() {

    private lateinit var pm: ProgressManager

    // Activity-level state so onResume() can update it
    private var resumeAvailable by mutableStateOf(false)

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pm = ProgressManager(this)
        resumeAvailable = pm.getCurrentDestination() != null

        setContent {
            MaterialTheme {
                var hasLocation by remember { mutableStateOf(hasAnyLocationPermission()) }

                // Recheck after settings or perms
                LaunchedEffect(Unit) { hasLocation = hasAnyLocationPermission() }

                IntroScreen(
                    hasLocation = hasLocation,
                    resumeAvailable = resumeAvailable,
                    onRefreshPermissionState = { hasLocation = hasAnyLocationPermission() },
                    onRequestLocation = {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    onAbout = { startActivity(Intent(this, AboutActivity::class.java)) },
                    onStart = { startActivity(Intent(this, MainActivity::class.java)) }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Update whenever we return to the beginning
        resumeAvailable = pm.getCurrentDestination() != null
    }

    private fun hasAnyLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
}

@Composable
private fun IntroScreen(
    hasLocation: Boolean,
    resumeAvailable: Boolean,
    onRefreshPermissionState: () -> Unit,
    onRequestLocation: () -> Unit,
    onAbout: () -> Unit,
    onStart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        Text(
            text = "Tourist’s Treasure Hunt",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Pick objectives, then follow clues that reveal your destination as you get closer.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Location: " + if (hasLocation) "Enabled ✅" else "Not enabled",
            style = MaterialTheme.typography.bodyMedium
        )

        if (resumeAvailable) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Resume available ✅",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(Modifier.height(8.dp))

        Button(onClick = {
            onRequestLocation()
            onRefreshPermissionState()
        }) {
            Text(if (hasLocation) "Re-check Location" else "Enable Location")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (resumeAvailable) "Resume Hunt" else "Start Hunt")
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onAbout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("About")
        }

        Spacer(Modifier.height(8.dp))
    }
}