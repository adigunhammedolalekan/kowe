package app.kowe.kowe.data.locals.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import app.kowe.kowe.data.models.Record

@Dao
interface RecordDao {

    @Insert
    fun insertRecord(record: Record): Long

    @Query("SELECT * FROM records_data ORDER BY recordId DESC")
    fun fetchRecords(): List<Record>

    @Query("SELECT * FROM records_data WHERE recordId = :id")
    fun getRecord(id: Int?): Record?

    @Query("SELECT * FROM records_data WHERE synced = :synced")
    fun getUnSyncedRecords(synced: Boolean = false): List<Record>

    @Query("UPDATE records_data SET synced = :sn AND recordId = :id")
    fun markSynced(id: Long, sn: Boolean = true)
}