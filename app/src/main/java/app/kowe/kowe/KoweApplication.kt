package app.kowe.kowe

import android.app.Application
import android.content.IntentFilter
import app.kowe.kowe.di.appModules
import app.kowe.kowe.recv.NetworkChangeReceiver
import org.koin.android.ext.android.startKoin

class KoweApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin(this, appModules)

        // Okay, so Android24+ does not
        // allow 3rd party devs to listen
        // to device connectivity change on
        // the background. It has to happen when
        // the app is still alive.

        val broadCastIntentFilter = IntentFilter().apply {
            addAction(NetworkChangeReceiver.ACTION_CONNECTIVITY_CHANGE_API_24_PLUS)
            addAction(NetworkChangeReceiver.ACTION_CONNECTIVITY_CHANGE)
        }

        registerReceiver(NetworkChangeReceiver(), broadCastIntentFilter)
    }

}