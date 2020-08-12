package ar.gob.coronavirus.flujos.identificacion;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ar.gob.coronavirus.BuildConfig;
import ar.gob.coronavirus.data.DniEntity;
import ar.gob.coronavirus.data.Localidad;
import ar.gob.coronavirus.data.Localidades;
import ar.gob.coronavirus.data.Provincia;
import ar.gob.coronavirus.data.Provincias;
import ar.gob.coronavirus.data.UserStatus;
import ar.gob.coronavirus.data.local.modelo.LocalAddress;
import ar.gob.coronavirus.data.local.modelo.LocalUser;
import ar.gob.coronavirus.data.repositorios.RepositorioLogout;
import ar.gob.coronavirus.flujos.BaseViewModel;
import ar.gob.coronavirus.utils.json.JsonUtileria;
import ar.gob.coronavirus.utils.observables.EventoUnico;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class IdentificacionViewModel extends BaseViewModel {

    private static final String CADENA_VACIA_PRESENTACION = " ";

    private MutableLiveData<LocalUser> usuarioliveData = new MutableLiveData<>();
    private MutableLiveData<EventoUnico<NavegacionFragments>> registrarUsuarioRespuesta = new MutableLiveData<>();
    private MutableLiveData<EventoUnico<Boolean>> actualizarUsuarioRespuesta = new MutableLiveData<>();
    private MutableLiveData<DniEntity> dniEntidadMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Provincias> provinciasMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Localidad>> localidadesSpinnerLiveData = new MutableLiveData<>();
    private MutableLiveData<EventoUnico<Boolean>> limpiarPantallaLogin = new MutableLiveData<>();

    private IdentificationRepository identificationRepository;

    private Localidades localidades;
    private LocalAddress localAddress;
    private RepositorioLogout repositorioLogout;

    Provincia provinciaSeleccionado = null;
    Localidad localidad = null;
    String nroTelefono = "";

    public IdentificacionViewModel(
            IdentificationRepository identificationRepository,
            Resources resources,
            RepositorioLogout repositorioLogout
    ) {
        super();
        this.identificationRepository = identificationRepository;
        this.repositorioLogout = repositorioLogout;
        this.localidades = crearObjetoLocalidadesDesdeString(resources);
        crearObjetoProvinciaDesdeString(resources);
    }

    public LiveData<LocalUser> getUsuarioLiveData() {
        return usuarioliveData;
    }

    public LiveData<EventoUnico<NavegacionFragments>> getRegistrarUsuarioLiveData() {
        return registrarUsuarioRespuesta;
    }

    public LiveData<EventoUnico<Boolean>> getActualizarUsuarioLiveData() {
        return actualizarUsuarioRespuesta;
    }

    public LiveData<DniEntity> getDniEntidadLiveData() {
        return dniEntidadMutableLiveData;
    }

    public LiveData<EventoUnico<Boolean>> getLimpiarLogin() {
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
    public void registrarUsuario(final String dniNro, final String dniTramite, final String sexo) {
        addDisposable(Single.fromCallable(() -> identificationRepository.authorizeUser(dniNro, sexo, dniTramite.replaceFirst("^0+(?!$)", "")))
                .flatMap(success -> {
                    if (success) {
                        return identificationRepository.registerUser(dniNro, sexo);
                    } else {
                        throw new Exception("Error registering");
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(localUser -> {
                    if (localUser.getAddress() == null || TextUtils.isEmpty(localUser.getAddress().getProvince())) {
                        registrarUsuarioRespuesta.setValue(new EventoUnico(NavegacionFragments.IDENTIFICACION));
                    } else if (localUser.getCurrentState().getUserStatus() == UserStatus.MUST_SELF_DIAGNOSE) {
                        registrarUsuarioRespuesta.setValue(new EventoUnico(NavegacionFragments.AUTODIAGNOSTICO));
                    } else {
                        registrarUsuarioRespuesta.setValue(new EventoUnico(NavegacionFragments.PRINCIPAL));
                    }
                }, throwable -> {
                    Timber.e(throwable, "Error logging in");
                    registrarUsuarioRespuesta.setValue(new EventoUnico(NavegacionFragments.ERROR));
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
                .subscribe(() -> actualizarUsuarioRespuesta.setValue(new EventoUnico<>(true)), throwable -> {
                    Timber.e(throwable, "Error logging in");
                    actualizarUsuarioRespuesta.setValue(new EventoUnico<>(false));
                }));
    }

    public void procesarCodigoQr(String cadenaQr) {
        dniEntidadMutableLiveData.setValue(DniEntity.build(cadenaQr));
    }

    public String obtenerLocalidadesDeAssets(Resources resources) {
        return JsonUtileria.obtenerJsonDeAsset(resources, "localidades.json");
    }

    public String obtenerProvinciasDeAssets(Resources resources) {
        return JsonUtileria.obtenerJsonDeAsset(resources, "provincias.json");
    }

    public Localidades crearObjetoLocalidadesDesdeString(Resources resources) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Localidades localidades = gson.fromJson(obtenerLocalidadesDeAssets(resources), Localidades.class);
        return localidades;
    }

    public void crearObjetoProvinciaDesdeString(Resources resources) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Provincias provincias = gson.fromJson(obtenerProvinciasDeAssets(resources), Provincias.class);
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
        addDisposable((Completable.fromAction(() ->
                repositorioLogout.logout()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> limpiarPantallaLogin.setValue(new EventoUnico(true)), throwable -> {
                    if (BuildConfig.DEBUG)
                        Timber.d("Error al desloguear");
                })));
    }

    public void localidadSeleccionada(Localidad localidadSeleccionada) {
        this.localidad = localidadSeleccionada;
    }

    public void navegarSiguientePantallaDependiendoDelEstado() {
        boolean debeAutodiagnosticarse = usuarioliveData.getValue().getCurrentState().getUserStatus() == UserStatus.MUST_SELF_DIAGNOSE;
        if (debeAutodiagnosticarse) {
            registrarUsuarioRespuesta.setValue(new EventoUnico<>(NavegacionFragments.AUTODIAGNOSTICO));
        } else {
            registrarUsuarioRespuesta.setValue(new EventoUnico<>(NavegacionFragments.PRINCIPAL));
        }
    }
}
