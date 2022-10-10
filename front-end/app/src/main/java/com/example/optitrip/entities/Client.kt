package com.example.optitrip.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


/**
 * Class representing a Client of the app.
 * Its structure follows the table Client in the DB to allow retrieval/upload via [ClientAPI]
 *
 * @property clientId
 * @property username
 * @property firstName
 * @property lastName
 * @property mail
 * @property password
 * @property pending
 * @property token
 */
data class Client (
    @SerializedName("client_id"  )   var clientId  : Int?,
    @SerializedName("username"   )   var username  : String,
    @SerializedName("first_name" )   var firstName : String,
    @SerializedName("last_name"  )   var lastName  : String,
    @SerializedName("mail"       )   var mail      : String,
    @SerializedName("password"   )   var password  : String,
    @SerializedName("pending"    )   var pending   : Boolean,
    @SerializedName("token"      )   var token     : String = ""
)
