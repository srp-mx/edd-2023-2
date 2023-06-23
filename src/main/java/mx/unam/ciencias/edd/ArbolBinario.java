package mx.unam.ciencias.edd;

import java.util.NoSuchElementException;
import java.lang.StringBuilder;

/**
 * <p>Clase abstracta para árboles binarios genéricos.</p>
 *
 * <p>La clase proporciona las operaciones básicas para árboles binarios, pero
 * deja la implementación de varias en manos de las subclases concretas.</p>
 */
public abstract class ArbolBinario<T> implements Coleccion<T> {

    /**
     * Clase interna protegida para vértices.
     */
    protected class Vertice implements VerticeArbolBinario<T> {

        /** El elemento del vértice. */
        protected T elemento;
        /** El padre del vértice. */
        protected Vertice padre;
        /** El izquierdo del vértice. */
        protected Vertice izquierdo;
        /** El derecho del vértice. */
        protected Vertice derecho;

        /**
         * Constructor único que recibe un elemento.
         * @param elemento el elemento del vértice.
         */
        protected Vertice(T elemento) {
            this.elemento = elemento;
        }

        /**
         * Nos dice si el vértice tiene un padre.
         * @return <code>true</code> si el vértice tiene padre,
         *         <code>false</code> en otro caso.
         */
        @Override public boolean hayPadre() {
            return padre != null;
        }

        /**
         * Nos dice si el vértice tiene un izquierdo.
         * @return <code>true</code> si el vértice tiene izquierdo,
         *         <code>false</code> en otro caso.
         */
        @Override public boolean hayIzquierdo() {
            return izquierdo != null;
        }

        /**
         * Nos dice si el vértice tiene un derecho.
         * @return <code>true</code> si el vértice tiene derecho,
         *         <code>false</code> en otro caso.
         */
        @Override public boolean hayDerecho() {
            return derecho != null;
        }

        /**
         * Regresa el padre del vértice.
         * @return el padre del vértice.
         * @throws NoSuchElementException si el vértice no tiene padre.
         */
        @Override public VerticeArbolBinario<T> padre() {
            if (!hayPadre())
                throw new NoSuchElementException("El vértice no tiene padre.");
            return padre;
        }

        /**
         * Regresa el izquierdo del vértice.
         * @return el izquierdo del vértice.
         * @throws NoSuchElementException si el vértice no tiene izquierdo.
         */
        @Override public VerticeArbolBinario<T> izquierdo() {
            if (!hayIzquierdo())
                throw new NoSuchElementException("El vértice no tiene hijo izquierdo.");
            return izquierdo;
        }

        /**
         * Regresa el derecho del vértice.
         * @return el derecho del vértice.
         * @throws NoSuchElementException si el vértice no tiene derecho.
         */
        @Override public VerticeArbolBinario<T> derecho() {
            if (!hayDerecho())
                throw new NoSuchElementException("El vértice no tiene hijo derecho.");
            return derecho;
        }

        /**
         * Regresa la altura del vértice.
         * @return la altura del vértice.
         */
        @Override public int altura() {
            return alturaVertice(this);
        }

        /**
         * Regresa la profundidad del vértice.
         * @return la profundidad del vértice.
         */
        @Override public int profundidad() {
            Vertice nodoPadre = padre;
            int profundidad = 0;
            while (nodoPadre != null) {
                profundidad++;
                nodoPadre = nodoPadre.padre;
            }
            return profundidad;
        }

        /**
         * Regresa el elemento al que apunta el vértice.
         * @return el elemento al que apunta el vértice.
         */
        @Override public T get() {
            return elemento;
        }

        /**
         * Compara el vértice con otro objeto. La comparación es
         * <em>recursiva</em>. Las clases que extiendan {@link Vertice} deben
         * sobrecargar el método {@link Vertice#equals}.
         * @param objeto el objeto con el cual se comparará el vértice.
         * @return <code>true</code> si el objeto es instancia de la clase
         *         {@link Vertice}, su elemento es igual al elemento de éste
         *         vértice, y los descendientes de ambos son recursivamente
         *         iguales; <code>false</code> en otro caso.
         */
        @Override public boolean equals(Object objeto) {
            if (objeto == null || getClass() != objeto.getClass())
                return false;
            @SuppressWarnings("unchecked") Vertice vertice = (Vertice)objeto;
            return subarbolesIguales(this, vertice);   
        }

