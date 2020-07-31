package ar.gob.coronavirus.utils.strings;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

public class PintarSeccionBold {
    public static SpannableStringBuilder pintarBold(String texto, int desde, int hasta, Typeface typeface) {
        SpannableStringBuilder str = new SpannableStringBuilder(texto);
        if (!texto.isEmpty() && (texto.length() > desde)) {
            str.setSpan(new CustomTypefaceSpan("", typeface), desde, Math.min(hasta, texto.length()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return str;
    }
}
