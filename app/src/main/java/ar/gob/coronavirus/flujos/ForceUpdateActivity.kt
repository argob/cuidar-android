package ar.gob.coronavirus.flujos

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ar.gob.coronavirus.R
import ar.gob.coronavirus.utils.Constantes
import kotlinx.android.synthetic.main.dialogo_pantalla_completa.*

class ForceUpdateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialogo_pantalla_completa)

        txt_titulo_pantalla_completa_dialog.setText(R.string.dialog_version_title)
        txt_mensaje_pantalla_completa_dialog.setText(R.string.dialog_version_message)
        pantalla_completa_logo.setImageResource(R.drawable.ic_error)

        btn_action_pantalla_completa_dialog.setText(R.string.actualizar)
        btn_action_pantalla_completa_dialog.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Constantes.PREFIJO_PLAYSTORE + packageName)))
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Constantes.URL_PREFIJO_PLAYSTORE + packageName)))
            }
            finishAffinity()
        }
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, ForceUpdateActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }
}