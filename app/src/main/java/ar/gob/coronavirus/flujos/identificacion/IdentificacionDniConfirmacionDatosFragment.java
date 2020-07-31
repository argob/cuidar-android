package ar.gob.coronavirus.flujos.identificacion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ar.gob.coronavirus.R;
import ar.gob.coronavirus.utils.Constantes;
import ar.gob.coronavirus.utils.date.DateUtils;

public class IdentificacionDniConfirmacionDatosFragment extends Fragment {

    private IdentificacionViewModel identificacionViewModel;

    private TextView tvNombreCompleto;
    private TextView tvDni;
    private TextView tvFechaNacimiento;
    private TextView tvSexo;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.identificacion_pregunta_confirmacion_datos_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iniciarViews();
        identificacionViewModel = new ViewModelProvider(getActivity()).get(IdentificacionViewModel.class);
        identificacionViewModel.obtenerUsuario();
        identificacionViewModel.getUsuarioLiveData().observe(getViewLifecycleOwner(), usuario -> {
            String fechaNacimientoPresentacion = DateUtils.obtenerFechaParaPresentacion(usuario.getBirthDate());
            tvNombreCompleto.setText(String.format("%s, %s", usuario.getNames(), usuario.getLastNames()));
            tvDni.setText(String.format("%s", usuario.getDni()));
            tvFechaNacimiento.setText(String.format("%s", fechaNacimientoPresentacion));
            if (usuario.getGender().equals(Constantes.FEMENINO)) {
                tvSexo.setText(String.format("%s", getString(R.string.femenino)));
            } else {
                tvSexo.setText(String.format("%s", getString(R.string.masculio)));
            }
        });

        TextView botonSiguiente = getActivity().findViewById(R.id.btn_siguiente_confirmacion_datos_identificacion_fragment);
        botonSiguiente.setOnClickListener(v -> ((IdentificacionActivity) getActivity()).navegarAIdentificacionTelefonoFragment());
    }

    private void iniciarViews() {
        tvNombreCompleto = getView().findViewById(R.id.tv_nombre_completo_confirmacion_identificacion_fragment);
        tvDni = getView().findViewById(R.id.tv_dni_response_identificacion_fragment);
        tvFechaNacimiento = getView().findViewById(R.id.tv_fecha_nacimiento_response_identificacion_fragment);
        tvSexo = getView().findViewById(R.id.tv_sexo_response_identificacion_fragment);
    }
}
