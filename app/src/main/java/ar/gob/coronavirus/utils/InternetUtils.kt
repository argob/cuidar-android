@file:JvmName("InternetUtils")

package ar.gob.coronavirus.utils

import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.getSystemService

fun Context?.isConnected(): Boolean {
    val cm = this?.getSystemService<ConnectivityManager>()
    val activeNetwork = cm?.activeNetworkInfo
    return activeNetwork?.isConnectedOrConnecting ?: false
}