package ar.gob.coronavirus.flujos.identificacion;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.koin.androidx.viewmodel.compat.SharedViewModelCompat;

import ar.gob.coronavirus.R;
import ar.gob.coronavirus.data.local.modelo.LocalUser;
import ar.gob.coronavirus.utils.PhoneUtils;

public class IdentificacionTelefonoFragment extends Fragment {
    public static final String PREFIJO_TELEFONO = "+54";
    private IdentificacionViewModel identificacionViewModel;
    private TextInputLayout telefonoTil;
    private TextInputEditText telefonoTie;
    private TextView botonSiguiente;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.identificacion_pregunta_telefono_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iniciarViews();
        identificacionViewModel = SharedViewModelCompat.getSharedViewModel(this, IdentificacionViewModel.class);
        iniciarListeners();
        prepopularCampos();

    }

    private void prepopularCampos() {
        LocalUser localUser = ((IdentificacionActivity) getActivity()).localUser;
        if (localUser != null && localUser.getPhone() != null) {
            telefonoTie = getView().findViewById(R.id.tie_telefono_identificacion_fragment);
            telefonoTie.setText(localUser.getPhone().replace(PREFIJO_TELEFONO, ""));
        }
    }

    private void iniciarListeners() {
        botonSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String telefonoString = telefonoTie.getText().toString();
                if (validarDatos(telefonoString)) {
                    identificacionViewModel.nroTelefono = PREFIJO_TELEFONO + telefonoString;
                    ((IdentificacionActivity) getActivity()).navegarAIdentificacionDireccionCompletaFragment();
                }
            }
        });
    }

    private boolean validarDatos(String telefono) {

        if (PhoneUtils.isValidPhone(telefono)) {
            telefonoTil.setError(null);
            botonSiguiente.setEnabled(true);
            return true;
        } else {
            telefonoTil.setError(getString(R.string.invalid_phone_error));
            botonSiguiente.setEnabled(false);
            return false;
        }
    }

    private void iniciarViews() {
        telefonoTil = getView().findViewById(R.id.til_telefono_identificacion_fragment);
        telefonoTie = getView().findViewById(R.id.tie_telefono_identificacion_fragment);
        botonSiguiente = getView().findViewById(R.id.btn_siguiente_telefono_fragment);
        telefonoTie.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String temp = telefonoTie.getText().toString();
                validarDatos(temp);
            }
        });
    }
}
