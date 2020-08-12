package ar.gob.coronavirus.flujos.identificacion;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.koin.androidx.viewmodel.compat.SharedViewModelCompat;

import ar.gob.coronavirus.R;
import ar.gob.coronavirus.data.Localidad;
import ar.gob.coronavirus.data.Provincia;
import ar.gob.coronavirus.data.local.modelo.LocalAddress;
import ar.gob.coronavirus.data.local.modelo.LocalUser;
import ar.gob.coronavirus.flujos.identificacion.adapter.AutocompleteAdapter;
import ar.gob.coronavirus.utils.InternetUtileria;
import ar.gob.coronavirus.utils.TecladoUtils;
import ar.gob.coronavirus.utils.dialogs.LoadingDialog;
import ar.gob.coronavirus.utils.dialogs.PantallaCompletaDialog;

public class IdentificacionDireccionCompletaFragment extends Fragment {

    private static final int MAX_STREET_LENGTH = 70;
    private static final int MAX_STREET_NUMBER_LENGTH = 8;
    private static final int ZIP_CODE_LENGHT = 4;

    private IdentificacionViewModel identificacionViewModel;
    private TextView botonSiguiente;

    private AutocompleteAdapter<Object> provinciaAdapter;
    private AutocompleteAdapter<Object> localidadAdapter;

    private AutoCompleteTextView provinceSelector;
    private AutoCompleteTextView localitySelector;
    private TextInputEditText streetField;
    private TextInputEditText streetNumberField;
    private TextInputEditText zipCodeField;
    private TextInputEditText doorField;
    private TextInputEditText floorField;
    private TextInputEditText othersField;

    private TextInputLayout provinceTIL;
    private TextInputLayout localityTIL;
    private TextInputLayout streetTIL;
    private TextInputLayout streetNumberTIL;
    private TextInputLayout zipCodeTIL;

    private Dialog loaderDialog;
    private LocalUser localUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.identificacion_pregunta_direccion_completa_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            identificacionViewModel = SharedViewModelCompat.getSharedViewModel(this, IdentificacionViewModel.class);
            localUser = ((IdentificacionActivity) getActivity()).localUser;

