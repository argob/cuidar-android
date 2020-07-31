package ar.gob.coronavirus.utils.extensions

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import ar.gob.coronavirus.R

fun Activity.startWebView(url: String) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
    } catch (exception: ActivityNotFoundException) {
        Toast.makeText(this, getString(R.string.should_install_default_browser_warning), Toast.LENGTH_LONG).show()
    }
}

fun Fragment.startWebView(url: String) = requireActivity().startWebView(url)