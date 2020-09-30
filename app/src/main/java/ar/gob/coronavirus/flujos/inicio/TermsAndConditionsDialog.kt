package ar.gob.coronavirus.flujos.inicio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ar.gob.coronavirus.R
import kotlinx.android.synthetic.main.inicio_terminos_fragment.*

class TermsAndConditionsDialog : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.inicio_terminos_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        terms_and_conditions.text = getText(R.string.terms_and_conditions)
        btn_accept.setOnClickListener { dismiss() }
        img_back.setOnClickListener { dismiss() }
    }
}