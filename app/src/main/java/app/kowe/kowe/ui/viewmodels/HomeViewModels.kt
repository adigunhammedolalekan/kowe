package app.kowe.kowe.ui.viewmodels

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import app.kowe.kowe.KoweApplication
import app.kowe.kowe.L
import app.kowe.kowe.Util
import app.kowe.kowe.data.locals.RecordRepository
import app.kowe.kowe.data.models.Record
import java.util.concurrent.ExecutorService

class HomeViewModels(private val executor: ExecutorService, private val repo: RecordRepository) : ViewModel() {

    private val recordLiveData = MutableLiveData<List<Record>>()

    fun fetchRecords() {

        executor.execute {
            val data = repo.fetchRecords()
            data.forEach { record ->

                if (record.phoneNumber != null) {
                    record.contactName = Util.inferContactName(KoweApplication.getApplication(),
                            record.phoneNumber!!)
                }

                record.readableTime = Util.format(record.recordStartTime)
                record.callDuration = Util.getDuration(record)

                L.fine(record.contactName)
            }
            recordLiveData.postValue(data)
        }
    }

    fun liveData() = recordLiveData
}