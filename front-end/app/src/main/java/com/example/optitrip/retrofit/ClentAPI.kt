package com.example.optitrip.retrofit

import com.example.optitrip.entities.Client
import retrofit2.Call
import retrofit2.http.*

/**
 * Retrofit APi to communicate with the REST API used for DB
 * this API is responsible for getting and creating [Client]
 *
 */
interface ClientAPI {

    @GET("clients/{username}")
    fun getClientByUsername(@Path("username") username : String) : Call<Client?>

    @POST("clients")
    fun postClient(@Body client: Client) : Call<Client?>

    @POST("clients/{id}")
    fun activateClient(@Path("id") id : Int) : Call<String?>

    @PATCH("client/{id}/username/{username}")
    fun changeUsername(@Path("id") id: Int, @Path("username") username: String): Call<String?>

    @PATCH("client/{id}/password/{password}")
    fun changePassword(@Path("id") id : Int, @Path("password") password : String): Call<String?>

    @DELETE("client/{id}")
    fun deleteClient(@Path("id") id: Int) : Call<String?>
}