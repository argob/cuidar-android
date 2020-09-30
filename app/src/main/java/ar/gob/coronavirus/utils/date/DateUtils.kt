@file:JvmName("DateUtils")

package ar.gob.coronavirus.utils.date

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

private const val INPUT_FORMAT = "yyyy-MM-dd"
private const val OUTPUT_FORMAT = "dd/MM/yyyy"

fun String.formatDate(): String {
    return try {
        val date = SimpleDateFormat(INPUT_FORMAT, Locale.getDefault()).parse(this) ?: return ""
        SimpleDateFormat(OUTPUT_FORMAT, Locale.getDefault()).format(date)
    } catch (e: ParseException) {
        ""
    }
}
