package com.example.optitrip.utils

import com.example.optitrip.utils.annotations.Mail
import com.example.optitrip.utils.annotations.Min
import com.example.optitrip.utils.annotations.MinLength
import com.example.optitrip.utils.annotations.Required
import java.lang.reflect.Field
import java.util.function.BiFunction


/**
 * Enum where elements linls annotation and function that validate if the field respect its annotation
 *
 * @property annotation
 * @property validator
 */
enum class ValidatorResolver(
    var annotation: Class<*>,
    var validator: BiFunction<Any, Field, Boolean>
) {
    REQUIRED(
        Required::class.java,
        BiFunction { src: Any, field: Field ->
            required(
                src,
                field
            )
        }),
    MIN_LENGTH(
        MinLength::class.java,
        BiFunction { src: Any, field: Field ->
            minLength(
                src,
                field
            )
        }),
    MIN(
        Min::class.java,
        BiFunction { o: Any, field: Field ->
            min(
                o,
                field
            )
        }),

    MAIL(
        Mail::class.java,
        BiFunction { src: Any, field: Field ->
            mail(
                src,
                field
            )
        });


    companion object {
        private fun min(o: Any, field: Field): Boolean {
            var value: Int? = null
            return try {
                value = field.getInt(o)
                val minAnnotation: Min = field.getAnnotation(Min::class.java)
                return value >= minAnnotation.value
            } catch (e: IllegalAccessException) {
                false
            }
        }

        private fun minLength(src: Any, field: Field): Boolean {
            return try {
                val value = field[src] as String
                val minLength: MinLength = field.getAnnotation(MinLength::class.java)
                return value.length >= minLength.size
            } catch (e: Exception) {
                false
            }
        }

        private fun required(src: Any, field: Field): Boolean {
            return try {
                val value = field[src]
                if (field.type == String::class.java && (value == null || value == "" )) {
                    false
                } else {
                    value != null
                }
            } catch (e: Exception) {
                false
            }
        }


        private fun mail(src: Any, field: Field): Boolean {
            return try {
                val value = field[src] as String
                if (field.type == String::class.java && (value == null || value == "" )) {
                    false
                } else {
                    val regex = "[a-zA-Z0-9.]+[@][a-zA-Z]+[.][a-zA-Z]+".toRegex()
                    return regex.matches(value)
                }
            } catch (e: Exception) {
                false
            }
        }

        fun fromAnnotation(annotation: Annotation): BiFunction<Any, Field, Boolean>? {
            for (resolver in values()) {

                if (resolver.annotation.name === annotation.annotationClass.java.name) {
                    return resolver.validator
                }
            }
            return null
        }
    }
}

