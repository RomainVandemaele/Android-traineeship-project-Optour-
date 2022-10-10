package com.example.optitrip.utils.annotations


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Min(val value: Int)


