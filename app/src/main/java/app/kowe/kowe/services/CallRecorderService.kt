package app.kowe.kowe.services

import android.Manifest
import android.app.Application
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.telephony.TelephonyManager
import app.kowe.kowe.KoweSettings
import app.kowe.kowe.L
import app.kowe.kowe.data.locals.RecordRepository
import app.kowe.kowe.data.models.Record
import app.kowe.kowe.recv.CallReceiver
import app.kowe.kowe.NotificationUtil
import org.koin.android.ext.android.inject
import java.io.File
import java.util.*
import java.util.concurrent.ExecutorService

internal class CallRecorderService: Service() {

    private val settings: KoweSettings by inject()
    private val executor: ExecutorService by inject()
    private val repo: RecordRepository by inject()


    private var currentState = STATE_RECORDING_STOPPED
    private lateinit var recorder: MediaRecorder
    private lateinit var record: Record
    private lateinit var recordFile: File
    private lateinit var app: Application


    companion object {

        const val STATE_RECORDING = 1
        const val STATE_RECORDING_STOPPED = 2
        const val RECORDS_FOLDER_NAME = "KoweData"
        const val CLICK_ACTION = "NotificationClickAction"
        const val ACTION_STOP_RECORDING = "stop_recording"
    }

    override fun onBind(p0: Intent?) = null


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        app = applicationContext as Application
        if (intent == null) {
            return START_NOT_STICKY
        }
        val notificationAction = intent.extras.getString(CLICK_ACTION, "")
        if (notificationAction == ACTION_STOP_RECORDING) {
            stopRecording()
            return START_NOT_STICKY
        }

        val phoneState = intent.extras.getString(CallReceiver.INTERNAL_ACTION_PHONE_STATE)
        record = intent.extras.getParcelable(CallReceiver.INTERNAL_EXTRA_DATA)

        if (currentState == STATE_RECORDING) {
            return START_NOT_STICKY
        }

        when(phoneState) {

            TelephonyManager.EXTRA_STATE_OFFHOOK -> {

                if (prepareRecorder()) {

                    startRecording()

                    record.recordStartTime = Date().time
                    record.savedPath = recordFile.absolutePath
                    record.synced = false
                }else {

                    stopRecording()
                    releaseMediaRecorder()
                }
            }

            TelephonyManager.EXTRA_STATE_IDLE -> {

                stopRecording()
                releaseMediaRecorder()

            } else -> {

            }
        }

        return START_STICKY
    }

    private fun startRecording() {

        if (currentState == STATE_RECORDING) return

        val permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        if (permissionState == PackageManager.PERMISSION_DENIED) return

        try {

            showForeGroundNotification()
            recorder.start()
            currentState = STATE_RECORDING
        }catch (e: Exception) {
            L.error(e)
        }
    }

    private fun showForeGroundNotification() {

        val isPre0 = Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1

        if (isPre0) NotificationUtil.PreO.createNotification(this)
        else NotificationUtil.O.createNotification(this)
    }

    private fun dismissForeGroundNotification() {

        stopForeground(true)
        stopSelf()
    }

    private fun prepareRecorder(): Boolean {

        recordFile = createUniqueFile()
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
            setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
            setOutputFile(recordFile.absolutePath)
            setOnErrorListener { mediaRecorder, i, i1 ->

                // error occurred
                // just delete the created file
                recordFile.delete()
            }
        }

        try {

            recorder.prepare()
        }catch (e: Exception) {
            L.error(e)

            return false
        }

        return true
    }

    private fun createUniqueFile(): File {

        app = applicationContext as Application
        try {

            val recordFolder = if (!settings.keepRecordsPrivate()) {
                Environment.DIRECTORY_MUSIC
            }else {
                app.cacheDir.absolutePath
            }

            val path = recordFolder + File.separator + RECORDS_FOLDER_NAME
            val folder = File(path)

            if (!folder.exists()) {
                folder.mkdirs()
            }

            val id = "Record" + UUID.randomUUID().toString() + ".amr"
            return File(folder, id)
        }catch (e: Exception) {
            L.error(e)
        }

        return File(UUID.randomUUID().toString())
    }

    private fun releaseMediaRecorder() {

        if (currentState == STATE_RECORDING_STOPPED) return
        try {

            recorder.apply {
                reset()
                release()
            }

            currentState = STATE_RECORDING_STOPPED
        }catch (e: Exception) { }
    }

    private fun stopRecording() {

        L.fine("Stops Recording!")
        try {

            recorder.apply {
                stop()
            }

            currentState = STATE_RECORDING_STOPPED
            dismissForeGroundNotification()
            saveRecordedFile()
        }catch (e: Exception) {}
    }

    private fun saveRecordedFile() {

        record.recordStopTime = Date().time

        executor.execute {
            repo.addRecord(record)
        }
    }
}