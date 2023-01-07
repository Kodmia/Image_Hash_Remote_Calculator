package ru.dikoresearch.imagehashcalculator.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CalculatedImage(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "image_uri") val imageUri: String,
    @ColumnInfo(name = "image_hash") val imageHash: String,
    @ColumnInfo(name = "counter") val counter: Int
)