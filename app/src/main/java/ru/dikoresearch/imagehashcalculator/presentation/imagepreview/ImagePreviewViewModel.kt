package ru.dikoresearch.imagehashcalculator.presentation.imagepreview

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.dikoresearch.imagehashcalculator.domain.entities.CalculatedImage
import ru.dikoresearch.imagehashcalculator.domain.repository.local.LocalImagesRepository
import ru.dikoresearch.imagehashcalculator.domain.repository.remote.RemoteHashCalculatorRepository
import ru.dikoresearch.imagehashcalculator.domain.requests.CalculateImageHashRequest
import ru.dikoresearch.imagehashcalculator.presentation.imagedetails.ImageDetailsFragment
import ru.dikoresearch.imagehashcalculator.presentation.utils.*
import java.io.ByteArrayOutputStream

class ImagePreviewViewModel(
    private val localImagesRepository: LocalImagesRepository,
    private val remoteHashCalculatorRepository: RemoteHashCalculatorRepository
): ViewModelWithDisposableBag() {

    private val _actionEvent = LiveEvent<UiEvent>()
    val actionEvent: LiveData<UiEvent> = _actionEvent

    private val _showProgressIndicator = MutableLiveData(false)
    val showProgressIndicator: LiveData<Boolean> = _showProgressIndicator

    fun processImage(bitmap: Bitmap, uri: String){
        _showProgressIndicator.postValue(true)

        compositeDisposable.add(
            bitmapToBase64(bitmap)
                .flatMap {
                    bitmapToBase64(bitmap)
                }
                .flatMap{ imageBase64 ->
                    val request = CalculateImageHashRequest(
                        base64Image = imageBase64
                    )
                    remoteHashCalculatorRepository.calculateBase64Image(request)
                }
                .flatMap{ hashResponse ->
                    val hash = hashResponse.imageHash
                    val calculatedImage = CalculatedImage(
                        imageUri = uri,
                        imageHash = hash,
                        counter = 0
                    )
                    localImagesRepository.insertCalculatedImage(
                        calculatedImage
                    )
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { id ->
                        _showProgressIndicator.postValue(false)
                        val bundle = bundleOf(ImageDetailsFragment.IMAGE_ID to id.toInt())
                        _actionEvent.postValue(Navigate(NavigationDestination.IMAGE_DETAILS_SCREEN, bundle))
                    },
                    {e ->
                        _showProgressIndicator.postValue(false)
                        _actionEvent.postValue(ShowToast("Error: ${e.message}"))
                        Log.e("", "Error calculating image $e")
                    }
                )

        )
    }

    private fun bitmapToBase64(bitmap: Bitmap): Single<String>{
        return Single.create { subscriber ->
            try {
                val byteArrayStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayStream)
                val imageBase64 = Base64.encodeToString(byteArrayStream.toByteArray(), Base64.DEFAULT)
                subscriber.onSuccess(imageBase64)
            }
            catch (e: Exception){
                subscriber.onError(e)
            }
        }
    }
}