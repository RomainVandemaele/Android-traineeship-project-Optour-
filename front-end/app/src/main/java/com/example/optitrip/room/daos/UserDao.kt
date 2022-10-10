package com.example.optitrip.room.daos

import androidx.room.*
import com.example.optitrip.entities.Client
import com.example.optitrip.room.entities.User

/**
 * DAO for [Client] in room accessible through [AppDatabase]
 *
 * @constructor Create empty Client dao
 */
@Dao
interface UserDao {

    companion object {
        const val DB_NAME = "user"
        const val COLUMN_ID = "user_id"
    }

    @Query("SELECT * FROM $DB_NAME")
    suspend fun getAll() : List<User>

    @Query("SELECT * FROM $DB_NAME WHERE $COLUMN_ID = :id")
    suspend fun getById(id : Int) : List<User>


    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update( user : User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert( user: User)



}