package com.example.optitrip.room.daos

import androidx.room.*
import com.example.optitrip.room.entities.TripDB

@Dao
interface TripDao {

    companion object {
        const val DB_NAME = "trip"
        const val COLUMN_ID = "trip_id"
        const val FOREIGN_KEY = "user_id"
    }

    @Query("SELECT * FROM $DB_NAME WHERE $FOREIGN_KEY = :userId")
    suspend fun getTripByUser(userId : Int) : List<TripDB>

    @Query("DELETE FROM $DB_NAME")
    suspend fun deleteAll()

    @Insert
    suspend fun insert(vararg trips : TripDB)

    @Update
    suspend fun update(vararg trips : TripDB)

    @Delete
    suspend fun delete(vararg trips : TripDB)

}