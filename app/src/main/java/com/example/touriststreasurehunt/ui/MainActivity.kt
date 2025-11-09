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
import com.example.touriststreasurehunt.model.*
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MainScreen { selected ->
                    val hunt = MockRepo.makeHunt(selected.ifEmpty { listOf(MockRepo.objectives[0]) })
                    val intent = Intent(this, ClueActivity::class.java)
                        .putExtra("hunt_json", Gson().toJson(hunt))
                    startActivity(intent)
                }
            }
        }
    }
}

@Composable
private fun MainScreen(onStart: (List<Objective>) -> Unit) {
    var coffee by remember { mutableStateOf(false) }
    var hike by remember { mutableStateOf(false) }
    var rain by remember { mutableStateOf(false) }

    // List the preference categories
    Column(Modifier.padding(16.dp)) {
        Text("Choose objectives", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Checkbox(checked = coffee, onCheckedChange = { coffee = it })
            Text("Coffee")
        }
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Checkbox(checked = hike, onCheckedChange = { hike = it })
            Text("Short Hike")
        }
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Checkbox(checked = rain, onCheckedChange = { rain = it })
            Text("Rainy Day")
        }

        Spacer(Modifier.height(12.dp))
        // Selection
        Button(onClick = {
            val sel = buildList {
                if (coffee) add(MockRepo.objectives[0])
                if (hike) add(MockRepo.objectives[1])
                if (rain) add(MockRepo.objectives[2])
            }
            onStart(sel)
        }) {
            Text("Start Hunt")
        }
    }
}
