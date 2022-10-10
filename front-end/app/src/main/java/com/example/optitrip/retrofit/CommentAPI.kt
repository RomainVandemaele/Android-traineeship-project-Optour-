package com.example.optitrip.retrofit

import com.example.optitrip.entities.Comment
import com.example.optitrip.entities.CommentDB
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


/**
 * Retrofit APi to communicate with the REST API used for DB
 * this API is responsible for GET and POSt comments on a [Trip]
 *
 */
interface CommentAPI {

    /**
     * Function retrieving all comments on a trip
     *
     * @param id the trip's id
     * @return An array of [Comment]
     */
    @GET("comments/{id}")
    fun getTripComments(@Path("id") id : Int) : Call<Array<Comment>?>

    /**
     * Function posting a comment to a trip
     *
     * @param comment
     * @return the comment string
     */
    @POST("comment")
    fun postComment(@Body comment: CommentDB) : Call<String?>
}