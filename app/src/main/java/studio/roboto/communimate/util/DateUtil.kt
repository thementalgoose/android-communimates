package studio.roboto.communimate.util

import java.text.SimpleDateFormat
import java.util.*

class DateUtil {
    companion object {
        private val mSDF = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        fun from(format: String): Date {
            return mSDF.parse(format)
        }
    }
}