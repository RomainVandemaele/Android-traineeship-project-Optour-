package com.example.optitrip.entities.reverseGeocoding

import com.google.gson.annotations.SerializedName


data class GeoCodingResult (

  @SerializedName("plus_code" ) var plusCode : PlusCode?          = PlusCode(),
  @SerializedName("results"   ) var results  : ArrayList<Results> = arrayListOf(),
  @SerializedName("status"    ) var status   : String?            = null

)