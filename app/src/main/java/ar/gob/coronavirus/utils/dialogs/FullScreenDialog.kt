package ar.gob.coronavirus.utils.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import ar.gob.coronavirus.R
import ar.gob.coronavirus.utils.strings.applyFont
import kotlinx.android.synthetic.main.dialogo_pantalla_completa.*

class FullScreenDialog : DialogFragment() {
    private var accion: ActionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialogo_pantalla_completa, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewValues()
        setNotCancelable()
    }

    private fun setViewValues() {
        val title = arguments?.getString(KEY_TITLE) ?: ""
        val message = arguments?.getString(KEY_MESSAGE) ?: ""
        val action = arguments?.getString(KEY_ACTION)
        val icon = arguments?.getInt(KEY_ICON) ?: 0

        txt_titulo_pantalla_completa_dialog.text = title.applyFont(requireContext(), R.font.encode_bold)
        txt_mensaje_pantalla_completa_dialog.text = message.applyFont(requireContext(), R.font.roboto_medium)
        if (icon != 0) {
            pantalla_completa_logo.setImageResource(icon)
        } else {
            pantalla_completa_logo.isVisible = false
        }
        btn_action_pantalla_completa_dialog.text = action
        btn_action_pantalla_completa_dialog.setOnClickListener {
            dismiss()
            accion?.onClick()
        }
    }

    private fun setNotCancelable() {
        isCancelable = false
        dialog?.setCanceledOnTouchOutside(false)
    }

    fun setActionListener(actionListener: ActionListener): FullScreenDialog {
        this.accion = actionListener
        return this
    }

    interface ActionListener {
        fun onClick()
    }

    companion object {
        private const val KEY_TITLE = "key_title"
        private const val KEY_MESSAGE = "key_message"
        private const val KEY_ACTION = "key_action"
        private const val KEY_ICON = "key_icon"

        @JvmStatic
        fun newInstance(title: String, message: String, action: String, icon: Int) =
                FullScreenDialog().apply {
                    arguments = bundleOf(KEY_TITLE to title, KEY_MESSAGE to message, KEY_ACTION to action, KEY_ICON to icon)
                }
    }
}