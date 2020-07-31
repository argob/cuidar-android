package ar.gob.coronavirus.flujos.pba

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import ar.gob.coronavirus.R
import ar.gob.coronavirus.flujos.BaseActivity
import ar.gob.coronavirus.utils.Constantes
import ar.gob.coronavirus.utils.extensions.startWebView
import kotlinx.android.synthetic.main.activity_pba.*

class PbaActivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pba)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        portal.setOnClickListener(this)
        mental_health_assistant.setOnClickListener(this)
        sip.setOnClickListener(this)
        covid_assistant.setOnClickListener(this)
        subtitle.setText(getString(R.string.pba_header_text), TextView.BufferType.SPANNABLE)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.portal -> startWebView(Constantes.URL_PBA_PORTAL_COVID)
            R.id.mental_health_assistant -> startWebView(Constantes.URL_MENTAL_HEALTH_ASSISTANT)
            R.id.covid_assistant -> startWebView(Constantes.URL_PBA_ASISTENCIA)
            R.id.sip -> startWebView(Constantes.URL_PBA_SIP)
        }
    }

    companion object {
        @JvmStatic
        fun start(context: Context) {
            context.startActivity(Intent(context, PbaActivity::class.java))
        }
    }
}