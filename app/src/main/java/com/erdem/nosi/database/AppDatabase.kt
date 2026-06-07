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
 *  v2 → v3: 'saved_words' tablosuna 'meaning_tr' (Türkçe anlam) sütunu eklendi.
 *  v3 → v4: 'saved_words' tablosuna 'mastery_level' (SRS öğrenme seviyesi) sütunu eklendi.
 *  v4 → v5: 'saved_sentences' tablosu eklendi (çeviri ekranından kaydedilen cümleler).
 *
 * İleride eklenebilecek entity'ler (şema yer bırakıldı):
 *  - StudySessionEntity  (öğrenme seans geçmişi)
 */
@Database(
    entities = [
        WordListEntity::class,
        SavedWordEntity::class,
        SavedSentenceEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun wordListDao(): WordListDao
    abstract fun savedWordDao(): SavedWordDao
    abstract fun savedSentenceDao(): SavedSentenceDao

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

        /** v2 → v3 Migration: saved_words tablosuna Türkçe anlam sütunu ekle */
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `saved_words` ADD COLUMN `meaning_tr` TEXT NOT NULL DEFAULT ''")
            }
        }

        /** v3 → v4 Migration: saved_words tablosuna SRS öğrenme seviyesi sütunu ekle */
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `saved_words` ADD COLUMN `mastery_level` INTEGER NOT NULL DEFAULT 0")
            }
        }

        /** v4 → v5 Migration: saved_sentences tablosunu oluştur */
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `saved_sentences` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `list_id` INTEGER NOT NULL,
                        `source_text` TEXT NOT NULL,
                        `translated_text` TEXT NOT NULL,
                        `saved_at` INTEGER NOT NULL,
                        FOREIGN KEY(`list_id`) REFERENCES `word_lists`(`id`) ON DELETE CASCADE
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_saved_sentences_list_id` ON `saved_sentences` (`list_id`)")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nosi_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}