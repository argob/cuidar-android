package ar.gob.coronavirus.flujos.inicio

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import ar.gob.coronavirus.R
import ar.gob.coronavirus.flujos.BaseActivity
import ar.gob.coronavirus.flujos.identificacion.IdentificacionActivity
import ar.gob.coronavirus.utils.many.ApiConstants
import com.newrelic.agent.android.NewRelic
import kotlinx.android.synthetic.main.activity_inicio.*

class InicioActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        if (ApiConstants.APPLICATION_TOKEN.isNotEmpty()) {
            NewRelic.withApplicationToken(
              ApiConstants.APPLICATION_TOKEN).start(this.application)
        }

        if (savedInstanceState == null) {
            app_bar_inicio.isVisible = false
            supportFragmentManager.commit {
                add(R.id.fragment_container, InicioSplashFragment())
            }
        }
    }

    fun navegarALogin() {
        IdentificacionActivity.iniciar(this, false)
        finish()
    }
}