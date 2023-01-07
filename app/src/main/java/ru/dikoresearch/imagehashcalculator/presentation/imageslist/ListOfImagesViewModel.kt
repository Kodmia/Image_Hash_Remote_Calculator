package ru.dikoresearch.imagehashcalculator.presentation.imageslist

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.dikoresearch.imagehashcalculator.domain.entities.CalculatedImage
import ru.dikoresearch.imagehashcalculator.domain.repository.local.LocalImagesRepository
import ru.dikoresearch.imagehashcalculator.presentation.imagedetails.ImageDetailsFragment
import ru.dikoresearch.imagehashcalculator.presentation.utils.*

class ListOfImagesViewModel(
    private val localImagesRepository: LocalImagesRepository
): ViewModelWithDisposableBag() {


    private val _actionEvent = LiveEvent<UiEvent>()
    val actionEvent: LiveData<UiEvent> = _actionEvent

    private val _imagesList = MutableLiveData<List<CalculatedImage>>()
    val imagesList: LiveData<List<CalculatedImage>> = _imagesList

    private val _showProgressIndicator = MutableLiveData(false)
    val showProgressIndicator: LiveData<Boolean> = _showProgressIndicator


    fun getAllImages(){
        compositeDisposable.add(
            localImagesRepository.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        _imagesList.postValue(it)
                    },
                    {e ->
                        Log.e("", "Error: ${e.printStackTrace()}")
                    }
                )
        )
    }

    fun deleteImageFromRepository(image: CalculatedImage){
        compositeDisposable.add(
            localImagesRepository.deleteCalculatedImage(image)
                .toSingleDefault(false)
                .flatMap {
                    localImagesRepository.getAll()
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        _imagesList.postValue(it)
                    },
                    {e->
                        Log.e("", "Error: ${e.printStackTrace()}")
                    }
                )
        )
    }

    fun updateCounterAndNavigate(image: CalculatedImage){
        val newImage = image.copy(counter = image.counter + 1)
        compositeDisposable.add(
            localImagesRepository.updateCalculatedImage(newImage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        val bundle = Bundle()
                        bundle.putInt(ImageDetailsFragment.IMAGE_ID, image.uid)
                        _actionEvent.postValue(Navigate(NavigationDestination.IMAGE_DETAILS_SCREEN, bundle))
                    },
                    { e ->
                        Log.e("", "Error: ${e.printStackTrace()}")
                    }
                )
        )
    }

}