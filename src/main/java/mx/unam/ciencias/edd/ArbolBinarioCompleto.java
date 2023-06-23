package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>Clase para árboles binarios completos.</p>
 *
 * <p>Un árbol binario completo agrega y elimina elementos de tal forma que el
 * árbol siempre es lo más cercano posible a estar lleno.</p>
 */
public class ArbolBinarioCompleto<T> extends ArbolBinario<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Cola para recorrer los vértices en BFS. */
        private Cola<Vertice> cola;

        /* Inicializa al iterador. */
        private Iterador() {
            cola = new Cola<Vertice>();
            if (raiz != null)
                cola.mete(raiz);
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override public boolean hasNext() {
            return !cola.esVacia();
        }

        /* Regresa el siguiente elemento en orden BFS. */
        @Override public T next() {
            if (!hasNext())
                throw new NoSuchElementException("Se nos acabó el árbol.");
            return siguienteCola(cola).elemento;
        }
    }

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinario}.
     */
    public ArbolBinarioCompleto() { super(); }

    /**
     * Construye un árbol binario completo a partir de una colección. El árbol
     * binario completo tiene los mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol
     *        binario completo.
     */
    public ArbolBinarioCompleto(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Agrega un elemento al árbol binario completo. El nuevo elemento se coloca
     * a la derecha del último nivel, o a la izquierda de un nuevo nivel.
     * @param elemento el elemento a agregar al árbol.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    @Override public void agrega(T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException("No se puede agregar null al árbol.");

        Vertice agregado = nuevoVertice(elemento);
        elementos++;

        if (raiz == null) {
            raiz = agregado;
            return;
        }

        Vertice padreAgregado = get(elementos >>> 1);

        if (padreAgregado.izquierdo != null)
            padreAgregado.derecho = agregado;
        else
            padreAgregado.izquierdo = agregado;

        agregado.padre = padreAgregado;
    }

    /* Obtiene un vértice en orden de BFS. Primer elemento tiene indice 1. */
    private Vertice get(int indice) {
        Vertice v = raiz;
        for (int i = pisoLg(indice) - 1; i >= 0; i--) {
            if (((indice >>> i) & 1) == 1) {
                v = v.derecho;
            } else {
                v = v.izquierdo;
            }
        }
        return v;
    }

    /**
     * Elimina un elemento del árbol. El elemento a eliminar cambia lugares con
     * el último elemento del árbol al recorrerlo por BFS, y entonces es
     * eliminado.
     * @param elemento el elemento a eliminar.
     */
    @Override public void elimina(T elemento) {
        Vertice aEliminar = (Vertice)busca(elemento);
        if (aEliminar == null)
            return;

        Vertice ultimo = get(elementos--);
        aEliminar.elemento = ultimo.elemento;
        muevePadre(ultimo, null);
    }

    /* Reemplaza a un vértice como el hijo de su padre. Supone hijo no nulo. */
    private void muevePadre(Vertice hijo, Vertice reemplazo) {
        if (reemplazo != null)
            reemplazo.padre = hijo.padre;

        if (hijo.padre == null) {
            raiz = reemplazo;
            return;
        }

        if (hijo.padre.izquierdo == hijo)
            hijo.padre.izquierdo = reemplazo;
        else
            hijo.padre.derecho = reemplazo;
    }

    /**
     * Regresa la altura del árbol. La altura de un árbol binario completo
     * siempre es ⌊log<sub>2</sub><em>n</em>⌋.
     * @return la altura del árbol.
     */
    @Override public int altura() {
        return pisoLg(elementos);
    }

    /* Calcula el piso de lg(n), suponiendo n > 0 */
    private int pisoLg(int n) {
        int r = -1;
        while (n != 0) {
            n >>>= 1;
            r++;
        }
        return r;
    }

    /**
     * Realiza un recorrido BFS en el árbol, ejecutando la acción recibida en
     * cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void bfs(AccionVerticeArbolBinario<T> accion) {
        if (raiz == null)
            return;

        Cola<Vertice> cola = new Cola<Vertice>();
        cola.mete(raiz);
        while (!cola.esVacia())
            accion.actua(siguienteCola(cola));
    }

    /* Da el siguiente elemento de la cola y la avanza. Agrega si necesita. */
    private Vertice siguienteCola(Cola<Vertice> cola) {
        Vertice v = cola.saca();
        if (v.izquierdo != null) 
            cola.mete(v.izquierdo);
        if (v.derecho != null) 
            cola.mete(v.derecho);
        return v;
    }

    /**
     * Regresa un iterador para iterar el árbol. El árbol se itera en orden BFS.
     * @return un iterador para iterar el árbol.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }
}
