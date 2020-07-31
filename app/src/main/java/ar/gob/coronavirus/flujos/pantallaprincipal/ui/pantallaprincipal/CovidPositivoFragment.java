package ar.gob.coronavirus.flujos.pantallaprincipal.ui.pantallaprincipal;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;

import androidx.lifecycle.LiveData;

import com.google.android.material.textview.MaterialTextView;

import org.jetbrains.annotations.NotNull;

import ar.gob.coronavirus.R;
import ar.gob.coronavirus.data.local.modelo.LocalState;
import ar.gob.coronavirus.data.local.modelo.LocalUser;
import ar.gob.coronavirus.flujos.autodiagnostico.resultado.ResultadoActivity;
import ar.gob.coronavirus.utils.Constantes;
import ar.gob.coronavirus.utils.InternetUtileria;
import ar.gob.coronavirus.utils.dialogs.PantallaCompletaDialog;

public class CovidPositivoFragment extends BaseMainFragment {

    private MaterialTextView pieInfoTelefonos;

    public CovidPositivoFragment() {
        super(R.layout.pantalla_principal_infectado_fragment);
    }


    @Override
    public void onViewCreated(@NotNull View view, @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpHeader(R.drawable.gradiente_rosa, R.drawable.ic_infectado_rosa, R.string.h_description_infectado_2, 22);

        pieInfoTelefonos = view.findViewById(R.id.pie_info_telefonos);
        setFormatoTextoInfoTelefonos();
        pieInfoTelefonos.setOnClickListener(v -> {
            if (InternetUtileria.hayConexionDeInternet(getContext())) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constantes.URL_INFO_TELEFONOS)));
            } else {
                crearDialogo();
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
                LocalState currentState = localUser.getCurrentState();
                setUpUserInfo(localUser, getString(R.string.h_recomendacion_infectado_2, currentState.getCoep().getCoep(),
                        currentState.getCoep().getContactInformation()));

                String fechaVencimiento = currentState.getExpirationDate();
                setUpSymptomsSection(R.string.sintomas_derivado_al_sistema_de_salud, getString(R.string.derivado_de_salud_covid_positivo), R.color.covid_fucsia, fechaVencimiento, false, ResultadoActivity.OpcionesNavegacion.RESULTADO_ROSA);
            } catch (Exception ignored) {
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        getViewModel().obtenerUltimoEstadoLiveData().removeObservers(requireActivity());
    }

    private void setFormatoTextoInfoTelefonos() {
        String texto = getString(R.string.informacion_footer_telefonos);
        Spanned textoConFormato = Html.fromHtml(texto);
        pieInfoTelefonos.setText(textoConFormato);
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
}
