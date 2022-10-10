package com.example.optitrip.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a trip in the local database.
 * it means that it contains trips from the users using the app on that smartphone
 *
 *
 * @property id
 * @property tripName
 * @property grade
 * @property userId
 * @constructor Create empty Trip
 */
@Entity(tableName = "trip")
data class TripDB(
    @PrimaryKey @ColumnInfo(name="trip_id") val id : Int,
    @ColumnInfo(name = "trip_name")  val tripName : String,
    @ColumnInfo(name="grade") val grade : Int,
    @ColumnInfo(name= "user_id") val userId : Int
)
