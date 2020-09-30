package ar.gob.coronavirus.flujos.identificacion;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.koin.androidx.viewmodel.compat.SharedViewModelCompat;

import ar.gob.coronavirus.R;
import ar.gob.coronavirus.data.DniEntity;
import ar.gob.coronavirus.fcm.FcmIntentService;
import ar.gob.coronavirus.flujos.autodiagnostico.AutodiagnosticoActivity;
import ar.gob.coronavirus.flujos.inicio.TermsAndConditionsDialog;
import ar.gob.coronavirus.flujos.pantallaprincipal.PantallaPrincipalActivity;
import ar.gob.coronavirus.utils.Constantes;
import ar.gob.coronavirus.utils.InternetUtils;
import ar.gob.coronavirus.utils.KeyboardUtils;
import ar.gob.coronavirus.utils.dialogs.Dialogs;
import ar.gob.coronavirus.utils.dialogs.FullScreenDialog;
import ar.gob.coronavirus.utils.dialogs.IdentificationNumberTutorialDialog;
import ar.gob.coronavirus.utils.permisos.PermisosUtileria;

public class IdentificacionDniManualFragment extends Fragment {

    private static final int SOLICITAR_PERMISO_CAMARA = 100;

    private IdentificacionViewModel identificacionViewModel;
    private TextView botonSiguiente;
    private TextView botonEscanear;
    private EditText dniEt;
    private EditText numeroTramiteEt;
    private RadioButton masculinoRb;
    private RadioButton femeninoRb;
    private RadioGroup radioGroupSexo;
    private View mensajeError;
    private TextInputLayout dniIL;
    private TextInputLayout noTramiteIL;
    private Dialog loaderDialog;
    private TextView textViewErrorSexo;
    private TextView comoObtenerNoTramite;
    private CheckBox checkBox;
    private TextView txtTyC;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.identificacion_pregunta_dni_manual_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iniciarViews();
        identificacionViewModel = SharedViewModelCompat.getSharedViewModel(this, IdentificacionViewModel.class);
        iniciarObservers();
        iniciarEventos();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void iniciarViews() {
        botonSiguiente = getView().findViewById(R.id.btn_siguiente_dni_manual_identificacion_fragment);
        botonEscanear = getView().findViewById(R.id.btn_escanear_dni_identificacion_fragment);
        dniIL = getView().findViewById(R.id.ti_numero_dni_identificacion);
        noTramiteIL = getView().findViewById(R.id.ti_numero_tramite_identificacion);
        dniEt = dniIL.getEditText();
        numeroTramiteEt = noTramiteIL.getEditText();
        masculinoRb = getView().findViewById(R.id.rb_masculino_identificacion_fragment);
        femeninoRb = getView().findViewById(R.id.rb_femenino_identificacion_fragment);
        textViewErrorSexo = getView().findViewById(R.id.error_sexo_radio_group);
        radioGroupSexo = getView().findViewById(R.id.radioGroupSexo);
        mensajeError = getView().findViewById(R.id.tv_error_message);
        comoObtenerNoTramite = getView().findViewById(R.id.tv_como_obtengo_numero_tramite_identificacion_fragment);
        checkBox = getView().findViewById(R.id.checkBoxAceptarCondiciones);
        txtTyC = getView().findViewById(R.id.txtTerminosYCondiciones);

        setTyCTextoFormateado();

        dniEt.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                dniIL.setErrorEnabled(false);
            }
        });

        numeroTramiteEt.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                noTramiteIL.setErrorEnabled(false);
            }
        });

        radioGroupSexo.setOnCheckedChangeListener((group, checkedId) -> {
            if (textViewErrorSexo != null) {
                textViewErrorSexo.setVisibility(View.INVISIBLE);
            }
            KeyboardUtils.hideKeyboard(requireActivity());
        });

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if ((isChecked)) {
                habilitarBonotes();
            } else {
                deshabilidatBotones();
            }
            KeyboardUtils.hideKeyboard(requireActivity());
        });
        checkBox.setChecked(true);

        txtTyC.setOnClickListener(v -> {
            TermsAndConditionsDialog dialog = new TermsAndConditionsDialog();
            dialog.show(getParentFragmentManager(), "TyC");
        });

        loaderDialog = Dialogs.createLoadingDialog(getActivity());
    }

    private void setTyCTextoFormateado() {
        String texto = getString(R.string.acepto_terminos);
        Spanned textoConFormato = Html.fromHtml(texto);
        txtTyC.setText(textoConFormato);
    }

    private void deshabilidatBotones() {
        botonEscanear.setEnabled(false);
        botonSiguiente.setEnabled(false);
    }

    private void habilitarBonotes() {
        botonEscanear.setEnabled(true);
        botonSiguiente.setEnabled(true);
    }


    private void iniciarEventos() {
        botonSiguiente.setOnClickListener(v -> {
            if (InternetUtils.isConnected(getContext())) {
                mensajeError.setVisibility(View.GONE);
                String dni = dniEt.getText().toString();
                String noTramite = numeroTramiteEt.getText().toString();
                if (validarDatosEntrada(dni, noTramite)) {
                    identificacionViewModel.registerUser(
                            dni,
                            noTramite,
                            obtenerValorRadioGroupSexo()
                    );

                    if(loaderDialog != null){
                        loaderDialog.show();
                    }
                }
                dniIL.clearFocus();
                noTramiteIL.clearFocus();
                botonSiguiente.requestFocus();
            } else {
                crearDialogoInternet();
            }
        });

        botonEscanear.setOnClickListener(v -> {
            mensajeError.setVisibility(View.GONE);
            if (revisarPermisoCamara()) {
                IntentIntegrator intentEscaner = IntentIntegrator.forSupportFragment(IdentificacionDniManualFragment.this);
                intentEscaner.initiateScan();
            }
        });

        comoObtenerNoTramite.setOnClickListener(view -> {
            view.setClickable(false);
            new IdentificationNumberTutorialDialog().show(getChildFragmentManager(), null);
            new Handler().postDelayed(() -> view.setClickable(true), 500);
        });
    }

    private String obtenerValorRadioGroupSexo() {
        int radioButtonChecked = radioGroupSexo.getCheckedRadioButtonId();
        String sexo = "";

        switch (radioButtonChecked) {
            case R.id.rb_femenino_identificacion_fragment:
                sexo = Constantes.FEMENINO;
                break;
            case R.id.rb_masculino_identificacion_fragment:
                sexo = Constantes.MASCULINO;
                break;
        }

        return sexo;
    }

    private void iniciarObservers() {
        identificacionViewModel.getDniEntidadLiveData().observe(getViewLifecycleOwner(), dniEntity -> {
            if (dniEntity.hasBasicData()) {
                insertarDatosEnLaVista(dniEntity);
            } else {
                Dialogs.createMessageDialog(getContext(), R.string.mensaje_error_escanear_dni, R.string.aceptar, (dialog, which) -> dialog.dismiss());
            }
        });
        identificacionViewModel.getRegistrarUsuarioLiveData().observe(getViewLifecycleOwner(), eventoUnicoNavegacion -> {
            if (eventoUnicoNavegacion.getOrNull() != null) {
                switch (eventoUnicoNavegacion.get()) {
                    case IDENTIFICACION:
                        ((IdentificacionActivity) getActivity()).navegarAIdentificacionConfirmacionDatosFragment();
                        FcmIntentService.startActionFetchToken(getActivity());
                        break;
                    case AUTODIAGNOSTICO:
                        AutodiagnosticoActivity.iniciar(getContext(), false);
                        FcmIntentService.startActionFetchToken(getActivity());
                        getActivity().finish();
                        break;
                    case PRINCIPAL:
                        PantallaPrincipalActivity.iniciar(getContext(), false);
                        FcmIntentService.startActionFetchToken(getActivity());
                        getActivity().finish();
                        break;
                    case ERROR:
                        mensajeError.setVisibility(View.VISIBLE);
                        break;
                }
            }
            loaderDialog.dismiss();
        });

        identificacionViewModel.getLimpiarLogin().observe(requireActivity(), booleanEventoUnico -> {
            if (booleanEventoUnico.getOrNull() != null) {
                if (booleanEventoUnico.get()) {
                    limpiarVista();
                }
            }
        });
    }

    private void limpiarVista() {
        dniEt.setText("");
        numeroTramiteEt.setText("");
        radioGroupSexo.clearCheck();
    }

    private void insertarDatosEnLaVista(DniEntity dniEntity) {
        dniEt.setText(dniEntity.getId());
        numeroTramiteEt.setText(dniEntity.getProcedure());
        if (Constantes.MASCULINO.equals(dniEntity.getGender())) {
            masculinoRb.setChecked(true);
        } else {
            femeninoRb.setChecked(true);
        }
    }

    private Boolean revisarPermisoCamara() {
        return PermisosUtileria.revisarPermiso(IdentificacionDniManualFragment.this,
                SOLICITAR_PERMISO_CAMARA, Manifest.permission.CAMERA);
    }

    private Boolean validarDatosEntrada(String dni, String numeroTramite) {
        if ((validarDni(dni) == TipoMensajeError.SIN_ERROR)
                && (validarNumeroDeTramite(numeroTramite) == TipoMensajeError.SIN_ERROR)
                && !obtenerValorRadioGroupSexo().isEmpty()) {
            return true;
        } else {

            if (textViewErrorSexo != null) {
                if (obtenerValorRadioGroupSexo().isEmpty()) {
                    textViewErrorSexo.setVisibility(View.VISIBLE);
                } else {
                    textViewErrorSexo.setVisibility(View.INVISIBLE);
                }
            }

            if (validarDni(dni) == TipoMensajeError.VACIO) {
                dniIL.setError(getString(R.string.erro_dni));
            } else if (validarDni(dni) == TipoMensajeError.INVALIDO) {
                dniIL.setError(getString(R.string.erro_dni_invalido));
            } else {
                dniIL.setErrorEnabled(false);
            }

            if (validarNumeroDeTramite(numeroTramite) == TipoMensajeError.VACIO) {
                noTramiteIL.setError(getString(R.string.erro_numero_tramite));
            } else if (validarNumeroDeTramite(numeroTramite) == TipoMensajeError.INVALIDO) {
                noTramiteIL.setError(getString(R.string.erro_numero_tramite_invalido));
            } else {
                noTramiteIL.setErrorEnabled(false);
            }

            return false;
        }
    }

    private TipoMensajeError validarDni(String dni) {
        if (dni.isEmpty()) return TipoMensajeError.VACIO;
        else if (dni.length() < 7 || dni.length() > 9) return TipoMensajeError.INVALIDO;
        return TipoMensajeError.SIN_ERROR;
    }

    private TipoMensajeError validarNumeroDeTramite(String numeroTramite) {
        if (numeroTramite.isEmpty()) {
            return TipoMensajeError.VACIO;
        }
        return TipoMensajeError.SIN_ERROR;
    }

    private enum TipoMensajeError {
        VACIO, INVALIDO, SIN_ERROR
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && result.getContents() != null) {
            identificacionViewModel.procesarCodigoQr(result.getContents());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SOLICITAR_PERMISO_CAMARA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                IntentIntegrator intentEscaner = IntentIntegrator.forSupportFragment(IdentificacionDniManualFragment.this);
                intentEscaner.initiateScan();
            }
        }
    }

    private void crearDialogoInternet() {
        FullScreenDialog.newInstance(
                getString(R.string.hubo_error),
                getString(R.string.no_hay_internet),
                getString(R.string.cerrar).toUpperCase(),
                R.drawable.ic_error
        ).show(getParentFragmentManager(), "TAG");
    }

}
