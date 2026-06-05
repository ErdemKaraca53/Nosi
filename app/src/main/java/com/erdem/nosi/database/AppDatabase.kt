package com.erdem.nosi.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Uygulamanın ana Room veritabanı.
 *
 * Versiyon geçmişi:
 *  v1 → v2: Eski placeholder 'word' tablosu kaldırıldı.
 *            'word_lists' ve 'saved_words' tabloları oluşturuldu.
 *
 * İleride eklenebilecek entity'ler (şema yer bırakıldı):
 *  - StudySessionEntity  (öğrenme seans geçmişi)
 *  - WordProgressEntity  (SRS tabanlı kelime ilerleme durumu)
 */
@Database(
    entities = [
        WordListEntity::class,
        SavedWordEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun wordListDao(): WordListDao
    abstract fun savedWordDao(): SavedWordDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        /** v1 → v2 Migration: eski 'word' tablosunu sil, yeni tabloları oluştur */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Eski placeholder tabloyu kaldır
                db.execSQL("DROP TABLE IF EXISTS `word`")

                // word_lists tablosu
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `word_lists` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `emoji` TEXT NOT NULL DEFAULT '📚',
                        `description` TEXT NOT NULL DEFAULT '',
                        `color` TEXT NOT NULL DEFAULT '#0D9488',
                        `created_at` INTEGER NOT NULL
                    )
                """.trimIndent())

                // saved_words tablosu
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `saved_words` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `list_id` INTEGER NOT NULL,
                        `word` TEXT NOT NULL,
                        `part_of_speech` TEXT NOT NULL,
                        `definitions_json` TEXT NOT NULL DEFAULT '[]',
                        `synonyms_json` TEXT NOT NULL DEFAULT '[]',
                        `antonyms_json` TEXT NOT NULL DEFAULT '[]',
                        `saved_at` INTEGER NOT NULL,
                        FOREIGN KEY(`list_id`) REFERENCES `word_lists`(`id`) ON DELETE CASCADE
                    )
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS `index_saved_words_list_id` ON `saved_words` (`list_id`)")
                db.execSQL("""
                    CREATE UNIQUE INDEX IF NOT EXISTS `index_saved_words_list_id_word_part_of_speech`
                    ON `saved_words` (`list_id`, `word`, `part_of_speech`)
                """.trimIndent())
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nosi_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}