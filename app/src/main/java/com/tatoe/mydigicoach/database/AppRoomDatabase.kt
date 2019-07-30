package com.tatoe.mydigicoach.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Exercise

@Database(
    entities = [Exercise::class,Block::class],
    version = 1
)
abstract class AppRoomDatabase : RoomDatabase(){

    abstract fun exercisesDao(): ExerciseDao
    abstract fun blockDao(): BlockDao

        //todo do the migration change, change version and add the schema
    companion object {
        @Volatile private var instance: AppRoomDatabase? = null

        private val LOCK = Any()

        operator fun invoke(context: Context)= instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also { instance = it}
        }

        fun buildDatabase(context: Context) = Room.databaseBuilder(context,
            AppRoomDatabase::class.java, "exercise-list.db")
            .build()
    }
}