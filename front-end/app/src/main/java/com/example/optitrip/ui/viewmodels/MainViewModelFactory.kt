package com.example.optitrip.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


/**
 * Factory pattern to build a [MainViewModel] with a context to use local [AppDatabase]
 *
 * @property context
 */
class MainViewModelFactory(private val context: Context) : ViewModelProvider.Factory  {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(context) as T
    }

}