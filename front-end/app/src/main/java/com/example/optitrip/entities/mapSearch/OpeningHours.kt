package com.example.optitrip.entities.mapSearch

import com.google.gson.annotations.SerializedName


data class OpeningHours (
    @SerializedName("open_now" ) var openNow : Boolean? = null
)
