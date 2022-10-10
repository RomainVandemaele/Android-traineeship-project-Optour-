package com.example.optitrip.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.optitrip.room.daos.TripDao

import com.example.optitrip.room.daos.UserDao
import com.example.optitrip.room.entities.User
import com.example.optitrip.room.entities.TripDB


@Database(
    entities = [User::class,TripDB::class],
    version = 1,
    exportSchema = false
)
/**
 * Class [AppDatabase] is a singleton responsible for the local sqlite database and entry point for all DAO
 *
 */
abstract class AppDatabase : RoomDatabase() {
    //daos
    abstract fun UserDao() : UserDao
    abstract fun TripDao() : TripDao

    companion object {
        const val DB_NAME = "Optitour"
        private var instance : AppDatabase? = null

        fun instance(context: Context) : AppDatabase {
            if(instance == null) {
                val room = Room.databaseBuilder(context,AppDatabase::class.java, DB_NAME).fallbackToDestructiveMigration()
                instance = room.build()
            }
            return instance!!
        }


    }
}