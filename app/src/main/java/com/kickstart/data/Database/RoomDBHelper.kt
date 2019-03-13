package com.kickstart.data.Database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import android.util.Log
import com.kickstart.constants.DATABASE_NAME
import com.kickstart.data.Bean.ScoreBean
import com.kickstart.data.Bean.WSCategoryBean
import com.kickstart.data.Bean.WSQuestionsBean
import com.kickstart.data.Dao.CategoryDao
import com.kickstart.data.Dao.ScoreDao
import com.kickstart.data.Dao.WordsDao
import java.io.FileOutputStream
import java.io.IOException


/**
 * Created by sotsys-055 on 8/2/18.
 */

@Database(entities = arrayOf(ScoreBean::class, WSQuestionsBean::class, WSCategoryBean::class), version = 1, exportSchema = false)
abstract class RoomDBHelper : RoomDatabase() {

    abstract fun userDao(): ScoreDao
    abstract fun transactionsDao(): WordsDao
    abstract fun categoryDao(): CategoryDao

    companion object {

        lateinit var sInstance: RoomDBHelper
        var DB_PATH = ""

        fun getInstance(context: Context): RoomDBHelper {
//            return copyAttachedDatabase(context)
            return roomInstance(context)
        }

        fun copyAttachedDatabase(context: Context): RoomDBHelper {
            val dbPath = context.getDatabasePath(DATABASE_NAME)

            // If the database already exists, return
            if (dbPath.exists()) {
                return roomInstance(context)
            }

            // Make sure we have a path to the file
            dbPath.parentFile.mkdirs()

            // Try to copy database file
            try {
                val inputStream = context.assets.open(DATABASE_NAME)
                val output = FileOutputStream(dbPath)

                val buffer = ByteArray(1024)
                var length: Int = 0

                while ({ length = inputStream.read(buffer, 0, 1024);length }() > 0) {
                    output.write(buffer, 0, length)
                }

                output.flush()
                output.close()
                inputStream.close()
            } catch (e: IOException) {
                Log.d("NO", "Failed to open file", e)
                e.printStackTrace()
            }

            return roomInstance(context)
        }

        fun roomInstance(context: Context): RoomDBHelper {
            try {
                if (sInstance != null) {
                    return sInstance
                } else {
                    return sInstance //Patch
                }
            } catch (e: Exception) {
                Log.d("DB", "DB initialized")
                sInstance = Room.databaseBuilder(context, RoomDBHelper::class.java, DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build()

                return sInstance
            }
        }
    }


}