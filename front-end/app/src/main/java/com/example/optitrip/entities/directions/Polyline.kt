package com.example.optitrip.entities.directions

import com.google.gson.annotations.SerializedName


data class Polyline (

  @SerializedName("points" ) var points : String? = null

)