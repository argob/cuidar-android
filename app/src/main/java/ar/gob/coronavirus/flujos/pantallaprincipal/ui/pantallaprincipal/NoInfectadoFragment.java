package ar.gob.coronavirus.flujos.pantallaprincipal.ui.pantallaprincipal;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import org.jetbrains.annotations.NotNull;

import ar.gob.coronavirus.R;
import ar.gob.coronavirus.data.UserStatus;
import ar.gob.coronavirus.data.local.modelo.LocalUser;
import ar.gob.coronavirus.flujos.autodiagnostico.resultado.ResultadoActivity;
import ar.gob.coronavirus.utils.InternetUtileria;
import ar.gob.coronavirus.utils.dialogs.PantallaCompletaDialog;
import ar.gob.coronavirus.utils.observables.EventoUnico;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoInfectadoFragment extends BaseMainFragment {

    public NoInfectadoFragment() {
        super(R.layout.fragment_no_infectado);
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NotNull View view, @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpHeader(R.drawable.gradiente_azul, R.drawable.ic_no_contagioso_azul, R.string.h_description_quedate_en_casa, 30);

        TextView habilitarCirculacion = view.findViewById(R.id.qr_boton_agregar_certificado);
        habilitarCirculacion.setOnClickListener(v -> {
            if (InternetUtileria.hayConexionDeInternet(getContext())) {
                getViewModel().habilitarCirculacion();
            } else {
                crearDialogo();
            }
        });
    }

    private void crearDialogo() {
        final PantallaCompletaDialog dialog = PantallaCompletaDialog.newInstance(
                getString(R.string.hubo_error),
                getString(R.string.no_hay_internet),
                getString(R.string.cerrar).toUpperCase(),
                R.drawable.ic_error
        );

        dialog.setAccionBoton(new PantallaCompletaDialog.AccionBotonDialogoPantallaCompleta() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show(getParentFragmentManager(), "TAG");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getViewModel().obtenerLevantarWebLiveData().observe(getViewLifecycleOwner(), new Observer<EventoUnico<Intent>>() {
            @Override
            public void onChanged(EventoUnico<Intent> intentEventoUnico) {
                if (intentEventoUnico.obtenerContenidoSiNoFueLanzado() != null) {
                    try {
                        startActivity(intentEventoUnico.obtenerConenido());
                    } catch (ActivityNotFoundException exception) {
                        Toast.makeText(requireActivity(), getString(R.string.should_install_default_browser_warning), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        escucharCambiosDelUsuario();
    }

    private void escucharCambiosDelUsuario() {
        LiveData<LocalUser> localUserData = getViewModel().obtenerUltimoEstadoLiveData();
        localUserData.observe(requireActivity(), localUser -> {
            try {
                getViewModel().despacharEventoNavegacion();
                setUpUserInfo(localUser, getString(R.string.h_recommendation_no_contagioso));
                String fechaVencimiento = localUser.getCurrentState().getExpirationDate();
                boolean isNotContagious = localUser.getCurrentState().getUserStatus() == UserStatus.NOT_CONTAGIOUS;
                setUpSymptomsSection(R.string.auto_diagnostico, getString(R.string.sintomas_resultado_sin_sintomas), R.color.covid_azul, fechaVencimiento, !isNotContagious, ResultadoActivity.OpcionesNavegacion.RESULTADO_VERDE);
            } catch (Exception ignored) {
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        getViewModel().obtenerUltimoEstadoLiveData().removeObservers(requireActivity());
    }
}
