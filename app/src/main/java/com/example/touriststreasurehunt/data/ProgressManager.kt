package com.example.touriststreasurehunt.data

import android.content.Context

class ProgressManager(context: Context) {

    private val prefs =
        context.getSharedPreferences("hunt_progress", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CURRENT_DESTINATION = "current_destination"
        private const val KEY_CURRENT_CLUE_INDEX = "current_clue_index"
    }

    fun saveProgress(destinationName: String, clueIndex: Int) {
        prefs.edit().apply {
            putString(KEY_CURRENT_DESTINATION, destinationName)
            putInt(KEY_CURRENT_CLUE_INDEX, clueIndex)
            apply()
        }
    }

    fun getCurrentDestination(): String? {
        return prefs.getString(KEY_CURRENT_DESTINATION, null)
    }

    fun getCurrentClueIndex(): Int {
        return prefs.getInt(KEY_CURRENT_CLUE_INDEX, 0)
    }

    fun clearProgress() {
        prefs.edit().clear().apply()
    }
}
