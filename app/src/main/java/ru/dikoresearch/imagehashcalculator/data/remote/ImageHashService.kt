package ru.dikoresearch.imagehashcalculator.data.remote

import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.POST
import ru.dikoresearch.imagehashcalculator.domain.requests.CalculateImageHashRequest
import ru.dikoresearch.imagehashcalculator.domain.responses.ImageHashResponse

interface ImageHashService {

    @POST("base64tohash")
    fun calculateImage(@Body base64Img: CalculateImageHashRequest): Single<ImageHashResponse>
}