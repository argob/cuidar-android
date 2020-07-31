package ar.gob.coronavirus.data.remoto.modelo_autodiagnostico;

public class SintomasRemoto {
	private String id;
	private String descripcion;
	private boolean valor;

	public SintomasRemoto(String id, String descripcion, boolean valor) {
		this.id = id;
		this.descripcion = descripcion;
		this.valor = valor;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public boolean isValor() {
		return valor;
	}

	public void setValor(boolean valor) {
		this.valor = valor;
	}
}
