package com.example.touriststreasurehunt.ui

// Important imports
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.touriststreasurehunt.model.Hunt
import com.example.touriststreasurehunt.model.Destination
import com.google.android.gms.location.*
import com.google.gson.Gson
import kotlin.math.max
import android.annotation.SuppressLint
import com.google.android.gms.tasks.CancellationTokenSource
import androidx.activity.result.contract.ActivityResultContracts

class ClueActivity : ComponentActivity() {

    private lateinit var fused: FusedLocationProviderClient
    private val req = LocationRequest.Builder(
        Priority.PRIORITY_BALANCED_POWER_ACCURACY, 10_000L
    ).setMinUpdateIntervalMillis(5_000L).build()

    private var _lastLocation: Location? = null

    private val cb = object : LocationCallback() {
        override fun onLocationResult(res: LocationResult) {
            _lastLocation = res.lastLocation
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val granted = results[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                results[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) startLocation()
        else Toast.makeText(this, "Location denied. Use the manual reveal button as an alternative.", Toast.LENGTH_SHORT).show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fused = LocationServices.getFusedLocationProviderClient(this)

        val huntJson = intent.getStringExtra("hunt_json") ?: ""
        val hunt = Gson().fromJson(huntJson, Hunt::class.java)

        setContent {
            MaterialTheme {
                var showRationale by remember { mutableStateOf(false) }

                // Starting and stopping updates when perms change
                val hasLocation = hasAnyLocationPermission()
                LaunchedEffect(hasLocation) {
                    if (hasLocation) startLocation() else stopLocation()
                }

                val hasPermission = hasAnyLocationPermission()

                ClueScreen(
                    hunt = hunt,
                    lastLocationProvider = { _lastLocation },
                    requestPermission = {
                        val shouldExplain = ActivityCompat.shouldShowRequestPermissionRationale(
                            this, Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        if (shouldExplain) {
                            showRationale = true
                        } else {
                            permissionLauncher.launch(arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ))
                        }

                    },
                    openSettings = {
                        val uri = Uri.fromParts("package", packageName, null)
                        startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri))
                    },

                    onComplete = {
                        stopLocation()
                        startActivity(Intent(this, FinishActivity::class.java).apply {
                            putExtra("hunt_json", huntJson)
                        })
                        finish()
                    },

                    hasPermission = hasPermission
                )
                // Explanation
                if (showRationale) {
                    AlertDialog(
                        onDismissRequest = { showRationale = false },
                        title = { Text("Why we need your location") },
                        text = {
                            Text("This app includes a tiered clue system that grows in specificity " +
                                    "the closer you are to the location. By using your location, it " +
                                    "will allow the app to progress your clues automatically. As " +
                                    "an alternative, you can use the Manual Reveal button at any point.")
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                showRationale = false
                                permissionLauncher.launch(arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                ))
                            }) { Text("Continue") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showRationale = false }) { Text("Not now") }
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (hasAnyLocationPermission()) startLocation()
    }


    override fun onPause() {
        stopLocation()
        super.onPause()
    }

    @SuppressLint("MissingPermission")
    private fun startLocation() {
        if (!hasAnyLocationPermission()) return
        try {
            // Seed one-shot UI shows a distance quick
            val cts = CancellationTokenSource()
            fused.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token)
                .addOnSuccessListener { loc -> _lastLocation = loc }

            // Continuous updates
            fused.requestLocationUpdates(req, cb, mainLooper)
        } catch (_: SecurityException) { /* permission revoked mid-call; ignore */ }
    }


    private fun hasAnyLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun stopLocation() {
        try { fused.removeLocationUpdates(cb) } catch (_: SecurityException) {}
    }

    companion object { private const val REQ_LOC = 42 }
}

@Composable
private fun ClueScreen(
    hunt: Hunt,
    lastLocationProvider: () -> Location?,
    requestPermission: () -> Unit,
    openSettings: () -> Unit,
    onComplete: () -> Unit,
    hasPermission: Boolean
) {
    var destIdx by remember { mutableStateOf(0) }
    var tier by remember { mutableStateOf(1) }
    var fakeDistance by remember { mutableStateOf(4000) }   // Sim

    val d: Destination = hunt.destinations[destIdx]
    val currentClue = d.clues.first { it.tier == tier }
    val maxTier = d.clues.maxOf { it.tier }

    fun promote() {
        if (tier < maxTier) {
            tier++
        } else {
            if (destIdx < hunt.destinations.lastIndex) {
                destIdx++; tier = 1; fakeDistance = 4000
            } else {
                onComplete()
            }
        }
    }

    // Calc distance from last known location
    val lastLoc = lastLocationProvider()
    val distanceMeters: Float? = remember(lastLoc, destIdx, tier) {
        if (lastLoc == null) null else FloatArray(1).also {
            Location.distanceBetween(lastLoc.latitude, lastLoc.longitude, d.lat, d.lon, it)
        }[0]
    }

    // Promote on real GPS within band proximity
    LaunchedEffect(distanceMeters, currentClue) {
        if (distanceMeters != null && distanceMeters <= currentClue.proximityMeters) {
            promote()
        }
    }

    Column(Modifier.padding(16.dp)) {
        Text(text = d.name, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(text = "Clue: ${currentClue.text}")
        Spacer(Modifier.height(12.dp))
        Text(text = "Distance (approx): " + (distanceMeters?.toInt()?.toString() ?: "—") + " m")
        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            fakeDistance = max(0, fakeDistance - 800)
            if (fakeDistance <= currentClue.proximityMeters) promote()
        }) { Text("Simulate Move Closer") }

        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = { promote() }) {
            Text("Manual Reveal")
        }

        Spacer(Modifier.height(24.dp))
        val hasFix = lastLocationProvider() != null
        when {
            !hasPermission -> {
                Row {
                    Button(onClick = requestPermission) { Text("Enable Location") }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = openSettings) { Text("Open Settings") }
                }
            }
            !hasFix -> {
                Text("Getting location…")  // Has perm, waiting on location grab
            }
            else -> {
                // Show nothing since we got perms and location
            }
        }


    }
}
