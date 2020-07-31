package ar.gob.coronavirus.data.remoto.modelo;

import com.google.gson.annotations.SerializedName;

public class PushBody {

    public PushBody(String idApp) {
        this.idApp = idApp;
    }

    @SerializedName("id-app")
    public String idApp;
}
