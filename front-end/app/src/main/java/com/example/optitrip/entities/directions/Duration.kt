package com.example.optitrip.entities.directions

import com.google.gson.annotations.SerializedName


data class Duration (

  @SerializedName("text"  ) var text  : String? = null,
  @SerializedName("value" ) var value : Int?    = null

)