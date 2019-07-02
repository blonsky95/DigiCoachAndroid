package com.tatoe.mydigicoach.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tatoe.mydigicoach.entity.Exercise

@Database(
    entities = [Exercise::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase(){

    abstract fun ExercisesDao(): ExerciseDao

    companion object {
        @Volatile private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context)= instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also { instance = it}
        }

        fun buildDatabase(context: Context) = Room.databaseBuilder(context,
            AppDatabase::class.java, "exercise-list.db")
            .build()
    }
}