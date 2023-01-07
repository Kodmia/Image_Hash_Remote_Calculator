package ru.dikoresearch.imagehashcalculator.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.dikoresearch.imagehashcalculator.domain.entities.CalculatedImage

@Database(
  entities = [CalculatedImage::class],
  version = 1
)
abstract class CalculatedImagesDataBase: RoomDatabase() {
    abstract fun calculatedImageDao(): CalculatedImagesDao

    companion object {
        private var INSTANCE: CalculatedImagesDataBase? = null
        private val lock = Any()

        @JvmStatic
        fun getInstance(context: Context): CalculatedImagesDataBase{
            synchronized(lock){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        CalculatedImagesDataBase::class.java,
                        "calculated_images_database.db"
                    )
                        .build()
                }
                return INSTANCE!!
            }
        }
    }
}