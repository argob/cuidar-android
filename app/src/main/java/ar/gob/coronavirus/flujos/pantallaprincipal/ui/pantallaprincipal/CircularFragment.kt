package ar.gob.coronavirus.flujos.pantallaprincipal.ui.pantallaprincipal

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import ar.gob.coronavirus.R
import ar.gob.coronavirus.data.local.modelo.LocalCirculationPermit
import ar.gob.coronavirus.flujos.autodiagnostico.resultado.ResultadoActivity
import ar.gob.coronavirus.utils.Constantes
import ar.gob.coronavirus.utils.QrUtils.generateQrOfUrl
import kotlinx.android.synthetic.main.base_main_fragment.*
import kotlinx.android.synthetic.main.fragment_circular.*

/**
 * A simple [Fragment] subclass.
 */
class CircularFragment : BaseMainFragment(R.layout.fragment_circular) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpHeader(R.drawable.gradiente_verde, R.drawable.ic_circular_verde, R.string.h_description_circular, 30f)
        qr_mas_info.setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Constantes.URL_QR_MAS_INFO))) }
    }

    override fun onStart() {
        super.onStart()
        escucharCambiosDelUsuario()
    }

    private fun escucharCambiosDelUsuario() {
        viewModel.obtenerUltimoEstadoLiveData().observe(viewLifecycleOwner, Observer { (user, circulationPermits) ->
            try {
                viewModel.despacharEventoNavegacion()
                val currentState = user.currentState
                if (circulationPermits.size == 1) {
                    showSinglePermit(circulationPermits.first())
                } else {
                    showMultiplePermits(circulationPermits)
                }
                setUpUserInfo(user, getString(R.string.h_recommendation_circular))
                val fechaVencimiento = currentState.expirationDate
                setUpSymptomsSection(R.string.sintomas_autodiagnostico, getString(R.string.sintomas_resultado_sin_sintomas), R.color.covid_verde, fechaVencimiento, true, ResultadoActivity.OpcionesNavegacion.RESULTADO_VERDE)
            } catch (ignored: Exception) {
            }
        })
    }

    private fun showMultiplePermits(circulationPermits: List<LocalCirculationPermit>) {
        val adapter = ArrayAdapter<String>(requireContext(), R.layout.support_simple_spinner_dropdown_item)
        adapter.addAll(circulationPermits.map { it.reason })
        certificates_spinner.adapter = adapter
        certificates_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                loadPermit(circulationPermits[position])
            }
        }
    }

    private fun showSinglePermit(permit: LocalCirculationPermit) {
        certificates_spinner.isVisible = false
        loadPermit(permit)
    }

    private fun loadPermit(permit: LocalCirculationPermit) {
        qr_imagen.post {
            val nonNullQr = qr_imagen ?: return@post
            val permitQr = generateQrOfUrl(permit.url, nonNullQr.width, nonNullQr.height)
            nonNullQr.setImageBitmap(permitQr)
        }

        sube.isVisible = permit.sube.isNotEmpty()
        sube.text = getString(R.string.sube, "...${permit.sube.takeLast(8)}")
        plate.isVisible = permit.plate.isNotEmpty()
        plate.text = getString(R.string.plate, permit.plate)

        if (permit.activityType.isNotEmpty()) {
            qr_status_esencial.visibility = View.VISIBLE
            qr_status_esencial.text = permit.activityType
        } else {
            qr_status_esencial.visibility = View.GONE
        }
    }
}