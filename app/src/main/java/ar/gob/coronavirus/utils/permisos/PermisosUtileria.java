package ar.gob.coronavirus.utils.permisos;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import ar.gob.coronavirus.CovidApplication;
import ar.gob.coronavirus.utils.Constantes;
import ar.gob.coronavirus.utils.PermisoDeUbicacion;

public class PermisosUtileria {

    /*
     * Este permiso sólo se utiliza para leer el código de barras de un formato determinado del DNI y que sea más fácil la carga de los datos solicitados en el ingreso a la aplicación.
     *
     * @param fragment Fragmento que solicita el permiso
     * @param codigo   El código de respuesta que devuelve la solicitud de permisos
     * @param permiso  El permiso que está solicitando
     * @return Si la aplicación ya cuenta con permiso devuelve true, de lo contrario devuelve false y hace la solicitud al usuario.
     */
    public static boolean revisarPermiso(Fragment fragment, int codigo, String permiso) {
        if (ContextCompat.checkSelfPermission(fragment.requireContext(), permiso) != PackageManager.PERMISSION_GRANTED) {
            fragment.requestPermissions(new String[]{permiso}, codigo);
            return false;
        }
        return true;
    }

    // Se solicita la ubicación en caso de tener síntomas compatibles para poder derivar al ciudadano al Centro de Salud más cercano.
    // Este es el único lugar y momento donde se le requiere la ubicación al usuario.

    public static boolean revisarPermisoSinSolicitar(Context context, String permiso) {
        return ContextCompat.checkSelfPermission(context, permiso) == PackageManager.PERMISSION_GRANTED;
    }

    public static PermisoDeUbicacion validarPermisoDeUbicacionGeneral(final AppCompatActivity activity) {

        boolean denegoPermiso = CovidApplication.getInstance().getSharedUtils().getBoolean(Constantes.SHARED_KEY_DENY_SERVICE, false);
        boolean sePuedeVolverAPedirPermiso = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            PermisoDeUbicacion permisoDeUbicacion = obtenerPermisoActualDeUbicacion(activity);
            if (permisoDeUbicacion.equals(PermisoDeUbicacion.SIN_PERMISO)) {
                if (!denegoPermiso) {
                    return PermisoDeUbicacion.SIN_PERMISO;
                } else {
                    if (sePuedeVolverAPedirPermiso) {
                        return PermisoDeUbicacion.SIN_PERMISO;
                    } else {
                        return PermisoDeUbicacion.NUNCA;
                    }
                }
            }
            return permisoDeUbicacion;
        } else {
            PermisoDeUbicacion permisoDeUbicacion = validarPermisoDeUbicacion(activity);
            if (permisoDeUbicacion.equals(PermisoDeUbicacion.SIN_PERMISO)) {
                if (!denegoPermiso) {
                    return PermisoDeUbicacion.SIN_PERMISO;
                } else {
                    if (sePuedeVolverAPedirPermiso) {
                        return PermisoDeUbicacion.SIN_PERMISO;
                    } else {
                        return PermisoDeUbicacion.NUNCA;
                    }
                }
            }
            return permisoDeUbicacion;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void solicitarPermisoDeUbicacionAndroidQ(final Activity activity,
                                                           final int codigoDePeticion) {
        ActivityCompat.requestPermissions(activity, new String[] {
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, codigoDePeticion);
    }

    public static boolean tieneAccesoAUbicacion(@NonNull int[] grantResults) {
        return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    private static PermisoDeUbicacion validarPermisoDeUbicacion(final AppCompatActivity activity) {
        if (revisarPermisoSinSolicitar(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            return PermisoDeUbicacion.SOLO_CON_LA_APLICACION_VISIBLE;
        } else {
            return PermisoDeUbicacion.SIN_PERMISO;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static PermisoDeUbicacion obtenerPermisoActualDeUbicacion(final Context context) {
        boolean permissionAccessCoarseLocationApproved =
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        if (permissionAccessCoarseLocationApproved) {
            return PermisoDeUbicacion.SOLO_CON_LA_APLICACION_VISIBLE;
        } else {
            return PermisoDeUbicacion.SIN_PERMISO;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void solicitarPermisoDeUbicacion(Activity context, int codigoDePermiso) {
        String permiso = Manifest.permission.ACCESS_COARSE_LOCATION;
        if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
            context.requestPermissions(new String[]{permiso}, codigoDePermiso);
        }
    }
}
