package ru.dikoresearch.imagehashcalculator.presentation.utils

import ru.dikoresearch.imagehashcalculator.App
import ru.dikoresearch.imagehashcalculator.Navigator
import ru.dikoresearch.imagehashcalculator.presentation.imageslist.ListOfImagesViewModel


import android.util.Log
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.dikoresearch.imagehashcalculator.presentation.imagedetails.ImageDetailsViewModel
import ru.dikoresearch.imagehashcalculator.presentation.imagepreview.ImagePreviewViewModel
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class ViewModelFactory(
    private val app: App
): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = when(modelClass){
            ListOfImagesViewModel::class.java -> {
                ListOfImagesViewModel(app.localImagesRepository)
            }
            ImagePreviewViewModel::class.java -> {
                ImagePreviewViewModel(
                    app.localImagesRepository,
                    app.remoteHashCalculatorRepository
                )
            }
            ImageDetailsViewModel::class.java -> {
                ImageDetailsViewModel(
                    app.localImagesRepository
                )
            }
            else -> {
                throw IllegalStateException("Unknown View Model")
            }
        }

        @Suppress("UNCHECKED_CAST")
        return viewModel  as T
    }
}

fun Fragment.viewModelFactory() = ViewModelFactory(app = requireContext().applicationContext as App)
fun Fragment.navigator() = requireActivity() as Navigator

class LiveEvent<T> : MutableLiveData<T>() {
    private val mPending = AtomicBoolean(false)
    private val values: Queue<T> = LinkedList()

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (hasActiveObservers()) {
            Log.w(this::class.java.name, "Multiple observers registered but only one will be notified of changes.")
        }
        // Observe the internal MutableLiveData
        super.observe(owner) { t: T ->
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(t)
                //call next value processing if have such
                if (values.isNotEmpty())
                    pollValue()
            }
        }
    }

    override fun postValue(value: T) {
        values.add(value)
        pollValue()
    }

    private fun pollValue() {
        value = values.poll()
    }

    @MainThread
    override fun setValue(t: T?) {
        mPending.set(true)
        super.setValue(t)
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @Suppress("unused")
    @MainThread
    fun call() {
        value = null
    }
}