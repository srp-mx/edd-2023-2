package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>Clase para árboles binarios ordenados. Los árboles son genéricos, pero
 * acotados a la interfaz {@link Comparable}.</p>
 *
 * <p>Un árbol instancia de esta clase siempre cumple que:</p>
 * <ul>
 *   <li>Cualquier elemento en el árbol es mayor o igual que todos sus
 *       descendientes por la izquierda.</li>
 *   <li>Cualquier elemento en el árbol es menor o igual que todos sus
 *       descendientes por la derecha.</li>
 * </ul>
 */
public class ArbolBinarioOrdenado<T extends Comparable<T>>
    extends ArbolBinario<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Pila para recorrer los vértices en DFS in-order. */
        private Pila<Vertice> pila;

        /* Inicializa al iterador. */
        private Iterador() {
            pila = new Pila<Vertice>();
            meteIzquierdos(raiz, pila);
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override public boolean hasNext() {
            return !pila.esVacia();
        }

        /* Regresa el siguiente elemento en orden DFS in-order. */
        @Override public T next() {
            if (!hasNext())
                throw new NoSuchElementException("Se nos acabó el árbol.");
            return siguientePila(pila).elemento;
        }
    }

    /**
     * El vértice del último elemento agegado. Este vértice sólo se puede
     * garantizar que existe <em>inmediatamente</em> después de haber agregado
     * un elemento al árbol. Si cualquier operación distinta a agregar sobre el
     * árbol se ejecuta después de haber agregado un elemento, el estado de esta
     * variable es indefinido.
     */
    protected Vertice ultimoAgregado;

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinario}.
     */
    public ArbolBinarioOrdenado() { super(); }

    /**
     * Construye un árbol binario ordenado a partir de una colección. El árbol
     * binario ordenado tiene los mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol
     *        binario ordenado.
     */
    public ArbolBinarioOrdenado(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Agrega un nuevo elemento al árbol. El árbol conserva su orden in-order.
     * @param elemento el elemento a agregar.
     */
    @Override public void agrega(T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException("No se puede agregar null al árbol.");

        elementos++;
        if (raiz == null) {
            raiz = ultimoAgregado = nuevoVertice(elemento);
            return;
        }

        auxiliarAgrega(raiz, elemento);
    }

    /* Método auxiliar para hacer recursión al agregar */
    private void auxiliarAgrega(Vertice padreAgregado, T elemento) { 
        if (elemento.compareTo(padreAgregado.elemento) <= 0)
            if (padreAgregado.izquierdo == null)
                agregaVerticeIzq(padreAgregado, nuevoVertice(elemento));
            else
                auxiliarAgrega(padreAgregado.izquierdo, elemento);
        else
            if (padreAgregado.derecho == null)
                agregaVerticeDer(padreAgregado, nuevoVertice(elemento));
            else
                auxiliarAgrega(padreAgregado.derecho, elemento);
    }

    /* Hace v hijo izquierdo de padre preservando el subárbol de padre bajo v */
    private void agregaVerticeIzq(Vertice padre, Vertice v) {
        if (padre.izquierdo != null) {
            v.izquierdo = padre.izquierdo;
            padre.izquierdo.padre = v;
        }
        v.padre = padre;
        padre.izquierdo = v;
        ultimoAgregado = v;
    }

    /* Hace v hijo derecho de padre preservando el subárbol de padre bajo v */
    private void agregaVerticeDer(Vertice padre, Vertice v) {
        if (padre.derecho != null) {
            v.derecho = padre.derecho;
            padre.derecho.padre = v;
        }
        v.padre = padre;
        padre.derecho = v;
        ultimoAgregado = v;
    }

    /* Da el siguiente elemento de la pila y la avanza. Agrega si necesita. */
    private Vertice siguientePila(Pila<Vertice> pila) {
        Vertice v = pila.saca();
        if (v.derecho != null)
            meteIzquierdos(v.derecho, pila);
        return v;
    }
    
    /* Mete al vértice a la pila y todo elemento a su izquierda (si puede) */
    private void meteIzquierdos(Vertice v, Pila<Vertice> pila) {
        while (v != null) {
            pila.mete(v);
            v = v.izquierdo;
        }
    }

    /**
     * Elimina un elemento. Si el elemento no está en el árbol, no hace nada; si
     * está varias veces, elimina el primero que encuentre (in-order). El árbol
     * conserva su orden in-order.
     * @param elemento el elemento a eliminar.
     */
    @Override public void elimina(T elemento) {
        Vertice aEliminar = (Vertice)busca(elemento);
        if (aEliminar == null)
            return;

        if (aEliminar.izquierdo != null && aEliminar.derecho != null)
            aEliminar = intercambiaEliminable(aEliminar);

        eliminaVertice(aEliminar);
        elementos--;
    }

    /**
     * Intercambia el elemento de un vértice con dos hijos distintos de
     * <code>null</code> con el elemento de un descendiente que tenga a lo más
     * un hijo.
     * @param vertice un vértice con dos hijos distintos de <code>null</code>.
     * @return el vértice descendiente con el que vértice recibido se
     *         intercambió. El vértice regresado tiene a lo más un hijo distinto
     *         de <code>null</code>.
     */
    protected Vertice intercambiaEliminable(Vertice vertice) {
        Vertice infimo = infimo(vertice);
        vertice.elemento = infimo.elemento;
        return infimo;
    }

    /* Obtiene el vértice máximo de los vértices a la izquierda de otro */
    private Vertice infimo(Vertice v) {
        Vertice maximo = v.izquierdo;
        if (maximo == null)
            return null;

        while (maximo.derecho != null)
            maximo = maximo.derecho;

        return maximo;
    }

    /**
     * Elimina un vértice que a lo más tiene un hijo distinto de
     * <code>null</code> subiendo ese hijo (si existe).
     * @param vertice el vértice a eliminar; debe tener a lo más un hijo
     *                distinto de <code>null</code>.
     */
    protected void eliminaVertice(Vertice vertice) {
        Vertice hijo = vertice.izquierdo;
        if (hijo == null)
            hijo = vertice.derecho;
        cambiaHijo(vertice, hijo);
    }

    /* Reemplaza a un vértice como el hijo de su padre. Supone hijo no nulo. */
    private void cambiaHijo(Vertice hijo, Vertice reemplazo) {
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

    /* Reemplaza el hijo izquierdo del padre. */
    private void cambiaHijoIzq(Vertice padre, Vertice reemplazo) {
        if (reemplazo != null)
            reemplazo.padre = padre;

        if (padre == null)
            raiz = reemplazo;
        else
            padre.izquierdo = reemplazo;
    }

    /* Reemplaza el hijo derecho del padre. */
    private void cambiaHijoDer(Vertice padre, Vertice reemplazo) {
        if (reemplazo != null)
            reemplazo.padre = padre;

        if (padre == null)
            raiz = reemplazo;
        else
            padre.derecho = reemplazo;
    }

    /**
     * Busca un elemento en el árbol recorriéndolo in-order. Si lo encuentra,
     * regresa el vértice que lo contiene; si no, regresa <code>null</code>.
     * @param elemento el elemento a buscar.
     * @return un vértice que contiene al elemento buscado si lo
     *         encuentra; <code>null</code> en otro caso.
     */
    @Override public VerticeArbolBinario<T> busca(T elemento) {
        Vertice v = raiz;
        int comparacion;
        while (v != null) {
            comparacion = elemento.compareTo(v.elemento);
            if (comparacion == 0)
                return v;
            if (comparacion < 0)
                v = v.izquierdo;
            else
                v = v.derecho;
        }
        return v;
    }

    /**
     * Regresa el vértice que contiene el último elemento agregado al
     * árbol. Este método sólo se puede garantizar que funcione
     * <em>inmediatamente</em> después de haber invocado al método {@link
     * agrega}. Si cualquier operación distinta a agregar sobre el árbol se
     * ejecuta después de haber agregado un elemento, el comportamiento de este
     * método es indefinido.
     * @return el vértice que contiene el último elemento agregado al árbol, si
     *         el método es invocado inmediatamente después de agregar un
     *         elemento al árbol.
     */
    public VerticeArbolBinario<T> getUltimoVerticeAgregado() {
        return ultimoAgregado;
    }

    /**
     * Gira el árbol a la derecha sobre el vértice recibido. Si el vértice no
     * tiene hijo izquierdo, el método no hace nada.
     * @param vertice el vértice sobre el que vamos a girar.
     */
    public void giraDerecha(VerticeArbolBinario<T> vertice) {
        if (!vertice.hayIzquierdo())
            return;

        Vertice a = (Vertice)vertice;
        Vertice b = a.izquierdo;
        cambiaHijoIzq(a, b.derecho);
        cambiaHijo(a, b);
        cambiaHijoDer(b, a);
    }

    /**
     * Gira el árbol a la izquierda sobre el vértice recibido. Si el vértice no
     * tiene hijo derecho, el método no hace nada.
     * @param vertice el vértice sobre el que vamos a girar.
     */
    public void giraIzquierda(VerticeArbolBinario<T> vertice) {
        if (!vertice.hayDerecho())
            return;

        Vertice b = (Vertice)vertice;
        Vertice a = b.derecho;
        cambiaHijoDer(b, a.izquierdo);
        cambiaHijo(b, a);
        cambiaHijoIzq(a, b);
    }

    /**
     * Realiza un recorrido DFS <em>pre-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsPreOrder(AccionVerticeArbolBinario<T> accion) {
        auxiliarDfsPreOrder(raiz, accion);
    }

    /* Método auxiliar para hacer DFS pre-order recursivo */
    private void 
    auxiliarDfsPreOrder(Vertice v, AccionVerticeArbolBinario<T> accion) {
        if (v == null)
            return;

        accion.actua(v);
        auxiliarDfsPreOrder(v.izquierdo, accion);
        auxiliarDfsPreOrder(v.derecho, accion);
    }

    /**
     * Realiza un recorrido DFS <em>in-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsInOrder(AccionVerticeArbolBinario<T> accion) {
        auxiliarDfsInOrder(raiz, accion);
    }

    /* Método auxiliar para hacer DFS in-order recursivo */
    private void 
    auxiliarDfsInOrder(Vertice v, AccionVerticeArbolBinario<T> accion) {
        if (v == null)
            return;

        auxiliarDfsInOrder(v.izquierdo, accion);
        accion.actua(v);
        auxiliarDfsInOrder(v.derecho, accion);
    }

    /**
     * Realiza un recorrido DFS <em>post-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsPostOrder(AccionVerticeArbolBinario<T> accion) {
        auxiliarDfsPostOrder(raiz, accion);
    }

    /* Método auxiliar para hacer DFS post-order recursivo */
    private void 
    auxiliarDfsPostOrder(Vertice v, AccionVerticeArbolBinario<T> accion) {
        if (v == null)
            return;

        auxiliarDfsPostOrder(v.izquierdo, accion);
        auxiliarDfsPostOrder(v.derecho, accion);
        accion.actua(v);
    }

    /**
     * Regresa un iterador para iterar el árbol. El árbol se itera en orden.
     * @return un iterador para iterar el árbol.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }
}
