package ar.gob.coronavirus.utils

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import java.util.*

object QrUtils {

    @JvmStatic
    fun generateQrOfUrl(text: String, desiredWidth: Int, desiredHeight: Int): Bitmap {
        val hashTable = Hashtable<EncodeHintType, Any>().apply {
            put(EncodeHintType.CHARACTER_SET, Charsets.UTF_8.name())
        }
        val result = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, desiredWidth, desiredHeight, hashTable)
        val width: Int = result.width
        val height: Int = result.height
        val pixels = IntArray(width * height)
        // All are 0, or black, by default
        // All are 0, or black, by default
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = (if (result.get(x, y)) 0xFF000000 else 0xFFFFFFFF).toInt()
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }
}