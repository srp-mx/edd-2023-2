package mx.unam.ciencias.edd;

/**
 * Interfaz para computar valores y guardar los resultados en cachés.
 */
@FunctionalInterface
public interface AccionCache<T> {

    /**
     * Computa un valor.
     * @return el valor a regresar y guardar en la caché.
     */
    public T computa();
}

