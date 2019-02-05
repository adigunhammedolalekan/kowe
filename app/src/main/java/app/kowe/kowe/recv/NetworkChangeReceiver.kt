package app.kowe.kowe.recv

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import app.kowe.kowe.services.CallSyncService

class NetworkChangeReceiver: BroadcastReceiver() {


    companion object {

        const val ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE"
        const val ACTION_CONNECTIVITY_CHANGE_API_24_PLUS = ConnectivityManager.CONNECTIVITY_ACTION
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        // just check if there is internet connection
        // and start sync service if true

        val action = intent?.action
        if (action == ACTION_CONNECTIVITY_CHANGE
            || action == ACTION_CONNECTIVITY_CHANGE_API_24_PLUS) {

            val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = connectivityManager.activeNetworkInfo

            if (netInfo.isConnectedOrConnecting) {

                val syncServiceIntent = Intent(context, CallSyncService::class.java)
                context.startService(syncServiceIntent)
            }
        }
    }
}