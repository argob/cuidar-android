@file:JvmName("KeyboardUtils")

package ar.gob.coronavirus.utils

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService

fun hideKeyboard(activity: Activity) {
    val imm = activity.getSystemService<InputMethodManager>()
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = activity.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(activity)
    }
    imm?.hideSoftInputFromWindow(view.windowToken, 0)
}