        /* Revisa si dos subárboles son iguales recursivamente */
        private boolean subarbolesIguales(Vertice a, Vertice b) {
            if (a == b)
                return true;

            if (a == null || b == null || !a.elemento.equals(b.elemento))
                return false;

            boolean igualesIzq = subarbolesIguales(a.izquierdo, b.izquierdo);
            boolean igualesDer = subarbolesIguales(a.derecho, b.derecho);
            return igualesIzq && igualesDer;
        }

        /**
         * Regresa una representación en cadena del vértice.
         * @return una representación en cadena del vértice.
         */
        @Override public String toString() {
            return elemento.toString();
        }
    }

    /** La raíz del árbol. */
    protected Vertice raiz;
    /** El número de elementos */
    protected int elementos;

    /**
     * Constructor sin parámetros. Tenemos que definirlo para no perderlo.
     */
    public ArbolBinario() {}

    /**
     * Construye un árbol binario a partir de una colección. El árbol binario
     * tendrá los mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol
     *        binario.
     */
    public ArbolBinario(Coleccion<T> coleccion) {
        for (T e : coleccion)
            agrega(e);
    }

    /**
     * Construye un nuevo vértice, usando una instancia de {@link Vertice}. Para
     * crear vértices se debe utilizar este método en lugar del operador
     * <code>new</code>, para que las clases herederas de ésta puedan
     * sobrecargarlo y permitir que cada estructura de árbol binario utilice
     * distintos tipos de vértices.
     * @param elemento el elemento dentro del vértice.
     * @return un nuevo vértice con el elemento recibido dentro del mismo.
     */
    protected Vertice nuevoVertice(T elemento) {
        return new Vertice(elemento);
    }

    /**
     * Regresa la altura del árbol. La altura de un árbol es la altura de su
     * raíz.
     * @return la altura del árbol.
     */
    public int altura() {
        return alturaVertice(raiz);
    }


    /* Obtiene la altura dado un vértice */
    private int alturaVertice(Vertice v) {
        if (v == null)
            return -1;

        int alturaIzq = alturaVertice(v.izquierdo);
        int alturaDer = alturaVertice(v.derecho);
        return max(alturaIzq, alturaDer) + 1;
    }

    /* Regresa el máximo de dos enteros */
    private int max(int a, int b) {
        return a > b ? a : b;
    }


    /**
     * Regresa el número de elementos que se han agregado al árbol.
     * @return el número de elementos en el árbol.
     */
    @Override public int getElementos() {
        return elementos;
    }

    /**
     * Nos dice si un elemento está en el árbol binario.
     * @param elemento el elemento que queremos comprobar si está en el árbol.
     * @return <code>true</code> si el elemento está en el árbol;
     *         <code>false</code> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
        return busca(elemento) != null;
    }

    /**
     * Busca el vértice de un elemento en el árbol. Si no lo encuentra regresa
     * <code>null</code>.
     * @param elemento el elemento para buscar el vértice.
     * @return un vértice que contiene el elemento buscado si lo encuentra;
     *         <code>null</code> en otro caso.
     */
    public VerticeArbolBinario<T> busca(T elemento) {
        return busca(elemento, raiz);
    }

    /* Busca un elemento en todo el subárbol del vértice. Null si no hay. */
    private Vertice busca(T elemento, Vertice v) {
        if (v == null)
            return null;

        if (v.elemento.equals(elemento))
            return v;

        Vertice busquedaIzq = busca(elemento, v.izquierdo);
        if (busquedaIzq != null)
            return busquedaIzq;

        return busca(elemento, v.derecho);
    }

    /**
     * Regresa el vértice que contiene la raíz del árbol.
     * @return el vértice que contiene la raíz del árbol.
     * @throws NoSuchElementException si el árbol es vacío.
     */
    public VerticeArbolBinario<T> raiz() {
        if (raiz == null)
            throw new NoSuchElementException("El árbol es vacío.");
        return raiz;
    }

