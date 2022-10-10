package com.example.optitrip.retrofit

import com.example.optitrip.entities.TripBasic
import com.example.optitrip.entities.trip.Trip
import retrofit2.Call
import retrofit2.http.*


interface TripAPI {

    @POST("trip")
    fun postTrip(@Body trip : Trip) : Call<Int?>

    @GET("trip/{id}")
    fun getTrip(@Path("id") id: Int) : Call<Trip?>

    @GET("trips/{clientId}")
    fun getTripsByClient(@Path("clientId") clientId: Int) : Call<Array<TripBasic>?>

    @GET("trips/text/{query}")
    fun getTripsByText(@Path("query") query: String) : Call<Array<TripBasic>?>


    @DELETE("trip/{id}")
    fun deleteTrip(@Path("id") id : Int)  : Call<Trip?>

    @PUT("trip/{id}")
    fun updateTrip(@Path("id") id : Int, @Body trip : Trip)

}