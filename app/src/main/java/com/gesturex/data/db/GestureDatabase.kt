package com.gesturex.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gesturex.data.model.Gesture

@Database(entities = [Gesture::class], version = 1, exportSchema = false)
abstract class GestureDatabase : RoomDatabase() {
    abstract fun gestureDao(): GestureDao

    companion object {
        @Volatile private var INSTANCE: GestureDatabase? = null
        fun getInstance(ctx: Context): GestureDatabase = INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(ctx.applicationContext, GestureDatabase::class.java, "gesturex_db")
                .build().also { INSTANCE = it }
        }
    }
}
