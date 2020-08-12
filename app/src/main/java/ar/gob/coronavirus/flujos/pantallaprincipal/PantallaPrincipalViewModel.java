package ar.gob.coronavirus.flujos.pantallaprincipal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import ar.gob.coronavirus.data.UserStatus;
import ar.gob.coronavirus.data.local.modelo.LocalUser;
import ar.gob.coronavirus.data.repositorios.RepositorioLogout;
import ar.gob.coronavirus.flujos.BaseViewModel;
import ar.gob.coronavirus.utils.Constantes;
import ar.gob.coronavirus.utils.extensions.RxExtensionsKt;
import ar.gob.coronavirus.utils.observables.EventoUnico;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static ar.gob.coronavirus.flujos.pantallaprincipal.PantallaPrincipalViewModel.NavegacionDestinosPantallaPrincipal.CIRCULAR;
import static ar.gob.coronavirus.flujos.pantallaprincipal.PantallaPrincipalViewModel.NavegacionDestinosPantallaPrincipal.DEBE_AUTODIAGNOSTICARSE;
import static ar.gob.coronavirus.flujos.pantallaprincipal.PantallaPrincipalViewModel.NavegacionDestinosPantallaPrincipal.DERIVADO_A_SALUD_LOCAL;
import static ar.gob.coronavirus.flujos.pantallaprincipal.PantallaPrincipalViewModel.NavegacionDestinosPantallaPrincipal.DESLOGUEAR;
import static ar.gob.coronavirus.flujos.pantallaprincipal.PantallaPrincipalViewModel.NavegacionDestinosPantallaPrincipal.INFECTADO;
import static ar.gob.coronavirus.flujos.pantallaprincipal.PantallaPrincipalViewModel.NavegacionDestinosPantallaPrincipal.NO_INFECTADO;

public class PantallaPrincipalViewModel extends BaseViewModel {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public boolean permitirNavegar;

    private PantallaPrincipalRepository pantallaPrincipalRepository;
    private RepositorioLogout repositorioLogout;
    private LocalUser usuarioActual;

    private MutableLiveData<EventoUnico<NavegacionDestinosPantallaPrincipal>> eventosDeNavegacion = new MutableLiveData<>();
    private MutableLiveData<LocalUser> usuarioLiveData = new MutableLiveData<>();
    private MutableLiveData<EventoUnico<Boolean>> eventoLoadingDialog = new MutableLiveData<>();
    private MutableLiveData<EventoUnico<Integer>> eventoErrorBackend = new MutableLiveData<>();
    private MutableLiveData<EventoUnico<Intent>> levantarWebPermisoCirculacion = new MutableLiveData<>();
    private MutableLiveData<String> adviceLiveData = new MutableLiveData<>();

    public PantallaPrincipalViewModel(PantallaPrincipalRepository pantallaPrincipalRepository,
                                      RepositorioLogout repositorioLogout) {
        super();
        this.pantallaPrincipalRepository = pantallaPrincipalRepository;
        this.repositorioLogout = repositorioLogout;
    }

    public LiveData<EventoUnico<NavegacionDestinosPantallaPrincipal>> obtenerEventosDeNavegacionLiveData() {
        return eventosDeNavegacion;
    }

    public LiveData<LocalUser> obtenerUltimoEstadoLiveData() {
        return usuarioLiveData;
    }

    public LiveData<EventoUnico<Boolean>> obtenerEventoDeDialogo() {
        return eventoLoadingDialog;
    }

    public LiveData<EventoUnico<Integer>> obtenerErrorBackend() {
        return eventoErrorBackend;
    }

    public LiveData<EventoUnico<Intent>> obtenerLevantarWebLiveData() {
        return levantarWebPermisoCirculacion;
    }

    public LiveData<String> getAdviceLiveData() {
        return adviceLiveData;
    }

    @SuppressLint("CheckResult")
    public void obtenerUsuarioDeLaBD() {
        Disposable disposable = pantallaPrincipalRepository.loadUser()
                .subscribe(usuario -> {
                    usuarioActual = usuario;
                    permitirNavegar = true;
                    usuarioLiveData.setValue(usuario);
                    despacharEventoNavegacion();
                }, throwable -> Timber.d(throwable, "Error al obtener user de la BD"));
        compositeDisposable.add(disposable);
    }

    public void obtenerUltimoEstadoDeBackend() {
        eventoLoadingDialog.setValue(new EventoUnico<>(true));

        Disposable disposable = pantallaPrincipalRepository.getAdviceUrl()
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
                    eventoLoadingDialog.setValue(new EventoUnico<>(false));
                }, throwable -> {
                    Timber.e(throwable);
                    despacharEventoErrorBackend(0);
                });
        compositeDisposable.add(disposable);
    }

    @SuppressLint("CheckResult")
    public void obtenerNavegador(final NavegacionDestinosPantallaPrincipal destino) {
        eventosDeNavegacion.setValue(new EventoUnico<>(destino));
    }

    public void despacharEventoNavegacion() {
        if (permitirNavegar) {
            if (usuarioActual == null) {
                obtenerNavegador(DESLOGUEAR);
            } else {
                UserStatus nombreEstado = usuarioActual.getCurrentState().getUserStatus();
                eventoLoadingDialog.setValue(new EventoUnico<>(true));

                switch (nombreEstado) {
                    case INFECTED:
                        obtenerNavegador(INFECTADO);
                        break;
                    case DERIVED_TO_LOCAL_HEALTH:
                        obtenerNavegador(DERIVADO_A_SALUD_LOCAL);
                        break;
                    case NOT_CONTAGIOUS:
                    case NOT_INFECTED:
                        if (usuarioActual.getCurrentState().getCirculationPermit() != null && !TextUtils.isEmpty(usuarioActual.getCurrentState().getCirculationPermit().getQr())) {
                            obtenerNavegador(CIRCULAR);
                        } else {
                            obtenerNavegador(NO_INFECTADO);
                        }
                        break;
                    case MUST_SELF_DIAGNOSE:
                        obtenerNavegador(DEBE_AUTODIAGNOSTICARSE);
                        break;
                }
                eventoLoadingDialog.setValue(new EventoUnico<>(false));
            }
        }
    }

    public void despacharEventoErrorBackend(Integer error) {
        eventoErrorBackend.setValue(new EventoUnico<>(error));
    }

    public void logout() {
        compositeDisposable.add(Completable.fromAction(() -> repositorioLogout.logout()).subscribeOn(Schedulers.io())
                .subscribe(() -> {
                }, t -> Timber.e(t, "Error logging out")));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

    public void limpiarUsuarioActual() {
        permitirNavegar = false;
        usuarioActual = null;
    }

    @SuppressLint("CheckResult")
    public void habilitarCirculacion() {
        eventoLoadingDialog.setValue(new EventoUnico<Boolean>(true));
        compositeDisposable.add(
                pantallaPrincipalRepository.updateUser()
                        .subscribe(usuario -> {
                            usuarioActual = usuario;
                            eventoLoadingDialog.setValue(new EventoUnico<>(false));
                            if (usuario.getCurrentState().getCirculationPermit().getQr().isEmpty()) {
                                Intent abrirPaginaWebIntent = new Intent(Intent.ACTION_VIEW);
                                abrirPaginaWebIntent.setData(Uri.parse(Constantes.URL_HABILITAR_CIRCULACION));
                                levantarWebPermisoCirculacion.setValue(new EventoUnico<>(abrirPaginaWebIntent));
                            } else {
                                despacharEventoNavegacion();
                            }
                        }, throwable -> eventoLoadingDialog.setValue(new EventoUnico<>(false))));
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
