package ru.dikoresearch.imagehashcalculator.presentation.imagedetails

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.dikoresearch.imagehashcalculator.domain.entities.CalculatedImage
import ru.dikoresearch.imagehashcalculator.domain.repository.local.LocalImagesRepository
import ru.dikoresearch.imagehashcalculator.presentation.utils.LiveEvent
import ru.dikoresearch.imagehashcalculator.presentation.utils.ShowToast
import ru.dikoresearch.imagehashcalculator.presentation.utils.UiEvent
import ru.dikoresearch.imagehashcalculator.presentation.utils.ViewModelWithDisposableBag

class ImageDetailsViewModel(
    private val localImagesRepository: LocalImagesRepository
): ViewModelWithDisposableBag() {

    private val _calculatedImage = MutableLiveData<CalculatedImage>()
    val calculatedImage: LiveData<CalculatedImage> = _calculatedImage

    private val _actionEvent = LiveEvent<UiEvent>()
    val actionEvent: LiveData<UiEvent> = _actionEvent

    fun loadImageInfo(imageId: Int){
        compositeDisposable.add(
            localImagesRepository.getCalculatedImageById(imageId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {image ->
                        _calculatedImage.postValue(image)
                    },
                    {e ->
                        _actionEvent.postValue(ShowToast("Error loading image with id $imageId ${e.message}"))
                        Log.e("", "Can't load image with id $imageId, ${e.printStackTrace()}")
                    }
                )
        )
    }

}