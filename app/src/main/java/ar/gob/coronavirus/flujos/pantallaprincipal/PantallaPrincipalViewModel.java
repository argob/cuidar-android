package ar.gob.coronavirus.flujos.pantallaprincipal;

import android.content.Intent;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import ar.gob.coronavirus.GlobalAction;
import ar.gob.coronavirus.GlobalActionsManager;
import ar.gob.coronavirus.data.UserStatus;
import ar.gob.coronavirus.data.local.modelo.UserWithPermits;
import ar.gob.coronavirus.data.repositorios.LogoutRepository;
import ar.gob.coronavirus.flujos.BaseViewModel;
import ar.gob.coronavirus.utils.Constantes;
import ar.gob.coronavirus.utils.extensions.RxExtensionsKt;
import ar.gob.coronavirus.utils.observables.Event;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static ar.gob.coronavirus.flujos.pantallaprincipal.PantallaPrincipalViewModel.NavegacionDestinosPantallaPrincipal.CIRCULAR;
import static ar.gob.coronavirus.flujos.pantallaprincipal.PantallaPrincipalViewModel.NavegacionDestinosPantallaPrincipal.DEBE_AUTODIAGNOSTICARSE;
import static ar.gob.coronavirus.flujos.pantallaprincipal.PantallaPrincipalViewModel.NavegacionDestinosPantallaPrincipal.DERIVADO_A_SALUD_LOCAL;
import static ar.gob.coronavirus.flujos.pantallaprincipal.PantallaPrincipalViewModel.NavegacionDestinosPantallaPrincipal.DESLOGUEAR;
import static ar.gob.coronavirus.flujos.pantallaprincipal.PantallaPrincipalViewModel.NavegacionDestinosPantallaPrincipal.INFECTADO;
import static ar.gob.coronavirus.flujos.pantallaprincipal.PantallaPrincipalViewModel.NavegacionDestinosPantallaPrincipal.NO_INFECTADO;

public class PantallaPrincipalViewModel extends BaseViewModel {

    public boolean permitirNavegar;

    private PantallaPrincipalRepository pantallaPrincipalRepository;
    private LogoutRepository logoutRepository;
    private UserWithPermits usuarioActual;

    private MutableLiveData<Event<NavegacionDestinosPantallaPrincipal>> eventosDeNavegacion = new MutableLiveData<>();
    private MutableLiveData<UserWithPermits> usuarioLiveData = new MutableLiveData<>();
    private MutableLiveData<Event<Boolean>> eventoLoadingDialog = new MutableLiveData<>();
    private MutableLiveData<Event<Integer>> eventoErrorBackend = new MutableLiveData<>();
    private MutableLiveData<Event<Intent>> levantarWebPermisoCirculacion = new MutableLiveData<>();
    private MutableLiveData<String> adviceLiveData = new MutableLiveData<>();

    public PantallaPrincipalViewModel(PantallaPrincipalRepository pantallaPrincipalRepository,
                                      LogoutRepository logoutRepository) {
        super();
        this.pantallaPrincipalRepository = pantallaPrincipalRepository;
        this.logoutRepository = logoutRepository;
    }

    public LiveData<Event<NavegacionDestinosPantallaPrincipal>> obtenerEventosDeNavegacionLiveData() {
        return eventosDeNavegacion;
    }

    public LiveData<UserWithPermits> obtenerUltimoEstadoLiveData() {
        return usuarioLiveData;
    }

    public LiveData<Event<Boolean>> obtenerEventoDeDialogo() {
        return eventoLoadingDialog;
    }

    public LiveData<Event<Integer>> obtenerErrorBackend() {
        return eventoErrorBackend;
    }

    public LiveData<Event<Intent>> obtenerLevantarWebLiveData() {
        return levantarWebPermisoCirculacion;
    }

    public LiveData<String> getAdviceLiveData() {
        return adviceLiveData;
    }

