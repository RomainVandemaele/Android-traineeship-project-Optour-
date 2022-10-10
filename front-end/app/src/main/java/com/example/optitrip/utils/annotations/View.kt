package com.example.optitrip.utils.annotations

import com.google.android.material.textfield.TextInputLayout

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class View(val view : View)
