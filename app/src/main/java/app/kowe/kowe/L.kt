package app.kowe.kowe

import android.util.Log

public class L {

    companion object {

        val TAG = "Kowe"
        fun fine(message: String?) {
            Log.d(TAG, message)
        }
        fun error(e: Throwable?) {
            Log.d(TAG, "Error", e)
        }
    }
}