package com.example.touriststreasurehunt.model

object MockRepo {

    // Personal preferences
    val objectives = listOf(
        Objective("coffee", "Coffee"),
        Objective("hike", "Short Hike"),
        Objective("rain", "Rainy Day")
    )

    // Examples
    val sample = listOf(
        Destination(
            name = "Coast Coffee",
            lat = 45.994, lon = -123.929,
            clues = listOf(
                Clue(1, "A warm stop near dunes… (~3km)", 3000),
                Clue(2, "South of the overlooking light house. (~1km)", 1000),
                Clue(3, "Across from the mural on Main street. (~250m)", 250)
            )
        ),
        Destination(
            name = "Fir Bluff Trail",
            lat = 45.998, lon = -123.940,
            clues = listOf(
                Clue(1, "A walk through the forest to a coastal view… (~3km)", 3000),
                Clue(2, "Its trailhead near a wooden bridge. (~1km)", 1000),
                Clue(3, "A sign with a bird marks the start. (~250m)", 250)
            )
        )
    )

    fun makeHunt(sel: List<Objective>) = Hunt(sel, sample)
}