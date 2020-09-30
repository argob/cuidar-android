package ar.gob.coronavirus.flujos.identificacion;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ar.gob.coronavirus.data.DniEntity;
import ar.gob.coronavirus.data.Localidad;
import ar.gob.coronavirus.data.Localidades;
import ar.gob.coronavirus.data.Provincia;
import ar.gob.coronavirus.data.Provincias;
import ar.gob.coronavirus.data.UserStatus;
import ar.gob.coronavirus.data.local.modelo.LocalAddress;
import ar.gob.coronavirus.data.local.modelo.LocalUser;
import ar.gob.coronavirus.data.repositorios.LogoutRepository;
import ar.gob.coronavirus.flujos.BaseViewModel;
import ar.gob.coronavirus.utils.json.AssetsUtils;
import ar.gob.coronavirus.utils.observables.Event;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class IdentificacionViewModel extends BaseViewModel {

    private static final String CADENA_VACIA_PRESENTACION = " ";

    private MutableLiveData<LocalUser> usuarioliveData = new MutableLiveData<>();
    private MutableLiveData<Event<NavegacionFragments>> registrarUsuarioRespuesta = new MutableLiveData<>();
    private MutableLiveData<Event<Boolean>> actualizarUsuarioRespuesta = new MutableLiveData<>();
    private MutableLiveData<DniEntity> dniEntidadMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Provincias> provinciasMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Localidad>> localidadesSpinnerLiveData = new MutableLiveData<>();
    private MutableLiveData<Event<Boolean>> limpiarPantallaLogin = new MutableLiveData<>();

    private IdentificationRepository identificationRepository;

    private Localidades localidades;
    private LocalAddress localAddress;
    private LogoutRepository logoutRepository;

    Provincia provinciaSeleccionado = null;
    Localidad localidad = null;
    String nroTelefono = "";

    public IdentificacionViewModel(
            IdentificationRepository identificationRepository,
            LogoutRepository logoutRepository
    ) {
        super();
        this.identificationRepository = identificationRepository;
        this.logoutRepository = logoutRepository;
        this.localidades = AssetsUtils.loadFromAsset("localidades.json", Localidades.class);
        loadProvincesFromAsset();
    }

    public LiveData<LocalUser> getUsuarioLiveData() {
        return usuarioliveData;
    }

    public LiveData<Event<NavegacionFragments>> getRegistrarUsuarioLiveData() {
        return registrarUsuarioRespuesta;
    }

    public LiveData<Event<Boolean>> getActualizarUsuarioLiveData() {
        return actualizarUsuarioRespuesta;
    }

    public LiveData<DniEntity> getDniEntidadLiveData() {
        return dniEntidadMutableLiveData;
    }

    public LiveData<Event<Boolean>> getLimpiarLogin() {
        return limpiarPantallaLogin;
    }

    public LiveData<Provincias> getProvinciasLiveData() {
        return provinciasMutableLiveData;
    }

    public LiveData<List<Localidad>> getLocalidadesParaSpinner() {
        return localidadesSpinnerLiveData;
    }

    @SuppressLint("CheckResult")
    public void obtenerUsuario() {
        addDisposable(identificationRepository.getUser()
                .subscribe(localUser -> usuarioliveData.setValue(localUser), throwable -> Timber.e(throwable, "Error logging in")));
    }

    @SuppressLint("CheckResult")
    public void registerUser(final String dniNro, final String identification, final String gender) {
        addDisposable(identificationRepository.authorizeUser(dniNro, gender, identification.replaceFirst("^0+(?!$)", ""))
                .flatMap(success -> identificationRepository.registerUser(dniNro, gender))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(localUser -> {
                    if (localUser.getAddress() == null || TextUtils.isEmpty(localUser.getAddress().getProvince())) {
                        registrarUsuarioRespuesta.setValue(new Event<>(NavegacionFragments.IDENTIFICACION));
                    } else if (localUser.getCurrentState().getUserStatus() == UserStatus.MUST_SELF_DIAGNOSE) {
                        registrarUsuarioRespuesta.setValue(new Event<>(NavegacionFragments.AUTODIAGNOSTICO));
                    } else {
                        registrarUsuarioRespuesta.setValue(new Event<>(NavegacionFragments.PRINCIPAL));
                    }
                }, throwable -> {
                    Timber.e(throwable, "Error logging in");
                    registrarUsuarioRespuesta.setValue(new Event<>(NavegacionFragments.ERROR));
                }));
    }

    @SuppressLint("CheckResult")
    public void actualizarUsuario() {
        addDisposable(identificationRepository.updateUser(
                String.valueOf(usuarioliveData.getValue().getDni()),
                usuarioliveData.getValue().getGender(),
                nroTelefono,
                localAddress,
                null)
                .subscribe(() -> actualizarUsuarioRespuesta.setValue(new Event<>(true)), throwable -> {
                    Timber.e(throwable, "Error logging in");
                    actualizarUsuarioRespuesta.setValue(new Event<>(false));
                }));
    }

    public void procesarCodigoQr(String cadenaQr) {
        dniEntidadMutableLiveData.setValue(DniEntity.build(cadenaQr));
    }

    public void loadProvincesFromAsset() {
        Provincias provincias = AssetsUtils.loadFromAsset("provincias.json", Provincias.class);
        List<Provincia> sortedProvinces = new ArrayList<>(provincias.getProvincias());
        Collections.sort(sortedProvinces, (locality1, locality2) -> locality1.getNombre().compareToIgnoreCase(locality2.getNombre()));
        provincias.setProvincias(sortedProvinces);
        provinciasMutableLiveData.setValue(provincias);
    }

    void filtrarLocalidades(String provinciaId) {
        List<Localidad> localidadesSpinner = new ArrayList<>();

        for (Localidad localidad : localidades.getLocalidades()) {
            if (localidad.getProvinciaId().equals(provinciaId)) {
                localidadesSpinner.add(localidad);
            }
        }
        Collections.sort(localidadesSpinner, (locality1, locality2) -> locality1.getNombre().compareToIgnoreCase(locality2.getNombre()));
        localidadesSpinnerLiveData.setValue(localidadesSpinner);
    }

    public void crearDomicilioRemoto(
            String provincia,
            String calle,
            String numero,
            String piso,
            String puerta,
            String codigoPostal,
            String otros
    ) {
        if (localidad != null) {
            localAddress = new LocalAddress(
                    provincia,
                    localidad.getNombre() != null ? localidad.getNombre() : CADENA_VACIA_PRESENTACION,
                    localidad.getDepartamentoNombre() != null ? localidad.getDepartamentoNombre() : CADENA_VACIA_PRESENTACION,
                    calle,
                    numero,
                    piso,
                    puerta,
                    codigoPostal,
                    otros
            );
        } else {
            localAddress = new LocalAddress(
                    provincia,
                    CADENA_VACIA_PRESENTACION,
                    CADENA_VACIA_PRESENTACION,
                    calle,
                    numero,
                    piso,
                    puerta,
                    codigoPostal,
                    otros
            );
        }
    }

    void logout() {
        Disposable disposable = logoutRepository.logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> limpiarPantallaLogin.setValue(new Event<>(true)),
                        throwable -> Timber.d("Error al desloguear"));
        addDisposable(disposable);
    }

    public void localidadSeleccionada(Localidad localidadSeleccionada) {
        this.localidad = localidadSeleccionada;
    }

    public void navegarSiguientePantallaDependiendoDelEstado() {
        boolean debeAutodiagnosticarse = usuarioliveData.getValue().getCurrentState().getUserStatus() == UserStatus.MUST_SELF_DIAGNOSE;
        if (debeAutodiagnosticarse) {
            registrarUsuarioRespuesta.setValue(new Event<>(NavegacionFragments.AUTODIAGNOSTICO));
        } else {
            registrarUsuarioRespuesta.setValue(new Event<>(NavegacionFragments.PRINCIPAL));
        }
    }
}
