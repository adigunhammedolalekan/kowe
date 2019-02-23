package app.kowe.kowe

import android.app.Application
import android.provider.ContactsContract
import app.kowe.kowe.data.models.Record
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object Util {

    val SDF = SimpleDateFormat("MMM dd 2019, HH:mm", Locale.getDefault())

    fun formatCurrentTime() = SDF.format(Calendar.getInstance().time)

    fun format(date: Date) = SDF.format(date.time)

    fun format(t: Long) = SDF.format(t)


    fun inferContactName(app: KoweApplication, phone: String): String {

        try {

            val phoneQueryProjections = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID)

            val phoneCursor = app.applicationContext.contentResolver?.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    phoneQueryProjections,
                    phoneQueryProjections[0] + " = ?",
                    arrayOf(phone),
                    null
            )

            val contactId = phoneCursor?.getString(phoneCursor.getColumnIndex(phoneQueryProjections[1]))
            val projections = arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME)
            val cursor = app.applicationContext.contentResolver?.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    projections,
                    projections[0] + " = ?",
                    arrayOf(contactId),
                    null
            )

           return if (cursor == null) {
               phone
           }else {
               cursor.getString(cursor.getColumnIndex(projections[0])) ?: phone
           }
        }catch (e: Exception) {
            return phone
        }
    }

    fun getDuration(record: Record): String {

        val durationInMillis = (record.recordStopTime - record.recordStartTime)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(durationInMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(durationInMillis) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationInMillis))

        return String.format(Locale.getDefault(),
                "%02d%s%02d", minutes, ":", seconds)
    }
}