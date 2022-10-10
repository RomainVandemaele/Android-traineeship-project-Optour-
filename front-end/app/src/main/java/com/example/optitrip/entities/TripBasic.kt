package com.example.optitrip.entities

import com.google.gson.annotations.SerializedName

/**
 * Class representing a trip with minimal information.
 * Linked to a API request from [TripAPI]
 *
 * @property tripId
 * @property tripName
 * @property clientId
 * @property grade
 * @property startAddress
 * @property endAddress
 */
data class TripBasic(
    @SerializedName("trip_id"   ) var tripId   : Int,
    @SerializedName("trip_name" ) var tripName : String,
    @SerializedName("client_id" ) var clientId : Int,
    @SerializedName("grade"     ) var grade    : Int,
    @SerializedName("start_adress"     ) var startAddress    : String,
    @SerializedName("end_adress"     ) var endAddress    : String
)
