package ar.gob.coronavirus.utils.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class DateUtils {

    private static final String defaultFormat = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String formatoData = "yyyy-MM-dd";
    private static final String formatoDePresentacion = "dd/MM/yyyy";

    public static String obtenerFechaParaPresentacion(String fecha) {
        Date date;
        try {
            date = new SimpleDateFormat(formatoData, Locale.getDefault()).parse(fecha);
            return new SimpleDateFormat(formatoDePresentacion, Locale.getDefault()).format(date);
        } catch (ParseException e) {
            return "";
        }
    }

    private static Date convertFechaSegunFormato(String fechaString) {
        try {
            return new SimpleDateFormat(formatoData, Locale.getDefault()).parse(fechaString);
        } catch (ParseException e) {
            try {
                return new SimpleDateFormat(defaultFormat, Locale.getDefault()).parse(fechaString);
            } catch (ParseException ex) {
                Timber.d("La fecha no se puede parsear");
                return null;
            }
        }
    }

    public static int obtenerEdadPorFechaDeNacimiento(String fechaDeNacimiento) {
        Calendar fechaNacimiento = Calendar.getInstance();
        fechaNacimiento.setTime(convertFechaSegunFormato(fechaDeNacimiento));
        Calendar hoy = Calendar.getInstance();
        int edad = hoy.get(Calendar.YEAR) - fechaNacimiento.get(Calendar.YEAR);
        if (hoy.get(Calendar.DAY_OF_YEAR) < fechaNacimiento.get(Calendar.DAY_OF_YEAR))
            edad--;
        return edad;
    }

}
