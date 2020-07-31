package ar.gob.coronavirus.flujos.pantallaprincipal.ui.pantallaprincipal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;

import org.jetbrains.annotations.NotNull;

import ar.gob.coronavirus.R;
import ar.gob.coronavirus.data.local.modelo.LocalCirculationPermit;
import ar.gob.coronavirus.data.local.modelo.LocalState;
import ar.gob.coronavirus.data.local.modelo.LocalUser;
import ar.gob.coronavirus.flujos.autodiagnostico.resultado.ResultadoActivity;
import ar.gob.coronavirus.utils.Constantes;
import ar.gob.coronavirus.utils.QrUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class CircularFragment extends BaseMainFragment {
    private ImageView imagenQr;
    private TextView tvEsEscencial;

    public CircularFragment() {
        super(R.layout.fragment_circular);
    }

    @Override
    public void onViewCreated(@NotNull View view, @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpHeader(R.drawable.gradiente_verde, R.drawable.ic_circular_verde, R.string.h_description_circular, 30);

        imagenQr = view.findViewById(R.id.qr_imagen);

        View botonActualizarCertificado = view.findViewById(R.id.qr_mas_info);
        botonActualizarCertificado.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constantes.URL_QR_MAS_INFO))));

        tvEsEscencial = getView().findViewById(R.id.qr_status_esencial);
    }

    @Override
    public void onStart() {
        super.onStart();
        escucharCambiosDelUsuario();
    }

    private void escucharCambiosDelUsuario() {
        getViewModel().obtenerUltimoEstadoLiveData().observe(requireActivity(), localUser -> {
            try {
                getViewModel().despacharEventoNavegacion();
                LocalState currentState = localUser.getCurrentState();
                LocalCirculationPermit circulationPermit = currentState.getCirculationPermit();
                if (getActivity() != null && !android.text.TextUtils.isEmpty(circulationPermit.getQr())) {
                    Bitmap imagenPermiso = QrUtils.generarQr(circulationPermit.getQr());
                    imagenQr.setImageBitmap(imagenPermiso);
                    setUpUserInfo(localUser, getString(R.string.h_recommendation_circular));

                    if (circulationPermit != null && !android.text.TextUtils.isEmpty(circulationPermit.getActivityType())) {
                        tvEsEscencial.setVisibility(View.VISIBLE);
                        tvEsEscencial.setText(circulationPermit.getActivityType());
                    } else {
                        tvEsEscencial.setVisibility(View.GONE);
                    }

                    String fechaVencimiento = currentState.getExpirationDate();
                    setUpSymptomsSection(R.string.sintomas_autodiagnostico, getString(R.string.sintomas_resultado_sin_sintomas), R.color.covid_verde, fechaVencimiento, true, ResultadoActivity.OpcionesNavegacion.RESULTADO_VERDE);
                }
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