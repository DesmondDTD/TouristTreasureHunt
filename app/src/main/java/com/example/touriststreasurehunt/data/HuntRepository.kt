package com.example.touriststreasurehunt.data
// JSON GSON parsing
// Important imports
import android.content.Context
import com.example.touriststreasurehunt.model.Destination
import com.example.touriststreasurehunt.model.LocationHintsFile
import com.google.gson.Gson

class HuntRepository(private val context: Context) {
    private val gson = Gson()

    fun loadDestinations(): List<Destination> {
        return try {
            val json = HuntDataStore.loadJson(context)
            val parsed = gson.fromJson(json, LocationHintsFile::class.java)

            parsed.items
                .map { item ->
                    Destination(
                        name = item.displayName,
                        lat = item.lat,
                        lon = item.lng,
                        objectiveTags = item.objectives,
                        clues = item.hints
                            .sortedBy { it.tier }
                            .map { h ->
                                val meters = when (h.proximity) {
                                    "far" -> 3000
                                    "near" -> 1000
                                    "very_near" -> 250
                                    else -> 1000
                                }
                                com.example.touriststreasurehunt.model.Clue(
                                    tier = h.tier,
                                    text = h.text,
                                    proximityMeters = meters
                                )
                            }
                    )
                }
        } catch (e: Exception) {
            emptyList()
        }
    }
}