package mx.unam.ciencias.edd;

import java.lang.StringBuilder;

/**
 * Clase para pilas genéricas.
 */
public class Pila<T> extends MeteSaca<T> {

    /**
     * Regresa una representación en cadena de la pila.
     * @return una representación en cadena de la pila.
     */
    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Nodo n = cabeza; n != null; n = n.siguiente) {
            sb.append(n.elemento.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Agrega un elemento al tope de la pila.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    @Override public void mete(T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException("No se puede meter null a la pila.");

        Nodo n = new Nodo(elemento);
        n.siguiente = cabeza;
        cabeza = n;
        if (rabo == null)
            rabo = cabeza;
    }
}
