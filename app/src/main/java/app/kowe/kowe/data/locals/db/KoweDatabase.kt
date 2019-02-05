package app.kowe.kowe.data.locals.db

import android.app.Application
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import app.kowe.kowe.data.locals.dao.RecordDao
import app.kowe.kowe.data.models.Record

@Database(entities = arrayOf(Record::class), version = 2, exportSchema = false)
internal abstract class KoweDatabase: RoomDatabase() {

    companion object {

        private lateinit var INSTANCE: KoweDatabase
        private const val DATABASE_NAME = "koweeeeeeeee.db"

        fun initDatabase(app: Application) {

            synchronized(this) {
                INSTANCE = Room.databaseBuilder(app.applicationContext!!,
                        KoweDatabase::class.java, DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build()
            }
        }

        fun getInstance(app: Application): KoweDatabase {
            initDatabase(app)

            return INSTANCE
        }
    }

    abstract fun recordDao(): RecordDao
}