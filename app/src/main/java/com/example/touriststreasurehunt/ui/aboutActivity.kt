package com.example.touriststreasurehunt.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AboutScreen()
            }
        }
    }
}

@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Tourist's Treasure Hunt",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Version 1.0",
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 16.sp
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = """
                Tourist’s Treasure Hunt is an Android app built with Jetpack Compose 
                for exploring new places in a fun, interactive way. 

                Choose your objectives, start your personalized hunt, 
                and follow progressive clues that reveal each destination 
                as you get closer — either with real GPS or manual reveal.

                Developed as a Capstone Project for CS 461.
            """.trimIndent(),
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        Text(
            text = "Team Members:",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = """
                Jack Perkins  
                Desmond Delaney  
                (and team members, if applicable)
            """.trimIndent(),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}
