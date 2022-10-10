package com.example.optitrip.directions

import com.example.optitrip.entities.directions.Northeast
import com.google.gson.annotations.SerializedName


data class Bounds (

  @SerializedName("northeast" ) var northeast : Northeast? = Northeast(),
  @SerializedName("southwest" ) var southwest : Southwest? = Southwest()

)