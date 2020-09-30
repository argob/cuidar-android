package ar.gob.coronavirus.flujos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ar.gob.coronavirus.R
import kotlinx.android.synthetic.main.activity_error_generico.*

class GenericErrorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error_generico)

        btn_action_pantalla_completa_dialog.setOnClickListener { finishAffinity() }
    }
}