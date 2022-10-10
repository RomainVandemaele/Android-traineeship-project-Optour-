package com.example.optitrip.entities

import android.os.Parcel
import android.os.Parcelable
import com.example.optitrip.entities.reverseGeocoding.GeoCodingResult
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class Position() : Parcelable {

     var  markerTitle : String = ""
     var  markerSnippet : String = ""
     var  markerTag : String = ""
     var  markerLat : Double = 0.0
     var  markerLong : Double = 0.0

    private var infos : GeoCodingResult? = null

    constructor(marker: Marker) : this() {
        this.markerTitle = marker.title.toString()
        this.markerSnippet = marker.snippet.toString()
        this.markerTag = marker.tag.toString()
        this.markerLat = marker.position.latitude
        this.markerLong = marker.position.longitude

    }

    constructor(parcel: Parcel) : this() {
        this.markerTitle = parcel.readString()!!
        this.markerSnippet = parcel.readString()!!
        this.markerTag = parcel.readString()!!
        this.markerLat = parcel.readDouble()
        this.markerLong = parcel.readDouble()

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(out: Parcel?, flags: Int) {
        if (out != null) {
            out.writeString(markerTitle)
            out.writeString(markerSnippet)
            out.writeString(markerTag)
            out.writeDouble(markerLat)
            out.writeDouble(markerLong)
        }
    }

    companion object CREATOR : Parcelable.Creator<Position> {
        override fun createFromParcel(parcel: Parcel): Position {
            return Position(parcel)
        }

        override fun newArray(size: Int): Array<Position?> {
            return arrayOfNulls(size)
        }
    }
}