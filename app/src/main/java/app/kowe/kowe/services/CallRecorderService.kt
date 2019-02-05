package app.kowe.kowe.services

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Environment
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.telephony.TelephonyManager
import app.kowe.kowe.KoweSettings
import app.kowe.kowe.L
import app.kowe.kowe.data.locals.RecordRepository
import app.kowe.kowe.data.models.Record
import app.kowe.kowe.recv.CallReceiver
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
    private val recordFile = createUniqueFile()
    private var context: Context = this


    companion object {

        const val STATE_RECORDING = 1
        const val STATE_RECORDING_STOPPED = 2
        const val RECORDS_FOLDER_NAME = "KoweData"

    }

    override fun onBind(p0: Intent?) = null


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        context = this
        if (intent == null) {
            return START_NOT_STICKY
        }

        val phoneState = intent.extras.getString(CallReceiver.INTERNAL_ACTION_PHONE_STATE)
        record = intent.extras.getParcelable(CallReceiver.INTERNAL_EXTRA_DATA)

        L.fine("Phone state is => " + phoneState)
        if (currentState == STATE_RECORDING) {
            return START_NOT_STICKY
        }

        L.fine("Starting or stopping records")
        when(phoneState) {

            TelephonyManager.EXTRA_STATE_OFFHOOK -> {

                if (prepareRecorder()) {

                    startRecording()

                    record.recordStartTime = Date().time
                    record.savedPath = recordFile?.absolutePath
                    record.synced = false
                }else {

                    stopRecording()
                    releaseMediaRecorder()
                }
            }

            TelephonyManager.EXTRA_STATE_IDLE -> {

                stopRecording()
                releaseMediaRecorder()

                saveRecordedFile()
            } else -> {
                L.fine("UnHandled => " + phoneState)
            }
        }

        return START_STICKY
    }

    private fun startRecording() {

        L.fine("Starts Recording")
        if (currentState == STATE_RECORDING) return

        val permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        if (permissionState == PackageManager.PERMISSION_DENIED) return

        if (prepareRecorder()) {

            try {

                recorder.start()
                currentState = STATE_RECORDING
            }catch (e: Exception) {
                L.error(e)
            }
        }
    }

    private fun prepareRecorder(): Boolean {

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB)
            setOutputFile(recordFile?.absolutePath)
            setOnErrorListener { mediaRecorder, i, i1 ->

                // error occurred
                // just delete the created file
                recordFile?.delete()
                L.fine("Error!")
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

    private fun createUniqueFile(): File? {

        try {

            val recordFolder = if (!settings.keepRecordsPrivate()) {
                Environment.DIRECTORY_MUSIC
            }else {
                context.cacheDir.absolutePath
            }

            val path = recordFolder + File.separator + RECORDS_FOLDER_NAME
            val folder = File(path)

            if (!folder.exists()) {
                folder.mkdirs()
            }
            L.fine("Path => ${folder.absolutePath}, Exits => ${folder.exists()}")

            val id = "Record-" + UUID.randomUUID().toString() + ".amr"
            val file = File(folder, id)
            file.createNewFile()

            return file
        }catch (e: Exception) {
            L.error(e)
        }

        return null
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
        }catch (e: Exception) {}
    }

    private fun saveRecordedFile() {

        record.recordStopTime = Date().time

        executor.execute {
            repo.addRecord(record)
        }
    }
}