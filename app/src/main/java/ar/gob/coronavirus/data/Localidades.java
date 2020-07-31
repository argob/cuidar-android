package ar.gob.coronavirus.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Localidades {

    @SerializedName("cantidad")
    @Expose
    private Integer cantidad;
    @SerializedName("inicio")
    @Expose
    private Integer inicio;
    @SerializedName("localidades")
    @Expose
    private List<Localidad> localidades = null;
    @SerializedName("total")
    @Expose
    private Integer total;

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getInicio() {
        return inicio;
    }

    public void setInicio(Integer inicio) {
        this.inicio = inicio;
    }

    public List<Localidad> getLocalidades() {
        return localidades;
    }

    public void setLocalidades(List<Localidad> localidades) {
        this.localidades = localidades;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Localidades{" +
                "cantidad=" + cantidad +
                ", inicio=" + inicio +
                ", localidades=" + localidades +
                ", total=" + total +
                '}';
    }
}
