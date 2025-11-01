package com.example.touriststreasurehunt.ui

// Important imports
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.touriststreasurehunt.model.*
import com.google.gson.Gson
import kotlin.math.max

class ClueActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val huntJson = intent.getStringExtra("hunt_json") ?: ""
        val hunt = Gson().fromJson(huntJson, Hunt::class.java)

        setContent {
            MaterialTheme {
                ClueScreen(
                    hunt = hunt,
                    onComplete = {
                        Toast.makeText(this, "Hunt complete!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
private fun ClueScreen(hunt: Hunt, onComplete: () -> Unit) {
    var destIdx by remember { mutableStateOf(0) }
    var tier by remember { mutableStateOf(1) }
    var fakeDistance by remember { mutableStateOf(4000) } // Meter distance

    val d = hunt.destinations[destIdx]
    val currentClue = d.clues.first { it.tier == tier }

    fun nextTier(force: Boolean = false) {
        val maxTier = d.clues.maxOf { it.tier }
        if (tier < maxTier) {
            tier++
        } else {
            if (destIdx < hunt.destinations.lastIndex) {
                destIdx++
                tier = 1
                fakeDistance = 4000
            } else {
                onComplete()
            }
        }
    }

    fun evaluateProgress(distance: Int) {
        if (distance <= currentClue.proximityMeters) nextTier()
    }

    Column(Modifier.padding(16.dp)) {
        Text(text = d.name, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(text = "Clue: ${currentClue.text}")
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            fakeDistance = max(0, fakeDistance - 800)   // Simulate fake moving
            evaluateProgress(fakeDistance)
        }) { Text("Simulate Move Closer") }

        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = { nextTier(force = true) }) {
            Text("Manual Reveal")   // For offline purposes
        }
    }
}
