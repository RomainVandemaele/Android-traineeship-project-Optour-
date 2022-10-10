package com.example.optitrip.entities.reverseGeocoding

import com.example.optitrip.entities.reverseGeocoding.Location
import com.example.optitrip.entities.reverseGeocoding.Viewport
import com.google.gson.annotations.SerializedName


data class Geometry (

  @SerializedName("location"      ) var location     : Location? = Location(),
  @SerializedName("location_type" ) var locationType : String?   = null,
  @SerializedName("viewport"      ) var viewport     : Viewport? = Viewport()

)