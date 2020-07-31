package ar.gob.coronavirus.data;

public class DniEntidad {
    private String id;
    private String nombre;
    private String apellido;
    private String tramite;
    private String sexo;
    private String ejemplar;
    private String fechaDeNacimiento;
    private String fechaDeEmision;

    public DniEntidad(String id, String nombre, String apellido, String tramite, String sexo, String ejemplar, String fechaDeNacimiento, String fechaDeEmision) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.tramite = tramite;
        this.sexo = sexo;
        this.ejemplar = ejemplar;
        this.fechaDeNacimiento = fechaDeNacimiento;
        this.fechaDeEmision = fechaDeEmision;
    }

    public DniEntidad() {
        this.id = "";
        this.nombre = "";
        this.apellido = "";
        this.tramite = "";
        this.sexo = "";
        this.ejemplar = "";
        this.fechaDeNacimiento = "";
        this.fechaDeEmision = "";
    }

    public DniEntidad construirDni(String valorQrDniEscaneado) {
        if(valorQrDniEscaneado != null){
            String[] attr = valorQrDniEscaneado.split("@");
            if (!valorQrDniEscaneado.startsWith("@") && attr.length >= 7) {
                String tramite = attr[0] != null ? attr[0] : "";
                String apellido = attr[1] != null ? attr[1] : "";
                String nombre = attr[2] != null ? attr[2] : "";
                String sexo = attr[3] != null ? attr[3] : "";
                String id = attr[4] != null ? attr[4] : "";
                id = id.replaceAll("[a-zA-Z]", "");
                String ejemplar = attr[5] != null ? attr[5] : "";
                String fechaDeNacimiento = attr[6] != null ? attr[6] : "";
                String fechaDeEmision = attr[7] != null ? attr[7] : "";
                return new DniEntidad(id, nombre, apellido, tramite, sexo, ejemplar, fechaDeNacimiento, fechaDeEmision);
            }
        }
        return new DniEntidad();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTramite() {
        return tramite;
    }

    public void setTramite(String tramite) {
        this.tramite = tramite;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getEjemplar() {
        return ejemplar;
    }

    public void setEjemplar(String ejemplar) {
        this.ejemplar = ejemplar;
    }

    public String getFechaDeNacimiento() {
        return fechaDeNacimiento;
    }

    public void setFechaDeNacimiento(String fechaDeNacimiento) {
        this.fechaDeNacimiento = fechaDeNacimiento;
    }

    public String getFechaDeEmision() {
        return fechaDeEmision;
    }

    public void setFechaDeEmision(String fechaDeEmision) {
        this.fechaDeEmision = fechaDeEmision;
    }

    @Override
    public String toString() {
        return "DniEntidad{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", tramite='" + tramite + '\'' +
                ", sexo='" + sexo + '\'' +
                ", ejemplar='" + ejemplar + '\'' +
                ", fechaDeNacimiento='" + fechaDeNacimiento + '\'' +
                ", fechaDeEmision='" + fechaDeEmision + '\'' +
                '}';
    }

    public boolean tieneDatosBasicosCompletos() {
        return (id != null && !id.isEmpty()) &&
                (tramite != null && !tramite.isEmpty()) &&
                (sexo != null && !sexo.isEmpty()) ;
    }
}
