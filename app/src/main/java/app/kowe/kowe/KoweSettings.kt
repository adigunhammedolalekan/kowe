package app.kowe.kowe

import android.app.Application
import android.preference.PreferenceManager
import app.kowe.kowe.data.models.Account
import com.google.gson.Gson

class KoweSettings(app: Application) {

    private val preference = PreferenceManager.getDefaultSharedPreferences(app.applicationContext)
    private val editor = preference.edit()

    companion object {

        const val KEY_ENABLE_AUTO_RECORDING = "enable_auto_recording"
        const val KEY_MAKE_RECORDS_PRIVATE = "keep_records_private"
        const val KEY_SYNC_RECORDS = "sync_records"
        const val KEY_ACTIVATE_GHOST_MODE = "activate_ghost_mode"
        const val KEY_ACCOUNT = "account_key"
    }

    fun enableAutoRecording(value: Boolean) {
        editor.putBoolean(KEY_ENABLE_AUTO_RECORDING, value).apply()
    }

    fun isAutoRecordingEnabled() = preference.getBoolean(KEY_ENABLE_AUTO_RECORDING, true)

    fun setKeepRecordsPrivate(value: Boolean) {
        editor.putBoolean(KEY_MAKE_RECORDS_PRIVATE, value).apply()
    }

    fun keepRecordsPrivate() = preference.getBoolean(KEY_MAKE_RECORDS_PRIVATE, false)

    fun setSyncRecords(value: Boolean) {
        editor.putBoolean(KEY_SYNC_RECORDS, value).apply()
    }

    fun syncRecordsEnabled() = preference.getBoolean(KEY_SYNC_RECORDS, false)

    fun activateGhostMode() {
        editor.putBoolean(KEY_ACTIVATE_GHOST_MODE, true).apply()
    }

    fun deActivateGhostMode() {
        editor.putBoolean(KEY_ACTIVATE_GHOST_MODE, false).apply()
    }

    fun isInGhostMode() = preference.getBoolean(KEY_ACTIVATE_GHOST_MODE, false)

    fun putAccount(account: Account) {
        val json = Gson().toJson(account)
        editor.putString(KEY_ACCOUNT, json).apply()
    }

    fun getAuthenticatedAccount(): Account? {

        val json = preference.getString(KEY_ACCOUNT, "")
        if (json == "") return null

        return Gson().fromJson<Account>(json, Account::class.java)
    }
}