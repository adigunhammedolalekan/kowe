package app.kowe.kowe.data.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import java.util.*

@Entity(tableName = "records_data")
data class Record(@PrimaryKey(autoGenerate = true)
                           var recordId: Long = 0,
                           var savedPath: String? = "",
                           var phoneNumber: String? = "",
                           var recordStartTime: Long = 0,
                           var callDuration: String? = "",
                           var synced: Boolean? = false,
                           var recordStopTime: Long = 0,
                           var remoteUrl: String? = "",
                           var readableTime: String? = "",
                           var account: Int? = 0) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readString(),
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
            parcel.readLong(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(recordId)
        parcel.writeString(savedPath)
        parcel.writeString(phoneNumber)
        parcel.writeLong(recordStartTime)
        parcel.writeString(callDuration)
        parcel.writeValue(synced)
        parcel.writeLong(recordStopTime)
        parcel.writeString(remoteUrl)
        parcel.writeString(readableTime)
        parcel.writeValue(account)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Record> {
        override fun createFromParcel(parcel: Parcel): Record {
            return Record(parcel)
        }

        override fun newArray(size: Int): Array<Record?> {
            return arrayOfNulls(size)
        }
    }
}