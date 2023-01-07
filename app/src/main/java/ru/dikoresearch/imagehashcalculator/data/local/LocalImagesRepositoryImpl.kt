package ru.dikoresearch.imagehashcalculator.data.local

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.dikoresearch.imagehashcalculator.data.local.db.CalculatedImagesDao
import ru.dikoresearch.imagehashcalculator.domain.entities.CalculatedImage
import ru.dikoresearch.imagehashcalculator.domain.repository.local.LocalImagesRepository

class LocalImagesRepositoryImpl(
    private val calculatedImagesDao: CalculatedImagesDao
): LocalImagesRepository {
    override fun getAll(): Single<List<CalculatedImage>> = calculatedImagesDao.getAll()

    override fun insertCalculatedImage(image: CalculatedImage): Single<Long> =
        calculatedImagesDao.insertCalculatedImage(image)

    override fun deleteCalculatedImage(image: CalculatedImage): Completable =
        calculatedImagesDao.deleteCalculatedImage(image)

    override fun updateCalculatedImage(image: CalculatedImage): Completable =
        calculatedImagesDao.updateCalculatedImage(image)

    override fun getCalculatedImageById(id: Int): Single<CalculatedImage> =
        calculatedImagesDao.getById(id)
}