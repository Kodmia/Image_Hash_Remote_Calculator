package ru.dikoresearch.imagehashcalculator.presentation.utils

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * ViewModel with composite disposable to store disposables and cancel them in onCleared()
 */
open class ViewModelWithDisposableBag: ViewModel() {

    val compositeDisposable by lazy {
        CompositeDisposable()
    }


    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}