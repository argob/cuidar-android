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

import ar.gob.coronavirus.GlobalAction;
import ar.gob.coronavirus.GlobalActionsManager;
import ar.gob.coronavirus.data.UserStatus;
import ar.gob.coronavirus.data.local.modelo.LocalUser;
import ar.gob.coronavirus.data.remoto.modelo.RemoteLocation;
import ar.gob.coronavirus.data.remoto.modelo_autodiagnostico.RemoteAntecedents;
import ar.gob.coronavirus.data.remoto.modelo_autodiagnostico.RemoteSelfEvaluation;
import ar.gob.coronavirus.data.remoto.modelo_autodiagnostico.RemoteSymptom;
import ar.gob.coronavirus.data.repositorios.LogoutRepository;
import ar.gob.coronavirus.data.repositorios.SelfEvaluationRepository;
import ar.gob.coronavirus.flujos.BaseViewModel;
import ar.gob.coronavirus.flujos.identificacion.IdentificationRepository;
import ar.gob.coronavirus.utils.observables.Event;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class AutodiagnosticoViewModel extends BaseViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final SelfEvaluationRepository selfEvaluationRepository;
    private IdentificationRepository identificationRepository;
    private final LogoutRepository logoutRepository;

    private final RemoteSelfEvaluation remoteSelfEvaluation;
    private final Map<String, RemoteSymptom> sintomas = new HashMap<>();
    private final Map<String, RemoteAntecedents> antecedentes = new HashMap<>();
    private final MutableLiveData<LocalUser> userInformationLiveData = new MutableLiveData<>();
    final MutableLiveData<ScreenState> screenStateLiveData = new MutableLiveData<>();
    final MutableLiveData<Event<EstadoAlPresionarBack>> estadoAlPresionarBack = new MutableLiveData<>();
    MutableLiveData<Boolean> obtuvoGeolocalizacion = new MutableLiveData<>();

    double temperatura = 37.0;
    public MutableLiveData<Integer> pasoActual = new MutableLiveData<>();
    private FusedLocationProviderClient fusedLocationProviderClient;
    private RemoteLocation remoteLocation = null;

    public AutodiagnosticoViewModel(
            SelfEvaluationRepository selfEvaluationRepository,
            IdentificationRepository identificationRepository,
            LogoutRepository logoutRepository,
            FusedLocationProviderClient fusedLocationProviderClient
    ) {
        super();
        this.selfEvaluationRepository = selfEvaluationRepository;
        this.identificationRepository = identificationRepository;
        this.logoutRepository = logoutRepository;
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

        compositeDisposable.add(selfEvaluationRepository
                .confirmSelfEvaluation(remoteSelfEvaluation)
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
        compositeDisposable.add(selfEvaluationRepository
                .getUserStatus()
                .subscribe(estadoUsuario -> {
                    if (estadoUsuario == UserStatus.MUST_SELF_DIAGNOSE) {
                        estadoAlPresionarBack.setValue(new Event(EstadoAlPresionarBack.DebeDiagnosticarse));
                    } else {
                        estadoAlPresionarBack.setValue(new Event(EstadoAlPresionarBack.Diagnosticado));
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

    void modificarAntecedente(String id, boolean valor) {
        RemoteAntecedents remoteAntecedents = antecedentes.get(id);
        if (remoteAntecedents != null) {
            remoteAntecedents.setValue(valor);
            antecedentes.put(id, remoteAntecedents);
        }
    }

    RemoteAntecedents getAntecedent(@NotNull Antecedents antecedent) {
        return antecedentes.get(antecedent.getId());
    }

    boolean noTieneAntecedentes() {
        return antecedentes.isEmpty();
    }

    void setTemperatura(double temperatura) {
        remoteSelfEvaluation.setTemperature(temperatura);
    }

    RemoteSymptom getSymptom(@NonNull Symptoms symptom) {
        return sintomas.get(symptom.getValue());
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

    void logout() {
        Disposable disposable = logoutRepository.logout()
                .subscribeOn(Schedulers.io())
                .subscribe(() -> GlobalActionsManager.INSTANCE.post(GlobalAction.LOGOUT),
                        throwable -> Timber.d("Error al desloguear usuario"));
        compositeDisposable.add(disposable);
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
        boolean smellLoss = getSymptom(Symptoms.SMELL_LOSS).getValue();
        boolean tasteLoss = getSymptom(Symptoms.TASTE_LOSS).getValue();

        boolean diarrhea = getSymptom(Symptoms.DIARRHEA).getValue();
        boolean vomit = getSymptom(Symptoms.VOMIT).getValue();

        int diarrheaOrVomitCount = booleanToInt(diarrhea || vomit);
        int headacheCount = booleanToInt(getSymptom(Symptoms.HEADACHE).getValue());

        // Criterio 2
        int count = booleanToInt(temperatura >= 37.5)
                + booleanToInt(getSymptom(Symptoms.BREATHING_DIFFICULTY).getValue())
                + booleanToInt(getSymptom(Symptoms.COUGH).getValue())
                + booleanToInt(getSymptom(Symptoms.SORE_THROAT).getValue())
                + booleanToInt(getSymptom(Symptoms.MUSCLE_ACHE).getValue())
                + booleanToInt(smellLoss || tasteLoss)
                + headacheCount
                + diarrheaOrVomitCount;
        if (count >= 2) {
            return true;
        }

        // Dolor de cabeza, diarrea y vomito no cuentan como sintoms para contacto estrecho
        count -= (headacheCount + diarrheaOrVomitCount);
        // Criterio 3
        RemoteAntecedents antecedentContact1 = getAntecedent(Antecedents.A_CE1);
        RemoteAntecedents antecedentContact2 = getAntecedent(Antecedents.A_CE2);

        return ((antecedentContact1 != null && antecedentContact1.getValue()) ||
                (antecedentContact2 != null && antecedentContact2.getValue())) &&
                count >= 1;
    }

    private int booleanToInt(boolean value) {
        return value ? 1 : 0;
    }

    public void obtenerInformacionDeUsuario() {
        compositeDisposable.add(selfEvaluationRepository
                .getUser()
                .subscribe(userInformationLiveData::setValue,
                        throwable -> Timber.d("Error al obtener el último estado")));
    }
}
