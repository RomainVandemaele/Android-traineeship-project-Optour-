package com.example.optitrip.entities.trip

import com.google.gson.annotations.SerializedName


data class Point (

  @SerializedName("point_id"   ) var pointId   : Int?    = null,
  @SerializedName("point_name" ) var pointName : String? = null,
  @SerializedName("latitude"   ) var latitude  : Double? = null,
  @SerializedName("longitude"  ) var longitude : Double? = null,
  @SerializedName("trip_id"    ) var tripId    : Int?    = null,
  @SerializedName("adress"     ) var address : String? = null,
  @SerializedName("place_id"   ) var placeId : String? = null

)