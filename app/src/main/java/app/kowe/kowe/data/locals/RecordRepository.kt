package app.kowe.kowe.data.locals

import app.kowe.kowe.data.locals.dao.RecordDao
import app.kowe.kowe.data.models.Record

interface RecordRepository {

    fun addRecord(record: Record)

    fun fetchRecords(): List<Record>

    fun getRecord(id: Int?): Record

    fun getUnSynced(): List<Record>

    fun markSynced(id: Long)
}

class RecordRepositoryImpl(val database: RecordDao) : RecordRepository {

    override fun markSynced(id: Long) {
        database.markSynced(id)
    }

    override fun getUnSynced(): List<Record> {
        return database.getUnSyncedRecords()
    }

    override fun addRecord(record: Record) {
       database.insertRecord(record)
    }

    override fun fetchRecords(): List<Record> {
        return database.fetchRecords()
    }

    override fun getRecord(id: Int?): Record {
        return database.getRecord(id)!!
    }
}