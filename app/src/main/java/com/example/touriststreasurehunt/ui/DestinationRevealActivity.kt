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

class DestinationRevealActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val huntJson = intent.getStringExtra("hunt_json") ?: ""
        val destIndex = intent.getIntExtra("dest_index", 0)

        val hunt = Gson().fromJson(huntJson, Hunt::class.java)
        val destination = hunt.destinations[destIndex]

        setContent {
            MaterialTheme {
                RevealScreen(
                    destinationName = destination.name,
                    funFact = destination.funFact,
                    onContinue = {

                        if (destIndex >= hunt.destinations.lastIndex) {

                            startActivity(
                                Intent(this, FinishActivity::class.java)
                                    .putExtra("hunt_json", huntJson)
                            )

                        } else {

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

        Spacer(Modifier.height(32.dp))

        Button(onClick = onContinue) {
            Text("Continue Hunt")
        }
    }
}