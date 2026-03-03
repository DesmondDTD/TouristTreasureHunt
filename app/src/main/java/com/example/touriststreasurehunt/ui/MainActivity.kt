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
import com.example.touriststreasurehunt.data.ProgressManager

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val progressManager = ProgressManager(this)
        val savedDestination = progressManager.getCurrentDestination()
        val savedClueIndex = progressManager.getCurrentClueIndex()

        // 🔹 If saved progress exists → auto resume hunt
        if (savedDestination != null) {

            val repo = HuntRepository(this)
            val destinations = repo.loadDestinations()

            val savedObjectiveIds = progressManager.getSelectedObjectives()
            val savedObjectives = if (savedObjectiveIds.isEmpty()) {
                emptyList()
            } else {
                MockRepo.objectives.filter { it.id in savedObjectiveIds }
            }

            val hunt = Hunt(
                objectives = savedObjectives.ifEmpty { listOf(MockRepo.objectives[0]) },
                destinations = destinations
            )

            val intent = Intent(this, ClueActivity::class.java)
                .putExtra("hunt_json", Gson().toJson(hunt))

            startActivity(intent)
            finish()
            return
        }

        // 🔹 Normal start screen
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

    var scenic by remember { mutableStateOf(false) }
    var history by remember { mutableStateOf(false) }
    var adventure by remember { mutableStateOf(false) }

    Column(Modifier.padding(16.dp)) {
        Text("Choose objectives", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Checkbox(checked = scenic, onCheckedChange = { scenic = it })
            Text("Scenic")
        }
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Checkbox(checked = history, onCheckedChange = { history = it })
            Text("History")
        }
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Checkbox(checked = adventure, onCheckedChange = { adventure = it })
            Text("Adventure")
        }

        Spacer(Modifier.height(12.dp))
        Button(
            onClick = {
                val sel = buildList {
                    if (scenic) add(MockRepo.objectives.first { it.id == "scenic" })
                    if (history) add(MockRepo.objectives.first { it.id == "history" })
                    if (adventure) add(MockRepo.objectives.first { it.id == "adventure" })
                }
                // Save for resume
                ProgressManager(context).saveSelectedObjectives(sel.map { it.id })

                val filtered = filterDestinationsByObjectives(
                    all = destinations,
                    selected = sel,
                    allObjectiveCount = MockRepo.objectives.size
                )
                onStart(sel, filtered)
            },
            enabled = destinations.isNotEmpty()
        ) {
            Text("Start Hunt")
        }
    }
}

// Objective logic
private fun filterDestinationsByObjectives(
    all: List<Destination>,
    selected: List<Objective>,
    allObjectiveCount: Int
): List<Destination> {
    // None or all = include everything
    if (selected.isEmpty() || selected.size == allObjectiveCount) return all

    val selectedIds = selected.map { it.id }.toSet()

    // Include if it matches any selected objective
    return all.filter { dest ->
        dest.objectiveTags.any { it in selectedIds }
    }
}