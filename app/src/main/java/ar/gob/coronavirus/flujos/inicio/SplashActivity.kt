package ar.gob.coronavirus.flujos.inicio

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import ar.gob.coronavirus.R
import ar.gob.coronavirus.flujos.BaseActivity
import ar.gob.coronavirus.utils.many.APIConstants
import com.newrelic.agent.android.NewRelic
import kotlinx.android.synthetic.main.activity_inicio.*

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        if (APIConstants.APPLICATION_TOKEN.isNotEmpty()) {
            NewRelic.withApplicationToken(APIConstants.APPLICATION_TOKEN).start(this.application)
        }

        if (savedInstanceState == null) {
            app_bar_inicio.isVisible = false
            supportFragmentManager.commit {
                add(R.id.fragment_container, SplashFragment())
            }
        }
    }

    override fun mostrarDialogoSinInternet() {
        // Don't show it in the splash
    }
}