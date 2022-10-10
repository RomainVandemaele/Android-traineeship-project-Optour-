package com.example.optitrip.entities.trip

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

//TODO remove nullable where can be

/**
 * Class representing a trip in details with its points and steps
 *
 * @property tripId
 * @property tripName
 * @property clientId
 * @property grade
 * @property steps
 * @property points
 */
data class Trip (
  @SerializedName("trip_id"   ) var tripId   : Int?              = null,
  @SerializedName("trip_name" ) var tripName : String?           = null,
  @SerializedName("client_id" ) var clientId : Int?              = null,
  @SerializedName("grade"     ) var grade    : Int?              = null,
  @SerializedName("steps"     ) var steps    : ArrayList<Step>  = arrayListOf(),
  @SerializedName("points"    ) var points   : ArrayList<Point> = arrayListOf()

)