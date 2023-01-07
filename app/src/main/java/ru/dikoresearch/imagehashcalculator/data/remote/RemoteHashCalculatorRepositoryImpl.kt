package ru.dikoresearch.imagehashcalculator.data.remote

import io.reactivex.rxjava3.core.Single
import ru.dikoresearch.imagehashcalculator.domain.repository.remote.RemoteHashCalculatorRepository
import ru.dikoresearch.imagehashcalculator.domain.requests.CalculateImageHashRequest
import ru.dikoresearch.imagehashcalculator.domain.responses.ImageHashResponse

class RemoteHashCalculatorRepositoryImpl(
    private val imageHashService: ImageHashService
): RemoteHashCalculatorRepository {
    override fun calculateBase64Image(calculateImageHashRequest: CalculateImageHashRequest): Single<ImageHashResponse> {
        return imageHashService.calculateImage(calculateImageHashRequest)
    }
}