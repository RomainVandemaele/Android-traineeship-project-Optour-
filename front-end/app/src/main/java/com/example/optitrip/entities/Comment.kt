package com.example.optitrip.entities

import com.google.gson.annotations.SerializedName

/**
 * Class representing a comment from a user.
 * Used as a container from [CommentAPI].
 *
 *
 * @property comment the comment as String
 * @property username the username of the user that posted it
 */
data class Comment (

    @SerializedName("comment"  ) var comment  : String,
    @SerializedName("username" ) var username : String

)
