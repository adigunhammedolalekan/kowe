package app.kowe.kowe

import java.text.SimpleDateFormat
import java.util.*

object Util {

    val SDF = SimpleDateFormat("MMM dd 2019, HH:mm", Locale.getDefault())

    fun formatCurrentTime() = SDF.format(Calendar.getInstance().time)

    fun format(date: Date) = SDF.format(date.time)

    fun format(t: Long) = SDF.format(t)
}