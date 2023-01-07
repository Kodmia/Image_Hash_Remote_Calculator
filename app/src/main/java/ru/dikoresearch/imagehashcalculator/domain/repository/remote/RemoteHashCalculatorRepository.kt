package ru.dikoresearch.imagehashcalculator.domain.repository.remote

import io.reactivex.rxjava3.core.Single
import ru.dikoresearch.imagehashcalculator.domain.requests.CalculateImageHashRequest
import ru.dikoresearch.imagehashcalculator.domain.responses.ImageHashResponse

interface RemoteHashCalculatorRepository {
    fun calculateBase64Image(calculateImageHashRequest: CalculateImageHashRequest): Single<ImageHashResponse>
}