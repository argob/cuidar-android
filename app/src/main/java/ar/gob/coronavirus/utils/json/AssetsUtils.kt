package ar.gob.coronavirus.utils.json

import ar.gob.coronavirus.CovidApplication
import com.google.gson.Gson
import java.io.InputStreamReader

object AssetsUtils {
    @JvmStatic
    fun <T : Any> loadFromAsset(fileName: String, clazz: Class<T>): T {
        return CovidApplication.instance.assets.open(fileName).use {
            Gson().fromJson(InputStreamReader(it), clazz)
        }
    }
}