package ru.dikoresearch.imagehashcalculator.domain.requests

import com.google.gson.annotations.SerializedName

data class CalculateImageHashRequest(
    @SerializedName("base64Image") val base64Image: String
)