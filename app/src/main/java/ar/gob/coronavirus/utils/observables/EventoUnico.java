package ar.gob.coronavirus.utils.observables;

public class EventoUnico<T> {
    private T contenido = null;
    private Boolean yaFueLanzado = false;

    public T obtenerContenidoSiNoFueLanzado() {
        if (yaFueLanzado) {
            return null;
        } else {
            yaFueLanzado = true;
            return contenido;
        }
    }

    public EventoUnico(T contenido) {
        this.contenido = contenido;
    }

    public T obtenerConenido() {
        return contenido;
    }
}
