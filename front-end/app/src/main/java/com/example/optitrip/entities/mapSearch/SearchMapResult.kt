package com.example.optitrip.entities.mapSearch

import com.google.gson.annotations.SerializedName


data class SearchMapResult (

  @SerializedName("candidates" ) var candidates : ArrayList<Candidates> = arrayListOf(),
  @SerializedName("status"     ) var status     : String?               = null

)