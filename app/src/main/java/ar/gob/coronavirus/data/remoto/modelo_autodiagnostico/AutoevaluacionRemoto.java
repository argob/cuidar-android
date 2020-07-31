package ar.gob.coronavirus.data.remoto.modelo_autodiagnostico;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import ar.gob.coronavirus.data.remoto.modelo.RemoteLocation;

public class AutoevaluacionRemoto {
    private double temperatura;
    private List<SintomasRemoto> sintomas;
    private List<AntecedentesRemoto> antecedentes;
    @SerializedName("geo")
    private RemoteLocation remoteLocation;

    public AutoevaluacionRemoto(double temperatura, ArrayList<SintomasRemoto> sintomas, ArrayList<AntecedentesRemoto> antecedentes,
                                RemoteLocation remoteLocation) {
        this.temperatura = temperatura;
        this.antecedentes = antecedentes;
        this.sintomas = sintomas;
        this.remoteLocation = remoteLocation;
    }

	public double getTemperatura() {
		return temperatura;
	}

	public void setTemperatura(double temperatura) {
		this.temperatura = temperatura;
	}

	public List<SintomasRemoto> getSintomas() {
		return sintomas;
	}

	public void setSintomas(List<SintomasRemoto> sintomas) {
		this.sintomas = sintomas;
    }

    public List<AntecedentesRemoto> getAntecedentes() {
        return antecedentes;
    }

    public void setAntecedentes(List<AntecedentesRemoto> antecedentes) {
        this.antecedentes = antecedentes;
    }

    public RemoteLocation getRemoteLocation() {
        return remoteLocation;
    }

    public void setRemoteLocation(RemoteLocation remoteLocation) {
        this.remoteLocation = remoteLocation;
    }
}

