package ru.dikoresearch.imagehashcalculator.data.local.db

import androidx.room.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.dikoresearch.imagehashcalculator.domain.entities.CalculatedImage

@Dao
interface CalculatedImagesDao {
    @Query("SELECT * from calculatedImage")
    fun getAll(): Single<List<CalculatedImage>>

    @Query("SELECT * from calculatedImage WHERE uid = :id")
    fun getById(id: Int): Single<CalculatedImage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCalculatedImage(image: CalculatedImage): Single<Long>

    @Update
    fun updateCalculatedImage(image: CalculatedImage): Completable

    @Delete
    fun deleteCalculatedImage(image: CalculatedImage): Completable // delete by id
}