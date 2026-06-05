package com.erdem.nosi.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Word::class],
    version = 1
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun wordDao() : WordDao

    /*Bir sınıfın kendisine bağlı olan ama o sınıfın örneğini oluşturmadan kullanabileceğimiz
      metot ya da değişkenleri tanımlamak için kullanılır.

      Özellikleri
      -Bir sınıftan new vey benzeri bir yolla nesne üretmenize gerek kalmaz.
      -Sİngelton yapısındadır. İçerisinde üretilen elemanlar arka plande tek bir örneğe sahiptir. Bellekte bir kez yer alır.
    */

    //reposirtor sınıfı oluştur. parametre olarak database ver.
    //viewmodel içerisinde repositoyry kullnarak verileri çek.
    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE

            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "word_database"
                ).build()
                INSTANCE = instance
                return instance
            }

        }
    }

}