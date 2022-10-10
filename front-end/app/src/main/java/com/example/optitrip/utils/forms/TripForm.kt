package com.example.optitrip.utils.forms

import com.example.optitrip.utils.annotations.Required

class TripForm  (
    @Required
    val name : String?,
    @Required
    val mode : String?
) : FormModel(){}