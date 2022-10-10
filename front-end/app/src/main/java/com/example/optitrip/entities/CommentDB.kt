package com.example.optitrip.entities

import com.google.gson.annotations.SerializedName

/**
 * Class representing a comment from a user.
 * Properties follow the DB columns.
 *
 * @property tripID
 * @property clientId
 * @property comment
 */
data class CommentDB(
    @SerializedName("trip_id"  ) var tripID  : Int,
    @SerializedName("client_id" ) var clientId : Int,
    @SerializedName("comment" ) var comment : String
)
