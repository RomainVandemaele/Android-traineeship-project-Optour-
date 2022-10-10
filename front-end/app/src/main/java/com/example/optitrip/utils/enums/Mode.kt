package com.example.optitrip.utils.enums

/**
 * Enuml of the available modes of transport
 *
 * @property mode the text to selct one mode
 * @property mapQuery the text for the api call
 */
enum class Mode(val mode : String,val mapQuery: String) {
    DRIVING("driving","driving"),
    WALKING("walking", "walking"),
    BIKE("bike","bicycling"),
    TRANSPORT("public transport","transit")
}