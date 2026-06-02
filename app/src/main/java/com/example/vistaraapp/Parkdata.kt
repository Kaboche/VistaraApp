package com.example.vistaraapp

enum class ParkCategory {
    GAME_PARK,

}

data class NationalPark(
    val id: Int,
    val name: String,
    val location: String,
    val description: String,
    val category: ParkCategory
)

// Updated to focus on Nairobi National Park only
val allParks = listOf(
    NationalPark(
        id = 1,
        name = "Nairobi National Park",
        location = "Nairobi County, Kenya",
        description = "A unique wildlife sanctuary located just 7 km from Nairobi's city center. Home to lions, giraffes, zebras, rhinos, and over 400 bird species. The only national park in the world bordering a capital city.",
        category = ParkCategory.GAME_PARK
    )
)

// Helper function to get image resource (optional - if you have images)
fun getParkImage(parkId: Int): Int {
    // Return your image resource if you have one
    // For example: return R.drawable.nairobi_park
    return 0  // Return 0 for no image (will use placeholder)
}