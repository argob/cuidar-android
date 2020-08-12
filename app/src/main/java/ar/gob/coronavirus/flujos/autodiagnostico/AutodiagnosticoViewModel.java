package ar.gob.coronavirus.flujos.autodiagnostico;

import android.annotation.SuppressLint;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ar.gob.coronavirus.data.UserStatus;
import ar.gob.coronavirus.data.local.modelo.LocalUser;
import ar.gob.coronavirus.data.remoto.modelo.RemoteLocation;
import ar.gob.coronavirus.data.remoto.modelo_autodiagnostico.RemoteAntecedents;
import ar.gob.coronavirus.data.remoto.modelo_autodiagnostico.RemoteSelfEvaluation;
import ar.gob.coronavirus.data.remoto.modelo_autodiagnostico.RemoteSymptom;
import ar.gob.coronavirus.data.repositorios.RepositorioAutoevaluacion;
import ar.gob.coronavirus.data.repositorios.RepositorioLogout;
import ar.gob.coronavirus.flujos.BaseViewModel;
import ar.gob.coronavirus.flujos.identificacion.IdentificationRepository;
import ar.gob.coronavirus.utils.observables.EventoUnico;
import io.reactivex.Completable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class AutodiagnosticoViewModel extends BaseViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final RepositorioAutoevaluacion repositorioAutoevaluacion;
    private IdentificationRepository identificationRepository;
    private final RepositorioLogout repositorioLogout;

    private final RemoteSelfEvaluation remoteSelfEvaluation;
    private final Map<String, RemoteSymptom> sintomas = new HashMap<>();
    private final Map<String, RemoteAntecedents> antecedentes = new HashMap<>();
    private final MutableLiveData<LocalUser> userInformationLiveData = new MutableLiveData<>();
    final MutableLiveData<ScreenState> screenStateLiveData = new MutableLiveData<>();
    final MutableLiveData<EventoUnico<EstadoAlPresionarBack>> estadoAlPresionarBack = new MutableLiveData<>();
    MutableLiveData<Boolean> obtuvoGeolocalizacion = new MutableLiveData<>();

    double temperatura = 37.0;
    public MutableLiveData<Integer> pasoActual = new MutableLiveData<>();
    private FusedLocationProviderClient fusedLocationProviderClient;
    private RemoteLocation remoteLocation = null;

    public AutodiagnosticoViewModel(
            RepositorioAutoevaluacion repositorioAutoevaluacion,
            IdentificationRepository identificationRepository,
            RepositorioLogout repositorioLogout,
            FusedLocationProviderClient fusedLocationProviderClient
    ) {
        super();
        this.repositorioAutoevaluacion = repositorioAutoevaluacion;
        this.identificationRepository = identificationRepository;
        this.repositorioLogout = repositorioLogout;
        remoteSelfEvaluation = new RemoteSelfEvaluation(
                0.0F,
                new ArrayList<RemoteSymptom>(),
                new ArrayList<RemoteAntecedents>(),
                null
        );
        this.fusedLocationProviderClient = fusedLocationProviderClient;
    }

    public LiveData<LocalUser> getUserInformation() {
        return userInformationLiveData;
    }

    public MutableLiveData<Boolean> obtenerGeolocalizacionLivaData() {
        return obtuvoGeolocalizacion;
    }

    void enviarResultadosAutoevaluacion() {
        screenStateLiveData.setValue(ScreenState.SendingToServer);
        remoteSelfEvaluation.getAntecedents().clear();
        remoteSelfEvaluation.getAntecedents().addAll(antecedentes.values());
        remoteSelfEvaluation.getSymptoms().clear();
        remoteSelfEvaluation.getSymptoms().addAll(sintomas.values());

        compositeDisposable.add(repositorioAutoevaluacion
                .confirmarAutoevaluacion(remoteSelfEvaluation)
                .subscribe(usuarioRemoto -> {
                    // Si luego de la autoevaluación se determino que es compatible covid-19
                    // luego de enviar el resultado se solicita confirmar el telefono para
                    // reconfirmar que el nro este OK
                    if (UserStatus.DERIVED_TO_LOCAL_HEALTH == usuarioRemoto.getCurrentState().getUserStatus()) {
                        screenStateLiveData.setValue(ScreenState.PhoneConfirmation);
                    } else {
                        screenStateLiveData.setValue(ScreenState.MainScreen);
                    }
                }, throwable -> {
                    screenStateLiveData.setValue(ScreenState.ServerError);
                }));
    }

    void manejarBotonBack() {
        compositeDisposable.add(repositorioAutoevaluacion
                .obtenerEstadoUsuario()
                .subscribe(estadoUsuario -> {
                    if (estadoUsuario == UserStatus.MUST_SELF_DIAGNOSE) {
                        estadoAlPresionarBack.setValue(new EventoUnico(EstadoAlPresionarBack.DebeDiagnosticarse));
                    } else {
                        estadoAlPresionarBack.setValue(new EventoUnico(EstadoAlPresionarBack.Diagnosticado));
                    }
                }, throwable -> {
                }));
    }

    void agregarSintoma(RemoteSymptom sintoma) {
        sintomas.put(sintoma.getId(), sintoma);
    }

    void agregarAntecedente(RemoteAntecedents antecedente) {
        antecedentes.put(antecedente.getId(), antecedente);
    }

    void modificarAntecedente(String descripcion, boolean valor) {
        RemoteAntecedents remoteAntecedents = antecedentes.get(descripcion);
        if (remoteAntecedents != null) {
            remoteAntecedents.setValue(valor);
            antecedentes.put(descripcion, remoteAntecedents);
        }
    }

    RemoteAntecedents obtenerAntecedente(@NotNull String descripcion) {
        return antecedentes.get(descripcion);
    }

    boolean noTieneAntecedentes() {
        return antecedentes.isEmpty();
    }

    void setTemperatura(double temperatura) {
        remoteSelfEvaluation.setTemperature(temperatura);
    }

    RemoteSymptom obtenerSintoma(@NonNull Symptoms tipoSintoma) {
        return sintomas.get(tipoSintoma.getValue());
    }

    public RemoteSelfEvaluation obtenerAutoevaluacion() {
        remoteSelfEvaluation.getSymptoms().clear();
        remoteSelfEvaluation.getSymptoms().addAll(sintomas.values());
        remoteSelfEvaluation.getAntecedents().clear();
        remoteSelfEvaluation.getAntecedents().addAll(antecedentes.values());
        return remoteSelfEvaluation;
    }

    public void updatePhone(@NonNull String phone) {
        LocalUser user = userInformationLiveData.getValue();
        if (user != null) {
            if (phone.equals(user.getPhone())) {
                screenStateLiveData.setValue(ScreenState.MainScreen);
            } else {
                // Si se cambia el numero de teléfono se actualizan todos los datos del usuario
                Disposable disposable = identificationRepository
                        .updateUser(String.valueOf(user.getDni()), user.getGender(), phone, user.getAddress(), user.getLocation())
                        .subscribe(() -> {
                            screenStateLiveData.setValue(ScreenState.MainScreen);
                        }, throwable -> screenStateLiveData.setValue(ScreenState.ServerError));

                compositeDisposable.add(disposable);
            }
        }
    }

    enum ScreenState {
        MainScreen, ServerError, SendingToServer, PhoneConfirmation
    }

    enum EstadoAlPresionarBack {
        Diagnosticado, DebeDiagnosticarse
    }

    @SuppressLint("CheckResult")
    void logout() {
        compositeDisposable.add(Completable
                .fromAction(repositorioLogout::logout)
                .subscribeOn(Schedulers.io()).subscribe(() ->
                                Timber.d("Usuario deslogueado"),
                        throwable -> Timber.d("Error al desloguear usuario")));
    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
        super.onCleared();
    }

    @SuppressLint("MissingPermission")
    public void obtenerUbicacionLatLong() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            Location ubicacion = task.getResult();
            if (ubicacion != null) {
                remoteLocation = new RemoteLocation(Double.toString(ubicacion.getLatitude()),
                        Double.toString(ubicacion.getLongitude()));
            }
            remoteSelfEvaluation.setRemoteLocation(remoteLocation);

            obtuvoGeolocalizacion.setValue(true);
        });
    }

    public boolean debePedirPermisoDeLocalizacion() {
        boolean smellLoss = obtenerSintoma(Symptoms.S_PDO).getValue();
        boolean tasteLoss = obtenerSintoma(Symptoms.S_PDG).getValue();
        // Criterio 4
        if (smellLoss || tasteLoss) {
            return true;
        }

        // Criterio 2
        int count = booleanToInt(temperatura >= 37.5)
                + booleanToInt(obtenerSintoma(Symptoms.S_DRE).getValue())
                + booleanToInt(obtenerSintoma(Symptoms.S_TOS).getValue())
                + booleanToInt(obtenerSintoma(Symptoms.S_DDG).getValue())
                + booleanToInt(smellLoss || tasteLoss); // IDE warns always false, but for reading purposes...
        if (count >= 2) {
            return true;
        }

        // Criterio 3
        RemoteAntecedents antecedentContact1 = antecedentes.get(Antecedents.A_CE1.getId());
        RemoteAntecedents antecedentContact2 = antecedentes.get(Antecedents.A_CE2.getId());

        return ((antecedentContact1 != null && antecedentContact1.getValue()) ||
                (antecedentContact2 != null && antecedentContact2.getValue())) &&
                count >= 1;
    }

    private int booleanToInt(boolean value) {
        return value ? 1 : 0;
    }

    public void obtenerInformacionDeUsuario() {
        compositeDisposable.add(repositorioAutoevaluacion
                .obtenerUsuario()
                .subscribe(userInformationLiveData::setValue,
                        throwable -> Timber.d("Error al obtener el último estado")));
    }
}
