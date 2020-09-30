package ar.gob.coronavirus.utils.dialogs

import android.app.Dialog
import android.view.Window
import androidx.fragment.app.DialogFragment
import ar.gob.coronavirus.R
import kotlinx.android.synthetic.main.tramite_dialog_layout.*

class IdentificationNumberTutorialDialog : DialogFragment() {

    override fun getDialog(): Dialog? {
        return Dialog(requireContext()).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.tramite_dialog_layout)
            setCancelable(true)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            boton_cerrar.setOnClickListener {
                dismissAllowingStateLoss()
            }
        }
    }
}