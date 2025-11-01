package com.example.touriststreasurehunt.model

data class Objective(val id: String, val label: String)

data class Clue(
    val tier: Int,                 // 1 through 3
    val text: String,              // Clue copy
    val proximityMeters: Int       // For different tiers
)

data class Destination(
    val name: String,
    val lat: Double,
    val lon: Double,
    val clues: List<Clue>
)

data class Hunt(
    val objectives: List<Objective>,
    val destinations: List<Destination>
)
