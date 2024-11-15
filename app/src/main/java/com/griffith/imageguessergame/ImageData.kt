package com.griffith.imageguessergame


// Function to get images based on category and shuffle them
fun getImagesForCategory(categoryName: String): List<Pair<Int, String>> {
    // Define images for each category
    val animalImages = listOf(
        R.drawable.animal_fox to "Fox",
        R.drawable.animal_flamingo to "Flamingo",
        R.drawable.animal_bear to "Bear"
    )

    val logoImages = listOf(
        R.drawable.logo_apple to "Apple",
        R.drawable.logo_nike to "Nike",
        R.drawable.logo_adidas to "Adidas"
    )

    val fruitImages = listOf(
        R.drawable.fruit_apple to "Apple",
        R.drawable.fruit_strawberries to "Strawberry",
        R.drawable.fruit_bananas to "Banana"
    )

    // Combine all images for the random category
    val allImages = animalImages + logoImages + fruitImages

    return when (categoryName) {
        "Animals" -> animalImages.shuffled()
        "Logos" -> logoImages.shuffled()
        "Fruits" -> fruitImages.shuffled()
        "Random" -> allImages.shuffled() // Random compilation of all images
        else -> emptyList()
    }
}
