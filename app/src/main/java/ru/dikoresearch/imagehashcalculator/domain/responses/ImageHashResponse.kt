package ru.dikoresearch.imagehashcalculator.domain.responses

import com.google.gson.annotations.SerializedName

data class ImageHashResponse(
    @SerializedName("imageHash") val imageHash: String
)
