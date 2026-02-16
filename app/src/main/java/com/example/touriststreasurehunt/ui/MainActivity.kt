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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.touriststreasurehunt.data.HuntRepository
import com.example.touriststreasurehunt.model.*
import com.google.gson.Gson
import androidx.compose.runtime.produceState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MainScreen { selected, destinations ->
                    val hunt = Hunt(
                        objectives = selected.ifEmpty { listOf(MockRepo.objectives[0]) },
                        destinations = destinations
                    )
                    val intent = Intent(this, ClueActivity::class.java)
                        .putExtra("hunt_json", Gson().toJson(hunt))
                    startActivity(intent)
                }
            }
        }
    }
}

@Composable
private fun MainScreen(onStart: (List<Objective>, List<Destination>) -> Unit) {
    val context = LocalContext.current

    val repo = remember { HuntRepository(context) }
    val destinations by produceState<List<Destination>>(initialValue = emptyList()) {
        value = repo.loadDestinations()
    }

    var coffee by remember { mutableStateOf(false) }
    var hike by remember { mutableStateOf(false) }
    var rain by remember { mutableStateOf(false) }

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
        Button(
            onClick = {
                val sel = buildList {
                    if (coffee) add(MockRepo.objectives[0])
                    if (hike) add(MockRepo.objectives[1])
                    if (rain) add(MockRepo.objectives[2])
                }
                onStart(sel, destinations)
            },
            enabled = destinations.isNotEmpty()
        ) {
            Text("Start Hunt")
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = { context.startActivity(Intent(context, AboutActivity::class.java)) },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("About")
        }
    }
}