            setupViews();
            setupObservers();
            setupListeners();
            fillSpinnerProvinces();
            prepopulateFields();
        }
    }

    private void setupViews() {
        provinceSelector = getView().findViewById(R.id.dropdown_provincia_identificacion_fragment);
        provinceTIL = getView().findViewById(R.id.ti_provincia_selector);
        localitySelector = getView().findViewById(R.id.dropdown_localidades_identificacion_fragment);
        localityTIL = getView().findViewById(R.id.ti_localidad_selector);
        streetField = getView().findViewById(R.id.tie_calle_identificacion_fragment);
        streetTIL = getView().findViewById(R.id.til_calle_identificacion_fragment);
        streetNumberField = getView().findViewById(R.id.tie_numero_casa_identificacion_fragment);
        streetNumberTIL = getView().findViewById(R.id.til_numero);
        zipCodeField = getView().findViewById(R.id.tie_codigo_postal_identificacion_fragment);
        zipCodeTIL = getView().findViewById(R.id.til_codigo_postal);
        doorField = getView().findViewById(R.id.tie_puerta_identificacion_fragment);
        floorField = getView().findViewById(R.id.tie_piso_identificacion_fragment);
        othersField = getView().findViewById(R.id.tie_otros_identificacion_fragment);
        botonSiguiente = getView().findViewById(R.id.btn_siguiente_direccion_completa_identificacion_fragment);

        loaderDialog = LoadingDialog.createLoadingDialog(getActivity(), getActivity().getLayoutInflater());
    }

    private void setupObservers() {
        identificacionViewModel.getProvinciasLiveData().observe(getViewLifecycleOwner(), provincias -> {
            provinciaAdapter = new AutocompleteAdapter<>(
                    getContext(),
                    R.layout.lista_item_dropdown,
                    provincias.getProvincias()
            );

            provinceSelector.setAdapter(provinciaAdapter);
            setupAutoCompleteTextView(provinceSelector, provinceTIL, getString(R.string.invalid_province_msg_error), provinciaAdapter);

            provinceSelector.setOnItemClickListener((parent, view, position, id) -> {
                onProvinceSelected();
            });

            provinceSelector.setOnEditorActionListener((v, actionId, event) -> {
                onProvinceSelected();
                return false;
            });

            if (localUser != null) {
                if (localUser.getAddress() != null) {
                    Provincia provincia = getProvincia(localUser.getAddress().getProvince());
                    if (provincia != null) {
                        provinceSelector.setText(localUser.getAddress().getProvince());
                        identificacionViewModel.provinciaSeleccionado = provincia;
                        identificacionViewModel.filtrarLocalidades(identificacionViewModel.provinciaSeleccionado.getId());
                    }
                }
            }
        });

        identificacionViewModel.getLocalidadesParaSpinner().observe(getViewLifecycleOwner(), localidades -> {
            localidadAdapter = new AutocompleteAdapter<>(
                    requireContext(),
                    R.layout.lista_item_dropdown,
                    localidades
            );

            localitySelector.setAdapter(localidadAdapter);
            setupAutoCompleteTextView(localitySelector, localityTIL, getString(R.string.invalid_locality_msg_error), localidadAdapter);

            localitySelector.setOnItemClickListener((parent, view, position, id) -> {
                onLocalitySelected();
                streetField.requestFocus();
            });

            localitySelector.setOnEditorActionListener((v, actionId, event) -> {
                onLocalitySelected();
                return false;
            });

            if (!localidades.isEmpty()) {
                LocalAddress userAddress = localUser != null ? localUser.getAddress() : null;

                if (userAddress != null) {
                    Localidad locality = getLocalidad(userAddress.getLocality(), userAddress.getApartment());
                    if (locality != null) {
                        localitySelector.setText(locality.toString());
                        identificacionViewModel.localidadSeleccionada(locality);
                    }
                }
            }
        });

        identificacionViewModel.getActualizarUsuarioLiveData().observe(requireActivity(), booleanEventoUnico -> loaderDialog.dismiss());

        streetField.addTextChangedListener(createOnTextChangedWatcher(streetTIL));
        streetNumberField.addTextChangedListener(createOnTextChangedWatcher(streetNumberTIL));
        zipCodeField.addTextChangedListener(createOnTextChangedWatcher(zipCodeTIL));
    }

    private void setupAutoCompleteTextView(
            AutoCompleteTextView autoCompleteTextView,
            TextInputLayout textInputLayout,
            String errorMsg,
            AutocompleteAdapter<Object> adapter) {

        autoCompleteTextView.setSelectAllOnFocus(true);

        autoCompleteTextView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                autoCompleteTextView.showDropDown();
                autoCompleteTextView.setListSelection(adapter.getPositionSelected());
                adapter.setAutoCompleteHasFocus(true);
            } else {
                adapter.setAutoCompleteHasFocus(false);
                adapter.restartList();
                adapter.setPositionSelected(adapter.getPositionFromValue(autoCompleteTextView.getText()));

                if (adapter.getPositionSelected() < 0) {
                    if (autoCompleteTextView.getText().toString().isEmpty()) {
                        textInputLayout.setError(null);
                    } else {
                        textInputLayout.setError(errorMsg);
                    }
                } else {
                    textInputLayout.setError(null);
                    autoCompleteTextView.setText(adapter.getValues().get(adapter.getPositionSelected()).toString());
                }
            }
        });
    }

    private void filterLocalities() {
        if (identificacionViewModel.provinciaSeleccionado != null) {
            identificacionViewModel.filtrarLocalidades(identificacionViewModel.provinciaSeleccionado.getId());
        } else {
            identificacionViewModel.filtrarLocalidades(null);
        }
    }

    private void onLocalitySelected() {
        localitySelector.clearFocus();
        identificacionViewModel.localidadSeleccionada((Localidad) localidadAdapter.getValueSelected());
    }

    private void onProvinceSelected() {
        provinceSelector.clearFocus();
        identificacionViewModel.provinciaSeleccionado = (Provincia) provinciaAdapter.getValueSelected();
        localitySelector.setText(null);
        filterLocalities();
    }

    private TextWatcher createOnTextChangedWatcher(TextInputLayout textInputLayout) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }

    private void setupListeners() {
        botonSiguiente.setOnClickListener(v -> {

            if (getActivity() != null) {
                TecladoUtils.esconderTeclado(getActivity());
            }

            if (validateForm()) {
                identificacionViewModel.crearDomicilioRemoto(
                        provinceSelector.getText().toString(),
                        streetField.getText().toString(),
                        streetNumberField.getText().toString(),
                        floorField.getText().toString(),
                        doorField.getText().toString(),
                        zipCodeField.getText().toString(),
                        othersField.getText().toString()
                );

                if (InternetUtileria.hayConexionDeInternet(getContext())) {
                    identificacionViewModel.actualizarUsuario();
                    loaderDialog.show();
                } else {
                    crearDialogoInternet();
                }

            } else {
                Toast.makeText(getActivity(), R.string.check_data_msg_warning, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillSpinnerProvinces() {
        if (identificacionViewModel.provinciaSeleccionado != null) {
            provinceSelector.setText(identificacionViewModel.provinciaSeleccionado.getNombre());
            if (identificacionViewModel.localidad != null) {
                localitySelector.setText(identificacionViewModel.localidad.toString());
            }
        } else {
            identificacionViewModel.localidadSeleccionada(null);
        }
    }

    private void prepopulateFields() {
        LocalAddress userAddress = localUser != null ? localUser.getAddress() : null;

        if (userAddress != null) {
            streetField.setText(userAddress.getStreet());
            streetNumberField.setText(userAddress.getNumber());
            zipCodeField.setText(userAddress.getPostalCode());
            doorField.setText(userAddress.getDoor());
            floorField.setText(userAddress.getFloor());
            othersField.setText(userAddress.getOthers());
        }
    }

    private Provincia getProvincia(String provincia) {
        Provincia provinciaSearch = new Provincia();
        provinciaSearch.setNombre(provincia);
        int position = provinciaAdapter.getPosition(provinciaSearch);

        return position < 0 ? null : (Provincia) provinciaAdapter.getItem(position);
    }

    private Localidad getLocalidad(String localidad, String departamento) {
        Localidad localidadSearch = new Localidad();
        localidadSearch.setNombre(localidad);
        localidadSearch.setDepartamentoNombre(departamento);
        int position = localidadAdapter.getPosition(localidadSearch);

        return position < 0 ? null : (Localidad) localidadAdapter.getItem(position);
    }

    private Boolean validateForm() {
        boolean isFormValid = true;

        if (identificacionViewModel.localidad == null || !identificacionViewModel.localidad.toString().equals(localitySelector.getText().toString())) {
            isFormValid = onInvalidField(localityTIL, getString(R.string.invalid_locality_msg_error));
        } else {
            localityTIL.setErrorEnabled(false);
        }

        if (identificacionViewModel.provinciaSeleccionado == null || !identificacionViewModel.provinciaSeleccionado.getNombre().equals(provinceSelector.getText().toString())) {
            isFormValid = onInvalidField(provinceTIL, getString(R.string.invalid_province_msg_error));
        } else {
            provinceTIL.setErrorEnabled(false);
        }

        if (isFieldEmpty(streetField) || streetField.getText().length() > MAX_STREET_LENGTH) {
            isFormValid = onInvalidField(streetTIL, getString(R.string.invalid_street_msg_error));
        } else {
            streetTIL.setErrorEnabled(false);
        }

        if (isFieldEmpty(streetNumberField) || streetNumberField.getText().toString().length() > MAX_STREET_NUMBER_LENGTH) {
            isFormValid = onInvalidField(streetNumberTIL, getString(R.string.invalid_street_number_msg_error));
        } else {
            streetNumberTIL.setErrorEnabled(false);
        }

        if (isFieldEmpty(zipCodeField) || zipCodeField.getText().toString().length() != ZIP_CODE_LENGHT) {
            isFormValid = onInvalidField(zipCodeTIL, getString(R.string.invalid_zip_code_msg_error));
        } else {
            zipCodeTIL.setErrorEnabled(false);
        }

        return isFormValid;
    }

    private boolean onInvalidField(TextInputLayout inputLayout, String errorMsg) {
        inputLayout.setErrorEnabled(true);
        inputLayout.setError(errorMsg);
        return false;
    }

    private Boolean isFieldEmpty(EditText field) {
        return field == null || field.getText().toString().isEmpty();
    }

    private void crearDialogoInternet() {
        final PantallaCompletaDialog dialog = PantallaCompletaDialog.newInstance(
                getString(R.string.hubo_error),
                getString(R.string.no_hay_internet),
                getString(R.string.cerrar).toUpperCase(),
                R.drawable.ic_error
        );

        dialog.setAccionBoton(v -> dialog.dismiss());
        dialog.show(getParentFragmentManager(), "TAG");
    }
}