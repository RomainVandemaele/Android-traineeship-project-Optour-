package com.example.optitrip.entities.mapSearch

import com.google.gson.annotations.SerializedName


    data class Candidates (

        @SerializedName("formatted_address" ) var formattedAddress : String?           = null,
        @SerializedName("geometry"          ) var geometry         : Geometry?         = Geometry(),
        @SerializedName("name"              ) var name             : String?           = null,
        @SerializedName("opening_hours"     ) var openingHours     : OpeningHours?     = OpeningHours(),
        @SerializedName("types"             ) var types            : ArrayList<String> = arrayListOf()

    )

