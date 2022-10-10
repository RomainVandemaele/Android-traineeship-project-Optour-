package com.example.optitrip.utils.forms

import android.util.Log
import com.example.optitrip.utils.ValidatorResolver


/**
 * Model of form with validation through annotation constraints
 *
 */
abstract class FormModel {

    val isValid: Boolean
        get() {
            val fields = this.javaClass.declaredFields
            for (field in fields) {
                field.isAccessible = true
                Log.d("Field",field.toString())
                for (annotation in field.declaredAnnotations) {
                    val resolver = ValidatorResolver.fromAnnotation(annotation!!)
                    if (!resolver!!.apply(this, field)) {
                        return false
                    }
                }
            }
            return true
        }
}

