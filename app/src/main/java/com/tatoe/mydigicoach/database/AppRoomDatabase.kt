package com.tatoe.mydigicoach.database

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ioThread

@Database(
    entities = [Exercise::class, Block::class, Day::class],
    version = 2
)
@TypeConverters(DataConverter::class)
abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun exercisesDao(): ExerciseDao
    abstract fun blockDao(): BlockDao
    abstract fun dayDao(): DayDao

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

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        fun getInstance(context: Context): AppRoomDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }

        fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            AppRoomDatabase::class.java, "digital_coach.db"
        ).addCallback(object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                val appBlocks = Block.getPremadeBlocks()

                //todo should be replaced with coroutines
                ioThread {
                    for (block in appBlocks) {
                        getInstance(context).blockDao().addInitialBlock(block)
                    }
                }
            }
        }).fallbackToDestructiveMigration().build()

    }


}