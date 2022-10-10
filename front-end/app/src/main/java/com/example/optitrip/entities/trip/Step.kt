package com.example.optitrip.entities.trip

import com.google.gson.annotations.SerializedName


data class Step (

  @SerializedName("start_point_id" ) var startPointId : Int?    = null,
  @SerializedName("end_point_id"   ) var endPointId   : Int?    = null,
  @SerializedName("step_time"      ) var stepTime     : Int?    = null,
  @SerializedName("step_length"    ) var stepLength   : Double? = null,
  @SerializedName("step_mode"      ) var stepMode     : String? = null,
  @SerializedName("step_order"     ) var stepOrder    : Int?    = null,
  @SerializedName("trip_id"        ) var tripId       : Int?    = null

)