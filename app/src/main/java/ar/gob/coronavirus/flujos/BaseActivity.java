package ar.gob.coronavirus.flujos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import ar.gob.coronavirus.CovidApplication;
import ar.gob.coronavirus.R;
import ar.gob.coronavirus.utils.Constantes;
import ar.gob.coronavirus.utils.PermisoDeUbicacion;
import ar.gob.coronavirus.utils.TipoDePermisoDeUbicacion;
import ar.gob.coronavirus.utils.permisos.PermisosUtileria;

public class BaseActivity extends AppCompatActivity {
    private BaseViewModel viewModel;
    private AlertDialog dialogoDePermisoDeUbicacion;

    // Se solicita la ubicación en caso de tener sintomas compatibles para poder derivar al ciudadano al Centro de Salud más cercano.
    // Este es el único lugar y momento donde se le requiere la ubicación al usuario.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == TipoDePermisoDeUbicacion.SOLO_UBICACION) {
            capturarUbicacionDeUsuario(requestCode, permissions, grantResults);
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                CovidApplication.getInstance().getSharedUtils().putBoolean(Constantes.SHARED_KEY_DENY_SERVICE, true);
            }
        }
    }

    private void capturarUbicacionDeUsuario(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            PermisoDeUbicacion permisoDeUbicacion = PermisosUtileria.validarResultadoDePermisoDeUbicacionApi29(this, permissions, grantResults, requestCode);
            viewModel.setResultadoDialogoCustomPermisoDeUbicacion(permisoDeUbicacion == PermisoDeUbicacion.SOLO_CON_LA_APLICACION_VISIBLE);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            viewModel.setResultadoDialogoCustomPermisoDeUbicacion(PermisosUtileria.tieneAccesoAUbicacion(grantResults));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialogoDePermisoDeUbicacion != null && dialogoDePermisoDeUbicacion.isShowing()) {
            dialogoDePermisoDeUbicacion.dismiss();
        }
    }

    public boolean mostrarDialogoDeUbicacion(final Integer tipoDePermisoDeUbicacion) {
        PermisoDeUbicacion permisoDeUbicacion = PermisosUtileria.validarPermisoDeUbicacionGeneral(this, tipoDePermisoDeUbicacion);
        boolean tienePermisoDeUbicacion = false;
        switch (permisoDeUbicacion) {
            case SIN_PERMISO:
                if (dialogoDePermisoDeUbicacion == null || !dialogoDePermisoDeUbicacion.isShowing()) {
                    crearDialogoParaSolicitarPermisosDeUbicacion(tipoDePermisoDeUbicacion);
                }
                break;
            case SOLO_CON_LA_APLICACION_VISIBLE:
                tienePermisoDeUbicacion = true;
                break;
            case NUNCA:
                break;
        }
        return tienePermisoDeUbicacion;
    }

    private void crearDialogoParaSolicitarPermisosDeUbicacion(final Integer tipoDePermisoDeUbicacion) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialogo_pedir_ubicacion, null);
        builder.setView(view);
        builder.setPositiveButton(getString(R.string.permitir), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                validarPermisoDeUbicacionActual(tipoDePermisoDeUbicacion);
            }
        });
        builder.setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                viewModel.setResultadoDialogoCustomPermisoDeUbicacion(false);
                dialog.dismiss();
            }
        });
        dialogoDePermisoDeUbicacion = builder.show();
    }

    private void validarPermisoDeUbicacionActual(Integer tipoDePermisoDeUbicacion) {
        PermisoDeUbicacion permisoDeUbicacion = PermisosUtileria.validarPermisoDeUbicacionGeneral(this, tipoDePermisoDeUbicacion);
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
        viewModel.obtenerLanzarDialogoPermisosLocalizacionLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer tipoDePermisoDeUbicacion) {
                switch (tipoDePermisoDeUbicacion) {
                    case TipoDePermisoDeUbicacion.SOLO_UBICACION:
                        boolean tienePermisoDeSoloUbicacion = mostrarDialogoDeUbicacion(tipoDePermisoDeUbicacion);
                        //viewModel.setResultadoDialogoCustomPermisoDeUbicacion(tienePermisoDeSoloUbicacion);
                        break;
                }
            }
        });
    }
}
