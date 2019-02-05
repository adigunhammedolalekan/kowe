package app.kowe.kowe.recv

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import app.kowe.kowe.L
import app.kowe.kowe.data.models.Record
import app.kowe.kowe.services.CallRecorderService

internal class CallReceiver: BroadcastReceiver() {

    companion object {

        const val ACTION_OUTGOING_CALL = "android.intent.action.NEW_OUTGOING_CALLS"
        const val EXTRA_PHONE_NUMBER = "android.intent.extra.PHONE_NUMBER"
        const val INTERNAL_ACTION_PHONE_STATE = "app.kowe.INTERNAL_ACTION"
        const val INTERNAL_EXTRA_DATA = "app.kowe.INTERNAL_DATA"
    }


    override fun onReceive(context: Context?, intent: Intent?) {

        val action = intent?.action
        val phoneState = intent?.extras?.getString(TelephonyManager.EXTRA_STATE)
        val record = Record()

        record.phoneNumber = if (action == ACTION_OUTGOING_CALL) {
            intent.extras?.getString(EXTRA_PHONE_NUMBER)
        }else {
            intent?.extras?.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
        }

        val recorderServiceIntent = Intent(context, CallRecorderService::class.java).apply {
            putExtra(INTERNAL_EXTRA_DATA, record)
            putExtra(INTERNAL_ACTION_PHONE_STATE, phoneState)
        }

        L.fine("Starting Service...")
        context?.startService(recorderServiceIntent)
    }
}