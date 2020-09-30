package ar.gob.coronavirus.flujos.inicio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import ar.gob.coronavirus.BuildConfig
import ar.gob.coronavirus.databinding.InicioSplashFragmentBinding
import ar.gob.coronavirus.flujos.autodiagnostico.AutodiagnosticoActivity
import ar.gob.coronavirus.flujos.identificacion.IdentificacionActivity
import ar.gob.coronavirus.flujos.pantallaprincipal.PantallaPrincipalActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashFragment : Fragment() {
    private var binding: InicioSplashFragmentBinding? = null
    private val viewModel by viewModel<SplashViewModel>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = InicioSplashFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.navigationLiveData.observe(viewLifecycleOwner) { stringEvent ->
            if (stringEvent.getOrNull() != null) {
                when (stringEvent.get()) {
                    SplashDestinations.LOGIN -> {
                        IdentificacionActivity.start(context, false)
                        activity?.finish()
                    }
                    SplashDestinations.PRINCIPAL -> {
                        PantallaPrincipalActivity.iniciar(context, false)
                        activity?.finish()
                    }
                    SplashDestinations.DIAGNOSTICO -> {
                        AutodiagnosticoActivity.iniciar(context, false)
                        activity?.finish()
                    }
                    SplashDestinations.LOGIN_INVALID -> {
                        IdentificacionActivity.startAndShowInvalidLogin(requireContext())
                        activity?.finish()
                    }
                }
            }
        }
        binding!!.version.text = BuildConfig.VERSION_NAME
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}