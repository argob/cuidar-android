@file:JvmName("StringUtils")
package ar.gob.coronavirus.utils.strings

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import java.text.Normalizer
import java.util.*

fun CharSequence.normalizeToUnicode(): String {
    val result = Normalizer.normalize(this, Normalizer.Form.NFD)
    return result.replace("[^\\p{ASCII}]".toRegex(), "").toLowerCase(Locale.getDefault())
}

fun String.applyFont(context: Context, @FontRes font: Int): Spanned {
    val typeface = ResourcesCompat.getFont(context, font)
    val spannedString = SpannableString(this)
    spannedString.setSpan(CustomTypefaceSpan("", typeface), 0, this.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
    return spannedString
}