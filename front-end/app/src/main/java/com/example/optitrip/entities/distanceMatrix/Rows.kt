package com.example.optitrip.entities.distanceMatrix

import com.google.gson.annotations.SerializedName


data class Rows (

  @SerializedName("elements" ) var elements : ArrayList<Elements> = arrayListOf()

)