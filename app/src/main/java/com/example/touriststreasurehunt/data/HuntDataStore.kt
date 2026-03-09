package com.example.touriststreasurehunt.data

// Important imports
import android.content.Context
import java.io.File

object HuntDataStore {

    // Bump this when JSON changes
    private const val SCHEMA_VERSION = 2

    private const val CACHE_FILE_NAME = "locations_hints_cache.json"
    private const val META_FILE_NAME = "locations_hints_meta.txt"
    private const val ASSET_FILE_NAME = "locations_hints.json"

    // Offline first: load cached JSON, otherwise copy from assets into cache and return
    fun loadJson(context: Context): String {
        val cache = File(context.filesDir, CACHE_FILE_NAME)
        val meta = File(context.filesDir, META_FILE_NAME)

        // Cache exists matches schema... we use it
        if (cache.exists() && meta.exists()) {
            val savedVersion = meta.readText().trim().toIntOrNull()
            if (savedVersion == SCHEMA_VERSION) {
                return cache.readText()
            }
        }

        // Otherwise update through assets
        val fresh = context.assets.open(ASSET_FILE_NAME).bufferedReader().use { it.readText() }
        cache.writeText(fresh)
        meta.writeText(SCHEMA_VERSION.toString())
        return fresh
    }

    // Optional force cache refresh from assets (good for debugging)
    fun refreshFromAssets(context: Context): String {
        val cache = File(context.filesDir, CACHE_FILE_NAME)
        val meta = File(context.filesDir, META_FILE_NAME)

        val fresh = context.assets.open(ASSET_FILE_NAME).bufferedReader().use { it.readText() }
        cache.writeText(fresh)
        meta.writeText(SCHEMA_VERSION.toString())
        return fresh
    }

    // Optional clear cache (good for testing)
    fun clearCache(context: Context) {
        File(context.filesDir, CACHE_FILE_NAME).delete()
        File(context.filesDir, META_FILE_NAME).delete()
    }
}