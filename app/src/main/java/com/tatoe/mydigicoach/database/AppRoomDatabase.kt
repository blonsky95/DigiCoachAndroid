package com.tatoe.mydigicoach.database

import android.content.Context
import androidx.room.*
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.entity.Friend

@Database(
    entities = [Exercise::class, Day::class, Friend::class],
    version = 3
)
@TypeConverters(DataConverter::class)
abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun exercisesDao(): ExerciseDao
    abstract fun dayDao(): DayDao
    abstract fun friendDao(): FriendDao

    // do the migration change, change version and add the schema
    companion object {
        @Volatile
        private var instance: AppRoomDatabase? = null

        private val LOCK = Any()

//        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("CREATE TABLE IF NOT EXISTS `block_table` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `question_id` INTEGER NOT NULL, `answer` TEXT NOT NULL, FOREIGN KEY(`question_id`) REFERENCES `question`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
//            }
//        }

        operator fun invoke(context: Context, userId:String) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context, userId).also { instance = it }
        }

        fun getInstance(context: Context, userId:String): AppRoomDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context, userId).also { instance = it }
            }

        fun buildDatabase(context: Context, userId: String) = Room.databaseBuilder(
            context,
            AppRoomDatabase::class.java, "${userId}_digital_coach.db"
        ).addCallback(object : Callback() {
        }).fallbackToDestructiveMigration().build()

        /**
        *call this function to close the db instance - called when log out -
         * so different user can access their own db - even when not closing app
         */
        fun destroyInstance() {
            if (instance?.isOpen==true) {
                instance?.close()
            }
            instance=null
        }

    }


}