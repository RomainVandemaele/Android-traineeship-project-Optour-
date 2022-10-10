package com.example.optitrip.retrofit

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


object RequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return chain.proceed(request)
    }

}

/**
 * Retrofit client for API call
 *
 * @constructor Create empty Retrofit client
 */
object RetrofitClient   {
    private const val BASE_URL_PHONE = "http:192.168.0.9:3000/"
    private const val BASE_URL = "http:10.0.2.2:3000/"
    private const val MAP_BASE_URL = "https://maps.googleapis.com/maps/api/"

    private val gsonBuilder = GsonBuilder()
        .setLenient()
        .create()


    val clientLocal: Retrofit by lazy {
        val client = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gsonBuilder))
            .build()
        client
    }

    val clientMap: Retrofit by lazy {
        val client = Retrofit.Builder()
            .baseUrl(MAP_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        client
    }


    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient().newBuilder()
            .addInterceptor(RequestInterceptor)
            .build()
    }

}