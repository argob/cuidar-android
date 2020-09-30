package ar.gob.coronavirus.flujos;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import ar.gob.coronavirus.CovidApplication;
import ar.gob.coronavirus.GlobalAction;
import ar.gob.coronavirus.GlobalActionsManager;
import ar.gob.coronavirus.R;
import ar.gob.coronavirus.utils.Constantes;
import ar.gob.coronavirus.utils.PermisoDeUbicacion;
import ar.gob.coronavirus.utils.TipoDePermisoDeUbicacion;
import ar.gob.coronavirus.utils.permisos.PermisosUtileria;
import kotlin.Unit;
import timber.log.Timber;

public class BaseActivity extends AppCompatActivity {
    private BaseViewModel viewModel;
    private AlertDialog dialogoDePermisoDeUbicacion;
    private AlertDialog dialogoSinInternet;

    // Se solicita la ubicación en caso de tener sintomas compatibles para poder derivar al ciudadano al Centro de Salud más cercano.
    // Este es el único lugar y momento donde se le requiere la ubicación al usuario.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == TipoDePermisoDeUbicacion.SOLO_UBICACION) {
            capturarUbicacionDeUsuario(grantResults);
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                CovidApplication.getInstance().getSharedUtils().putBoolean(Constantes.SHARED_KEY_DENY_SERVICE, true);
            }
        }
    }


    private void capturarUbicacionDeUsuario(@NonNull int[] grantResults) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            viewModel.setPermissionDialogResult(PermisosUtileria.tieneAccesoAUbicacion(grantResults));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        GlobalActionsManager.INSTANCE.subscribe(action -> {
            if (action == GlobalAction.NO_INTERNET_CONNECTION) {
                runOnUiThread(() -> {
                    try {
                        if (!BaseActivity.this.isFinishing()) {
                            mostrarDialogoSinInternet();
                        }
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                });
            }
            return Unit.INSTANCE;
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialogoDePermisoDeUbicacion != null && dialogoDePermisoDeUbicacion.isShowing()) {
            dialogoDePermisoDeUbicacion.dismiss();
        }

        if (dialogoSinInternet != null && dialogoSinInternet.isShowing()) {
            dialogoSinInternet.dismiss();
        }
    }

    public void mostrarDialogoDeUbicacion(final Integer tipoDePermisoDeUbicacion) {
        PermisoDeUbicacion permisoDeUbicacion = PermisosUtileria.validarPermisoDeUbicacionGeneral(this);
        if (permisoDeUbicacion == PermisoDeUbicacion.SIN_PERMISO) {
            if (dialogoDePermisoDeUbicacion == null || !dialogoDePermisoDeUbicacion.isShowing()) {
                crearDialogoParaSolicitarPermisosDeUbicacion(tipoDePermisoDeUbicacion);
            }
        }
    }


    protected void mostrarDialogoSinInternet() {
        if (dialogoSinInternet == null || !dialogoSinInternet.isShowing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
            builder.setMessage(R.string.error_no_internet);
            builder.setPositiveButton(R.string.aceptar, (dialog, which) -> dialog.dismiss());
            builder.setCancelable(false);
            dialogoSinInternet = builder.show();
        }
    }

    private void crearDialogoParaSolicitarPermisosDeUbicacion(final Integer tipoDePermisoDeUbicacion) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialogo_pedir_ubicacion, null);
        builder.setView(view);
        builder.setPositiveButton(getString(R.string.permitir), (dialog, which) -> {
            dialog.dismiss();
            validarPermisoDeUbicacionActual(tipoDePermisoDeUbicacion);
        });
        builder.setNegativeButton(getString(R.string.cancelar), (dialog, which) -> {
            viewModel.setPermissionDialogResult(false);
            dialog.dismiss();
        });
        dialogoDePermisoDeUbicacion = builder.show();
    }

    private void validarPermisoDeUbicacionActual(Integer tipoDePermisoDeUbicacion) {
        PermisoDeUbicacion permisoDeUbicacion = PermisosUtileria.validarPermisoDeUbicacionGeneral(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (permisoDeUbicacion == PermisoDeUbicacion.SIN_PERMISO) {
                PermisosUtileria.solicitarPermisoDeUbicacionAndroidQ(this, tipoDePermisoDeUbicacion);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permisoDeUbicacion == PermisoDeUbicacion.SIN_PERMISO) {
                    PermisosUtileria.solicitarPermisoDeUbicacion(this, tipoDePermisoDeUbicacion);
                }
            }
        }
    }

    protected void setBaseViewModel(BaseViewModel baseViewModel) {
        this.viewModel = baseViewModel;
        observarPermisosDeUbicacionParaLanzarServicioDeRastreo();
    }

    private void observarPermisosDeUbicacionParaLanzarServicioDeRastreo() {
        viewModel.getShowLocationPermissionDialogLiveData().observe(this, tipoDePermisoDeUbicacion -> {
            if (tipoDePermisoDeUbicacion == TipoDePermisoDeUbicacion.SOLO_UBICACION) {
                mostrarDialogoDeUbicacion(tipoDePermisoDeUbicacion);
            }
        });
    }
}
