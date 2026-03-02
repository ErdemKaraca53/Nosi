package com.erdem.nosi.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CollectionEntity::class, SavedTranslationEntity::class, SavedWordEntity::class],
    version = 2,
    exportSchema = false
)
abstract class NosiDatabase : RoomDatabase() {

    abstract fun translationDao(): TranslationDao

    companion object {
        @Volatile
        private var INSTANCE: NosiDatabase? = null

        fun getInstance(context: Context): NosiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NosiDatabase::class.java,
                    "nosi_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
