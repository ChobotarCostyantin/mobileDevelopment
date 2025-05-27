package com.example.ukrainehistorylearner.utils

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.ukrainehistorylearner.data.database.ArticleEntry
import com.example.ukrainehistorylearner.data.database.ArticleEntryDao

@Database(entities = [ArticleEntry::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ArticleDataBase : RoomDatabase() {
    abstract fun articleEntryDao(): ArticleEntryDao

    companion object {
        @Volatile
        private var INSTANCE: ArticleDataBase? = null

        fun getDatabase(context: Context): ArticleDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArticleDataBase::class.java,
                    "article_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
