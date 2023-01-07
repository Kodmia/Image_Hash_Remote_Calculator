package ru.dikoresearch.imagehashcalculator.domain.repository.local

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.dikoresearch.imagehashcalculator.domain.entities.CalculatedImage

interface LocalImagesRepository {
    fun getAll(): Single<List<CalculatedImage>>
    fun insertCalculatedImage(image: CalculatedImage): Single<Long>
    fun deleteCalculatedImage(image: CalculatedImage): Completable
    fun updateCalculatedImage(image: CalculatedImage): Completable
    fun getCalculatedImageById(id: Int): Single<CalculatedImage>
}