package app.kowe.kowe.ui.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import app.kowe.kowe.data.locals.RecordRepository
import app.kowe.kowe.data.models.Record
import java.util.concurrent.ExecutorService

class HomeViewModels(private val executor: ExecutorService, private val repo: RecordRepository) : ViewModel() {

    private val recordLiveData = MutableLiveData<List<Record>>()

    fun fetchRecords() {

        executor.execute {
            val data = repo.fetchRecords()
            recordLiveData.postValue(data)
        }
    }

    fun liveData() = recordLiveData
}