    public void obtenerUsuarioDeLaBD() {
        addDisposable(pantallaPrincipalRepository.loadUser()
                .subscribe(usuario -> {
                    usuarioActual = usuario;
                    permitirNavegar = true;
                    usuarioLiveData.setValue(usuario);
                    despacharEventoNavegacion();
                }, throwable -> Timber.d(throwable, "Error al obtener user de la BD")));
    }

    public void obtenerUltimoEstadoDeBackend() {
        eventoLoadingDialog.setValue(new Event<>(true));

        addDisposable(pantallaPrincipalRepository.getAdviceUrl()
                .onErrorReturnItem("")
                .flatMap(url -> {
                    if (!url.isEmpty())
                        adviceLiveData.setValue(url);
                    return Single.zip(pantallaPrincipalRepository.updateUser(),
                            RxExtensionsKt.delayedSingle(true, 3), // Ensure request takes at least 3 seconds
                            (localUser, aBoolean) -> localUser);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(usuario -> {
                    usuarioActual = usuario;
                    permitirNavegar = true;
                    usuarioLiveData.setValue(usuario);
                    eventoLoadingDialog.setValue(new Event<>(false));
                }, throwable -> {
                    Timber.e(throwable);
                    despacharEventoErrorBackend(0);
                }));
    }

    public void obtenerNavegador(final NavegacionDestinosPantallaPrincipal destino) {
        eventosDeNavegacion.setValue(new Event<>(destino));
    }

    public void despacharEventoNavegacion() {
        if (permitirNavegar) {
            if (usuarioActual == null) {
                obtenerNavegador(DESLOGUEAR);
            } else {
                UserStatus nombreEstado = usuarioActual.getUser().getCurrentState().getUserStatus();
                eventoLoadingDialog.setValue(new Event<>(true));

                switch (nombreEstado) {
                    case INFECTED:
                        obtenerNavegador(INFECTADO);
                        break;
                    case DERIVED_TO_LOCAL_HEALTH:
                        obtenerNavegador(DERIVADO_A_SALUD_LOCAL);
                        break;
                    case NOT_CONTAGIOUS:
                    case NOT_INFECTED:
                        if (usuarioActual.getPermits().isEmpty()) {
                            obtenerNavegador(NO_INFECTADO);
                        } else {
                            obtenerNavegador(CIRCULAR);
                        }
                        break;
                    case MUST_SELF_DIAGNOSE:
                        obtenerNavegador(DEBE_AUTODIAGNOSTICARSE);
                        break;
                }
                eventoLoadingDialog.setValue(new Event<>(false));
            }
        }
    }

    public void despacharEventoErrorBackend(Integer error) {
        eventoErrorBackend.setValue(new Event<>(error));
    }

    public void logout() {
        addDisposable(logoutRepository.logout()
                .subscribeOn(Schedulers.io())
                .subscribe(() -> GlobalActionsManager.INSTANCE.post(GlobalAction.LOGOUT),
                        t -> Timber.e(t, "Error logging out")));
    }

    public void limpiarUsuarioActual() {
        permitirNavegar = false;
        usuarioActual = null;
    }

    public void habilitarCirculacion() {
        eventoLoadingDialog.setValue(new Event<>(true));
        addDisposable(
                pantallaPrincipalRepository.updateUser()
                        .subscribe(userWithPermits -> {
                            usuarioActual = userWithPermits;
                            eventoLoadingDialog.setValue(new Event<>(false));
                            if (userWithPermits.getPermits().isEmpty()) {
                                Intent abrirPaginaWebIntent = new Intent(Intent.ACTION_VIEW);
                                abrirPaginaWebIntent.setData(Uri.parse(Constantes.URL_HABILITAR_CIRCULACION));
                                levantarWebPermisoCirculacion.setValue(new Event<>(abrirPaginaWebIntent));
                            } else {
                                despacharEventoNavegacion();
                            }
                        }, throwable -> eventoLoadingDialog.setValue(new Event<>(false))));
    }

    public enum NavegacionDestinosPantallaPrincipal {
        CIRCULAR,
        INFECTADO,
        NO_INFECTADO,
        DERIVADO_A_SALUD_LOCAL,
        DEBE_AUTODIAGNOSTICARSE,
        DESLOGUEAR
    }
}
