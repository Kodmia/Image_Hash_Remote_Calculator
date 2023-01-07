package ru.dikoresearch.imagehashcalculator

import android.app.Application
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.dikoresearch.imagehashcalculator.data.local.LocalImagesRepositoryImpl
import ru.dikoresearch.imagehashcalculator.data.local.db.CalculatedImagesDataBase
import ru.dikoresearch.imagehashcalculator.data.remote.ImageHashService
import ru.dikoresearch.imagehashcalculator.data.remote.RemoteHashCalculatorRepositoryImpl
import ru.dikoresearch.imagehashcalculator.domain.repository.local.LocalImagesRepository
import ru.dikoresearch.imagehashcalculator.domain.repository.remote.RemoteHashCalculatorRepository

class App: Application() {

    /**
     * Local repository stores images in Room DataBase
     */
    lateinit var localImagesRepository: LocalImagesRepository
    /**
     * Remote image hash calculator repository
     */
    lateinit var remoteHashCalculatorRepository: RemoteHashCalculatorRepository

    lateinit var dataBase: CalculatedImagesDataBase
    lateinit var hashCalculatorService: ImageHashService

    override fun onCreate() {
        super.onCreate()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl("http://dikoresearch.ru/api/ktorhash/")
            .build()

        hashCalculatorService = retrofit.create(ImageHashService::class.java)


        dataBase = CalculatedImagesDataBase.getInstance(this)

        localImagesRepository = LocalImagesRepositoryImpl(dataBase.calculatedImageDao())
        remoteHashCalculatorRepository = RemoteHashCalculatorRepositoryImpl(
            imageHashService = hashCalculatorService
        )
    }
}