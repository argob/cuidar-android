package ar.gob.coronavirus.utils.strings;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;

import androidx.annotation.FontRes;
import androidx.core.content.res.ResourcesCompat;

public final class StringUtils {


    private StringUtils() {
    }

    public static Spanned applyFont(Context context, String text, @FontRes int font) {
        Typeface typeface = ResourcesCompat.getFont(context, font);
        SpannableString spannedString = new SpannableString(text);
        spannedString.setSpan(new CustomTypefaceSpan("", typeface), 0, text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return spannedString;
    }
}
