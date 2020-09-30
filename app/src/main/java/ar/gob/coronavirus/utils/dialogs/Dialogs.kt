package ar.gob.coronavirus.utils.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import ar.gob.coronavirus.R

object Dialogs {
    @JvmStatic
    fun createLoadingDialog(context: Context): Dialog {
        return Dialog(context, android.R.style.Theme_Light).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_loading)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }
    }

    @JvmStatic
    fun createMessageDialog(context: Context?, message: Int, positiveButton: Int, positiveListener: DialogInterface.OnClickListener): AlertDialog {
        return AlertDialog.Builder(context).run {
            setMessage(message)
            setPositiveButton(positiveButton, positiveListener)
            setCancelable(false)
            create()
        }
    }
}