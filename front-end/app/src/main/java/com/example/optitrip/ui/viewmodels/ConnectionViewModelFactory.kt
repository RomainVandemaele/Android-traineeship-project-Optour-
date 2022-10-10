package com.example.optitrip.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory pattern to build a [ConnectionViewModel] with a context to use local [AppDatabase]
 *
 * @property context the context of the activity
 */
class ConnectionViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ConnectionViewModel(context) as T
    }
}