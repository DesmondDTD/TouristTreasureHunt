package com.example.touriststreasurehunt.ui

// Important imports
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.touriststreasurehunt.model.Hunt
import com.google.gson.Gson

// All done!
class FinishActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val hunt = Gson().fromJson(intent.getStringExtra("hunt_json") ?: "", Hunt::class.java)

        setContent {
            MaterialTheme {
                Column(Modifier.padding(16.dp)) {
                    Text("Hunt Complete!", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    Text("You visited:")
                    Spacer(Modifier.height(8.dp))
                    hunt.destinations.forEach { dest ->
                        Text("• ${dest.name}")
                    }
                    Spacer(Modifier.height(24.dp))
                    Button(onClick = {
                        startActivity(Intent(this@FinishActivity, MainActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
                        finish()
                    }) { Text("Start New Hunt") }

                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = { /* TODO: share later */ }) { Text("Share") }
                }
            }
        }
    }
}