    /**
     * Nos dice si el árbol es vacío.
     * @return <code>true</code> si el árbol es vacío, <code>false</code> en
     *         otro caso.
     */
    @Override public boolean esVacia() {
        return raiz == null;
    }

    /**
     * Limpia el árbol de elementos, dejándolo vacío.
     */
    @Override public void limpia() {
        raiz = null;
        elementos = 0;
    }

    /**
     * Compara el árbol con un objeto.
     * @param objeto el objeto con el que queremos comparar el árbol.
     * @return <code>true</code> si el objeto recibido es un árbol binario y los
     *         árboles son iguales; <code>false</code> en otro caso.
     */
    @Override public boolean equals(Object objeto) {
        if (objeto == null || getClass() != objeto.getClass())
            return false;
        @SuppressWarnings("unchecked")
            ArbolBinario<T> arbol = (ArbolBinario<T>)objeto;
        if (raiz == arbol.raiz)
            return true;
        if ((raiz == null) != (arbol.raiz == null))
            return false;
        return raiz.equals(arbol.raiz);
    }

    /**
     * Regresa una representación en cadena del árbol.
     * @return una representación en cadena del árbol.
     */
    @Override public String toString() {
        if (raiz == null)
            return "";
        StringBuilder sb = new StringBuilder();
        boolean[] bufferBarras = new boolean[altura() + 1];
        ponVerticeStr(sb, raiz, 0, bufferBarras);
        return sb.toString();
    }

    /* Dibuja al subárbol del vértice especificado. */
    private void 
    ponVerticeStr(StringBuilder sb, Vertice v, int nivel, boolean[] bufferBarras) {
        sb.append(v);
        sb.append("\n");
        bufferBarras[nivel] = true;

        if (v.izquierdo != null && v.derecho != null)
            ponFlechaStr(sb, v, nivel, 1 & 2, bufferBarras);
        else if(v.izquierdo != null)
            ponFlechaStr(sb, v, nivel, 1, bufferBarras);
        else if (v.derecho != null)
            ponFlechaStr(sb, v, nivel, 2, bufferBarras);
    }

    /* Dibuja el renglón de la flecha y el subárbol que le corresponde.
     *  flechaID:   1   --> izquierda
     *              2   --> derecha
     *              1&2 --> izquierda y derecha
     */
    private void 
    ponFlechaStr(StringBuilder sb, Vertice v, int nivel, int flechaID, boolean[] bufferBarras) {
        ponEspaciosStr(sb, nivel, bufferBarras);

        if (flechaID == (1 & 2))
            sb.append("├─›");
        else if (flechaID == 1)
            sb.append("└─›");
        else
            sb.append("└─»");

        if (flechaID != (1 & 2))
            bufferBarras[nivel] = false;

        Vertice hijoSiguiente = flechaID == 2 ? v.derecho : v.izquierdo;
        ponVerticeStr(sb, hijoSiguiente, nivel+1, bufferBarras);

        if (flechaID == (1 & 2))
            ponFlechaStr(sb, v, nivel, 2, bufferBarras);
    }

    /* Dibuja los espacios y barras que anteceden a la flecha de cada línea.*/
    private void 
    ponEspaciosStr(StringBuilder sb, int nivel, boolean[] bufferBarras) {
        for (int i = 0; i < nivel; i++) {
            if (bufferBarras[i])
                sb.append("│  ");
            else
                sb.append("   ");
        }
    }

    /**
     * Convierte el vértice (visto como instancia de {@link
     * VerticeArbolBinario}) en vértice (visto como instancia de {@link
     * Vertice}). Método auxiliar para hacer esta audición en un único lugar.
     * @param vertice el vértice de árbol binario que queremos como vértice.
     * @return el vértice recibido visto como vértice.
     * @throws ClassCastException si el vértice no es instancia de {@link
     *         Vertice}.
     */
    protected Vertice vertice(VerticeArbolBinario<T> vertice) {
        return (Vertice)vertice;
    }
}
