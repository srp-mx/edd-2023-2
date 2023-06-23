package mx.unam.ciencias.edd;

import java.lang.StringBuilder;

/**
 * Clase para colas genéricas.
 */
public class Cola<T> extends MeteSaca<T> {

    /**
     * Regresa una representación en cadena de la cola.
     * @return una representación en cadena de la cola.
     */
    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Nodo n = cabeza; n != null; n = n.siguiente) {
            sb.append(n.elemento.toString());
            sb.append(",");
        }
        return sb.toString();
    }

    /**
     * Agrega un elemento al final de la cola.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    @Override public void mete(T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException("No se puede meter null a la cola.");

        Nodo n = new Nodo(elemento);
        if (cabeza == null) {
            rabo = cabeza = n;
            return;
        }
        rabo.siguiente = n;
        rabo = n;
    }
}
