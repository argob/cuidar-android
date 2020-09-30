package ar.gob.coronavirus.flujos.pantallaprincipal.ui.pantallaprincipal

import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import ar.gob.coronavirus.R
import ar.gob.coronavirus.data.local.modelo.LocalUser
import ar.gob.coronavirus.flujos.autodiagnostico.AutodiagnosticoActivity
import ar.gob.coronavirus.flujos.autodiagnostico.ProvincesEnum
import ar.gob.coronavirus.flujos.autodiagnostico.resultado.ResultadoActivity
import ar.gob.coronavirus.flujos.pantallaprincipal.PantallaPrincipalActivity
import ar.gob.coronavirus.flujos.pantallaprincipal.PantallaPrincipalViewModel
import ar.gob.coronavirus.utils.date.formatDate
import ar.gob.coronavirus.utils.many.APIConstants
import kotlinx.android.synthetic.main.base_main_fragment.*
import kotlinx.android.synthetic.main.base_main_fragment.view.*
import kotlinx.android.synthetic.main.current_state_view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

abstract class BaseMainFragment(private val contentLayout: Int) : Fragment(R.layout.base_main_fragment) {

    protected val viewModel by sharedViewModel<PantallaPrincipalViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LayoutInflater.from(requireContext()).inflate(contentLayout, view.fragment_content, true)
    }

    override fun onResume() {
        super.onResume()
        setUpToken()
    }

    protected fun setUpHeader(backgroundColor: Int, icon: Int, text: Int, textSize: Float = 30f) {
        header_colored_container.background = ContextCompat.getDrawable(requireContext(), backgroundColor)
        header_icon.setImageResource(icon)
        header_description.setText(text)
        header_description.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
    }

    protected fun setUpUserInfo(user: LocalUser, recommendation: String) {
        nombre_de_usuario.text = "${user.names} ${user.lastNames}"
        dni_de_usuario.text = getString(R.string.user_dni, user.dni)
        header_recomendation.text = recommendation
        pba_button.isVisible = ProvincesEnum.fromString(user.address?.province) == ProvincesEnum.BSAS
        if (pba_button.isVisible) {
            pba_button.setOnClickListener {
                (activity as? PantallaPrincipalActivity)?.openPbaActivity()
            }
        }
    }

    protected fun setUpSymptomsSection(header: Int, text: String, textColor: Int, expiry: String, autoDiagnosisButton: Boolean, result: ResultadoActivity.OpcionesNavegacion) {
        sintoma_autodiagnostico.setText(header)
        sintoma_resultado.text = text
        sintoma_resultado.setTextColor(ContextCompat.getColor(requireContext(), textColor))
        sintoma_vencimiento.isVisible = expiry.isNotEmpty()
        sintoma_vencimiento.text = getString(R.string.sintomas_vencimiento, expiry.formatDate())
        boton_nuevo_diagnostico.isVisible = autoDiagnosisButton
        boton_nuevo_diagnostico.setOnClickListener {
            AutodiagnosticoActivity.iniciarActividadParaResultado(activity, true)
        }

        sintoma_mas_informacion.setOnClickListener { v: View ->
            v.isClickable = false
            ResultadoActivity.iniciar(activity, result)
            Handler().postDelayed({ v.isClickable = true }, 500)
        }
    }

    private fun setUpToken() {
        val tokenInfo = APIConstants.getInfo()
        emoji_uno?.text = tokenInfo.component1()
        emoji_dos?.text = tokenInfo.component2()
        emoji_tres?.text = tokenInfo.component3()
    }
}

