package app.kowe.kowe.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import app.kowe.kowe.KoweSettings
import app.kowe.kowe.data.locals.RecordRepository
import app.kowe.kowe.services.http.HttpService
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.android.ext.android.inject
import java.io.File
import java.util.concurrent.ExecutorService

internal class CallSyncService: Service() {

    private val repo: RecordRepository by inject()
    private val executor: ExecutorService by inject()
    private val httpService: HttpService by inject()
    private val settings: KoweSettings by inject()

    override fun onBind(p0: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (!settings.syncRecordsEnabled() && !settings.isInGhostMode()) {
            stopSelf()
            return START_NOT_STICKY
        }

        executor.execute {

            try {

                val unSynceds = repo.getUnSynced()
                unSynceds.forEachIndexed { index, record ->

                    val body = MultipartBody.Part.createFormData("record-$index", record.savedPath,
                            RequestBody.create(MediaType.parse(""), File(record.savedPath)))
                    val response = httpService.upload(body).execute()
                    if (response.isSuccessful) {

                        record.remoteUrl = response.body()?.data
                        val saveResponse = httpService.saveRecord(record).execute()

                        if (saveResponse.isSuccessful) {

                            // ok, so we successfully upload
                            // record on the server. Mark it as synced
                            // in our local db
                            repo.markSynced(record.recordId)
                        }
                    }
                }
            }catch (e: Exception) {

            }
        }
        return START_STICKY
    }
}