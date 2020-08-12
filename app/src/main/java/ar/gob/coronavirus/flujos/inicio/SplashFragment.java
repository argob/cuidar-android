package ar.gob.coronavirus.flujos.inicio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import org.koin.androidx.viewmodel.compat.ViewModelCompat;

import ar.gob.coronavirus.BuildConfig;
import ar.gob.coronavirus.databinding.InicioSplashFragmentBinding;
import ar.gob.coronavirus.flujos.autodiagnostico.AutodiagnosticoActivity;
import ar.gob.coronavirus.flujos.identificacion.IdentificacionActivity;
import ar.gob.coronavirus.flujos.pantallaprincipal.PantallaPrincipalActivity;
import ar.gob.coronavirus.utils.observables.EventoUnico;
import kotlin.Lazy;

public class SplashFragment extends Fragment {
	private InicioSplashFragmentBinding binding;

	private Lazy<SplashViewModel> viewModel = ViewModelCompat.viewModel(this, SplashViewModel.class);

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = InicioSplashFragmentBinding.inflate(inflater, container, false);
		return binding.getRoot();
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		viewModel.getValue().getNavigationLiveData().observe(getViewLifecycleOwner(), new Observer<EventoUnico<NavegacionFragments>>() {
			@Override
			public void onChanged(EventoUnico<NavegacionFragments> stringEventoUnico) {
				if (stringEventoUnico.obtenerContenidoSiNoFueLanzado() != null) {
					switch (stringEventoUnico.obtenerConenido()) {
						case LOGIN:
							((InicioActivity) requireActivity()).navegarALogin();
							break;
						case PRINCIPAL:
							PantallaPrincipalActivity.iniciar(getContext(), false);
							getActivity().finish();
							break;
						case DIAGNOSTICO:
							AutodiagnosticoActivity.iniciar(getContext(), false);
							getActivity().finish();
							break;
						case LOGIN_INVALID:
							IdentificacionActivity.startAndPrintDialogOtroDispositivo(requireContext());
							getActivity().finish();
							break;
					}
				}
			}
		});

		String version = (BuildConfig.BUILD_TYPE.equals("release") ? "" : BuildConfig.BUILD_TYPE) + " " + BuildConfig.VERSION_NAME;
		binding.version.setText(version);

	}
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
