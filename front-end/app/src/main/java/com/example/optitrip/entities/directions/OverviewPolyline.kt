package com.example.optitrip.entities.directions

import com.google.gson.annotations.SerializedName


data class OverviewPolyline (

  @SerializedName("points" ) var points : String? = null

)