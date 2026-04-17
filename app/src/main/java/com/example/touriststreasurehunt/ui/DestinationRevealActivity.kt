package com.example.touriststreasurehunt.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.touriststreasurehunt.model.Hunt
import com.google.gson.Gson
import com.example.touriststreasurehunt.data.ProgressManager
import android.net.Uri
import android.widget.Toast
import android.content.ActivityNotFoundException

class DestinationRevealActivity : ComponentActivity() {

    // Deep linkage
    private fun openMaps(lat: Double, lon: Double, label: String) {
        // 1) Maps
        val geoUri = Uri.parse("geo:$lat,$lon?q=$lat,$lon(${Uri.encode(label)})")
        val geoIntent = Intent(Intent.ACTION_VIEW, geoUri)

        try {
            startActivity(geoIntent)
            return
        } catch (_: ActivityNotFoundException) {
            // Broswer time
        }

        // 2) Browser
        val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lon")
        val webIntent = Intent(Intent.ACTION_VIEW, webUri)

        try {
            startActivity(webIntent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, "No Maps or browser app found.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val huntJson = intent.getStringExtra("hunt_json") ?: ""
        val destIndex = intent.getIntExtra("dest_index", 0)

        val hunt = Gson().fromJson(huntJson, Hunt::class.java)
        val destination = hunt.destinations[destIndex]
        val isLast = destIndex >= hunt.destinations.lastIndex

        setContent {
            MaterialTheme {
                RevealScreen(
                    destinationName = destination.name,
                    funFact = destination.funFact,
                    isLast = isLast,
                    onOpenMaps = { openMaps(destination.lat, destination.lon, destination.name) },
                    onContinue = {
                        val pm = ProgressManager(this)

                        if (destIndex >= hunt.destinations.lastIndex) {
                            // Finished last destination so we clear progress and go to end screen
                            pm.clearProgress()
                            startActivity(
                                Intent(this, FinishActivity::class.java)
                                    .putExtra("hunt_json", huntJson)
                            )
                        } else {
                            // Advance to next destination and reset tier to 1
                            val nextIndex = destIndex + 1
                            val nextDest = hunt.destinations[nextIndex]
                            pm.saveProgress(destinationName = nextDest.name, clueIndex = 1)

                            startActivity(
                                Intent(this, ClueActivity::class.java)
                                    .putExtra("hunt_json", huntJson)
                            )
                        }

                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun RevealScreen(
    destinationName: String,
    funFact: String?,
    isLast: Boolean,
    onOpenMaps: () -> Unit,
    onContinue: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "🎉 Location Found!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = destinationName,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(24.dp))

        funFact?.let {
            Text("Fun Fact:", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(it)
        }

        OutlinedButton(onClick = onOpenMaps) {
            Text("Open in Maps")
        }

        Spacer(Modifier.height(12.dp))

        Spacer(Modifier.height(32.dp))

        Button(onClick = onContinue) {
            Text(if (isLast) "Finish Hunt" else "Continue Hunt")
        }
    }
}