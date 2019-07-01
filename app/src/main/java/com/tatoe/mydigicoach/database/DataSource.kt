package com.tatoe.mydigicoach.database

import android.content.Context
import androidx.room.*

class DataSource {

    @Entity
    data class ExerciseEntity(
        @PrimaryKey(autoGenerate = true)
        var id: Int,

        @ColumnInfo(name = "name") var name: String,
        @ColumnInfo(name = "description") var description: String
    )

    @Dao
    interface ExerciseDao {
        @Query("SELECT * FROM exercisestable")
        fun getAll(): List<ExerciseEntity>

        @Query("SELECT * FROM exercisestable WHERE name LIKE :title")
        fun findByName(title: String): ExerciseEntity

        @Insert
        fun insertAll(vararg exercise: ExerciseEntity)

        @Insert
        fun addExercise(exercise: ExerciseEntity)

        @Delete
        fun delete(exercise: ExerciseEntity)

        @Update
        fun updateTodo(vararg exercises: ExerciseEntity)
    }

    @Database(
        entities = [ExerciseEntity::class],
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

            private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
                AppDatabase::class.java, "exercise-list.db")
                .build()
        }
    }

}