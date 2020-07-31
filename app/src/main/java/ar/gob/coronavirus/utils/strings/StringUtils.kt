package ar.gob.coronavirus.utils.strings

import java.text.Normalizer
import java.util.*

fun CharSequence.normalizeToUnicode(): String {
    val result = Normalizer.normalize(this, Normalizer.Form.NFD)
    return result.replace("[^\\p{ASCII}]".toRegex(), "").toLowerCase(Locale.getDefault())
}