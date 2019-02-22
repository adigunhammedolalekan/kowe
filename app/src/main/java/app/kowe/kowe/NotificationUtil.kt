package app.kowe.kowe

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import app.kowe.kowe.services.CallRecorderService
import java.util.*


object NotificationUtil {

    const val ONGOING_NOTIFICATION_ID = 5012
    val SMALL_ICON = R.drawable.ic_mic_black_24dp
    const val CHANNEL_NAME = "SmartPOS notification Channel"

    /** PendingIntent to stop the service.  */
    private fun getStopServicePI(context: Service): PendingIntent {
        val iStopService = Intent(context, CallRecorderService::class.java)
        return PendingIntent.getService(context, getRandomNumber(), iStopService, 0)
    }

    private fun createStopRecordingPI(context: Service): PendingIntent {
        val recordService = Intent(context, CallRecorderService::class.java).apply {
            putExtra(CallRecorderService.CLICK_ACTION, CallRecorderService.ACTION_STOP_RECORDING)
        }

        return PendingIntent.getService(context, getRandomNumber(), recordService, 0)
    }

    //
    // Pre O specific versions.
    //
    @TargetApi(25)
    object PreO {

        fun createNotification(context: Service) {

            // Create a notification.
            val builder = NotificationCompat.Builder(context, "PreO channel")
                    .setContentTitle(getNotificationTitle(context))
                    .setContentText(getNotificationContent(context))
                    .setSmallIcon(SMALL_ICON)
                    .setStyle(NotificationCompat.BigTextStyle())


            val stopAction = createStopAction(context)
            builder.addAction(stopAction)
            context.startForeground(ONGOING_NOTIFICATION_ID, builder.build())
        }
    }

    private fun getNotificationContent(context: Service): String {
        return context.getString(R.string.notification_text_content)
    }

    private fun getNotificationTitle(context: Service): String {
        return context.getString(R.string.notification_text_title)
    }

    //
    // Oreo and Above Specific versions.
    //

    @TargetApi(26)
    object O {

        private val CHANNEL_ID = "${getRandomNumber()}"

        fun createNotification(context: Service) {
            val channelId = createChannel(context)
            val builder = buildNotification(context, channelId)

            val stopActionButton = createStopAction(context)
            builder.addAction(stopActionButton)

            context.startForeground(ONGOING_NOTIFICATION_ID, builder.build())
        }

        private fun buildNotification(context: Service, channelId: String): NotificationCompat.Builder {

            // Create a notification.
            return NotificationCompat.Builder(context, channelId)
                    .setContentTitle(getNotificationTitle(context))
                    .setContentText(getNotificationContent(context))
                    .setSmallIcon(SMALL_ICON)
                    .setStyle(NotificationCompat.BigTextStyle())
        }

        private fun createChannel(context: Service): String {
            // Create a channel.
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            notificationManager.createNotificationChannel(notificationChannel)
            return CHANNEL_ID
        }
    }

    private fun createStopAction(context: Service): NotificationCompat.Action {
        val restartText = "Stop Recording"
        return NotificationCompat.Action.Builder(0, restartText, createStopRecordingPI(context)).build()
    }

    private fun getRandomNumber(): Int {
        return Random().nextInt(100000)
    }
}