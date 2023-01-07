package ru.dikoresearch.imagehashcalculator.presentation.utils

import android.os.Bundle

sealed class UiEvent

data class ShowToast(val msg: String): UiEvent()
data class Navigate(val destination: String, val bundle: Bundle? = null): UiEvent()
