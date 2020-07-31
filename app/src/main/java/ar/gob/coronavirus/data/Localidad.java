package ar.gob.coronavirus.data;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Localidad {

    @SerializedName("departamento_id")
    @Expose
    private String departamentoId;
    @SerializedName("departamento_nombre")
    @Expose
    private String departamentoNombre;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("localidad_censal_id")
    @Expose
    private String localidadCensalId;
    @SerializedName("localidad_censal_nombre")
    @Expose
    private String localidadCensalNombre;
    @SerializedName("nombre")
    @Expose
    private String nombre;
    @SerializedName("provincia_id")
    @Expose
    private String provinciaId;
    @SerializedName("provincia_nombre")
    @Expose
    private String provinciaNombre;

    public String getDepartamentoId() {
        return departamentoId;
    }

    public void setDepartamentoId(String departamentoId) {
        this.departamentoId = departamentoId;
    }

    public String getDepartamentoNombre() {
        return departamentoNombre;
    }

    public void setDepartamentoNombre(String departamentoNombre) {
        this.departamentoNombre = departamentoNombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocalidadCensalId() {
        return localidadCensalId;
    }

    public void setLocalidadCensalId(String localidadCensalId) {
        this.localidadCensalId = localidadCensalId;
    }

    public String getLocalidadCensalNombre() {
        return localidadCensalNombre;
    }

    public void setLocalidadCensalNombre(String localidadCensalNombre) {
        this.localidadCensalNombre = localidadCensalNombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getProvinciaId() {
        return provinciaId;
    }

    public void setProvinciaId(String provinciaId) {
        this.provinciaId = provinciaId;
    }

    public String getProvinciaNombre() {
        return provinciaNombre;
    }

    public void setProvinciaNombre(String provinciaNombre) {
        this.provinciaNombre = provinciaNombre;
    }

    @Override
    public String toString() {
        return nombre + " - " +departamentoNombre;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Localidad)) {
            return false;
        }

        Localidad l = (Localidad) obj;
        return nombre.equals(l.nombre) && departamentoNombre.equals(l.departamentoNombre);
    }
}
