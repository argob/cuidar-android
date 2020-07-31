package ar.gob.coronavirus.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Provincias {
    @SerializedName("cantidad")
    @Expose
    private Integer cantidad;
    @SerializedName("inicio")
    @Expose
    private Integer inicio;
    @SerializedName("provincias")
    @Expose
    private List<Provincia> provincias = null;
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

    public List<Provincia> getProvincias() {
        return provincias;
    }

    public void setProvincias(List<Provincia> provincias) {
        this.provincias = provincias;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
