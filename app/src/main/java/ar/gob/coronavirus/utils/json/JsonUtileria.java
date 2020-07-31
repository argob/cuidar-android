package ar.gob.coronavirus.utils.json;

import android.content.res.Resources;

import java.io.IOException;
import java.io.InputStream;

public class JsonUtileria {
    public static String obtenerJsonDeAsset(final Resources resources, String nombreJsonArchivo){
        String json = null;
        try {
            InputStream is = resources.getAssets().open(nombreJsonArchivo);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
