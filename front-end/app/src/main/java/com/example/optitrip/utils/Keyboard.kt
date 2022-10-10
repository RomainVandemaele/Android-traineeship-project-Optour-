package com.example.optitrip.utils

import android.app.Activity
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

/**
 * Hoide the keyboard from the [activity] screen
 *
 * @param activity
 */
fun hideSoftKeyboard(activity: Activity) {
    val inputMethodManager: InputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    Log.d("keyboard","$inputMethodManager")
    if (inputMethodManager.isAcceptingText ) {
        inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
    }
}

