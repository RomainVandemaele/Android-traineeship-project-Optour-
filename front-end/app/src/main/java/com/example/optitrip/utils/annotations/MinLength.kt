package com.example.optitrip.utils.annotations


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class MinLength(val size: Int)


