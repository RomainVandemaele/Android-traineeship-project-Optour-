package com.example.optitrip.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey @ColumnInfo(name="user_id")val id : Int,
    @ColumnInfo(name="username") val username : String,
    @ColumnInfo(name ="password") val password : String
)
