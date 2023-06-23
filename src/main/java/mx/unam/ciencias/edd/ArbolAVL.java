package mx.unam.ciencias.edd;

/**
 * <p>Clase para árboles AVL.</p>
 *
 * <p>Un árbol AVL cumple que para cada uno de sus vértices, la diferencia entre
 * la áltura de sus subárboles izquierdo y derecho está entre -1 y 1.</p>
 */
public class ArbolAVL<T extends Comparable<T>>
    extends ArbolBinarioOrdenado<T> {

    /**
     * Clase interna protegida para vértices.
     */
    protected class VerticeAVL extends Vertice {

        /** La altura del vértice. */
        public int altura;

        /**
         * Constructor único que recibe un elemento.
         * @param elemento el elemento del vértice.
         */
        public VerticeAVL(T elemento) {
            super(elemento);
        }

        /**
         * Regresa la altura del vértice.
         * @return la altura del vértice.
         */
        @Override public int altura() {
            return altura;
        }

        /**
         * Regresa una representación en cadena del vértice AVL.
         * @return una representación en cadena del vértice AVL.
         */
        @Override public String toString() {
            return elemento.toString() + " " + altura + "/" + balance(this);
        }

        /**
         * Compara el vértice con otro objeto. La comparación es
         * <em>recursiva</em>.
         * @param objeto el objeto con el cual se comparará el vértice.
         * @return <code>true</code> si el objeto es instancia de la clase
         *         {@link VerticeAVL}, su elemento es igual al elemento de éste
         *         vértice, los descendientes de ambos son recursivamente
         *         iguales, y las alturas son iguales; <code>false</code> en
         *         otro caso.
         */
        @Override public boolean equals(Object objeto) {
            if (objeto == null || getClass() != objeto.getClass())
                return false;
            @SuppressWarnings("unchecked") VerticeAVL vertice = (VerticeAVL)objeto;
            return subarbolesIguales(this, vertice);
        }

        /* Revisa si dos subárboles son iguales recursivamente */
        private boolean 
        subarbolesIguales(VerticeAVL a, VerticeAVL b) {
            if (a == b)
                return true;

            if (a == null || b == null)
                return false;

            if (a.altura != b.altura || !a.elemento.equals(b.elemento))
                return false;

            boolean igualesIzq = 
                subarbolesIguales(avl(a.izquierdo), avl(b.izquierdo));
            boolean igualesDer = 
                subarbolesIguales(avl(a.derecho), avl(b.derecho));
            return igualesIzq && igualesDer;
        }
    }

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinarioOrdenado}.
     */
    public ArbolAVL() { super(); }

    /**
     * Construye un árbol AVL a partir de una colección. El árbol AVL tiene los
     * mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol AVL.
     */
    public ArbolAVL(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Construye un nuevo vértice, usando una instancia de {@link VerticeAVL}.
     * @param elemento el elemento dentro del vértice.
     * @return un nuevo vértice con el elemento recibido dentro del mismo.
     */
    @Override protected Vertice nuevoVertice(T elemento) {
        return new VerticeAVL(elemento);
    }

    /* Atajo para audicionar un VerticeArbolBinario como VerticeAVL */
    private VerticeAVL avl(VerticeArbolBinario<T> v) {
        return (VerticeAVL)v;
    }

    /* Calcula el balance de algún vértice */
    private int balance(Vertice v) {
        return altura(v.izquierdo) - altura(v.derecho);
    }

    /* Calcula la altura de algún vértice */
    private int altura(Vertice v) {
        if (v == null)
            return -1;
        return v.altura();
    }

    /**
     * Agrega un nuevo elemento al árbol. El método invoca al método {@link
     * ArbolBinarioOrdenado#agrega}, y después balancea el árbol girándolo como
     * sea necesario.
     * @param elemento el elemento a agregar.
     */
    @Override public void agrega(T elemento) {
        super.agrega(elemento);
        rebalancea(avl(ultimoAgregado.padre));
    }

    /**
     * Elimina un elemento del árbol. El método elimina el vértice que contiene
     * el elemento, y gira el árbol como sea necesario para rebalancearlo.
     * @param elemento el elemento a eliminar del árbol.
     */
    @Override public void elimina(T elemento) {
        Vertice aEliminar = (Vertice)busca(elemento);
        if (aEliminar == null)
            return;

        if (aEliminar.izquierdo != null && aEliminar.derecho != null)
            aEliminar = intercambiaEliminable(aEliminar);

        Vertice padre = aEliminar.padre;
        eliminaVertice(aEliminar);
        elementos--;

        rebalancea(avl(padre));
    }

    /* Algoritmo de rebalanceo AVL */
    private void rebalancea(VerticeAVL v) {
        if (v == null)
            return;

        actualizaAltura(v);
        int balance = balance(v);
        
        if (balance == -2)
            rebalanceaGrandeDer(v);
        else if (balance == 2)
            rebalanceaGrandeIzq(v);

        rebalancea(avl(v.padre));
    }

    /* Rebalanceo si el balance es -2 */
    private void rebalanceaGrandeDer(VerticeAVL v) {
        VerticeAVL subarbol = avl(v.derecho);
        if (balance(subarbol) == 1)
            giraDerecha(subarbol);
        giraIzquierda(v);
    }

    /* Rebalanceo si el balance es 2 */
    private void rebalanceaGrandeIzq(VerticeAVL v) {
        VerticeAVL subarbol = avl(v.izquierdo);
        if (balance(subarbol) == -1)
            giraIzquierda(subarbol);
        giraDerecha(v);
    }

    /* Gira VerticeAVL a la derecha y maneja las actualizaciones de altura */
    private void giraDerecha(VerticeAVL v) {
        super.giraDerecha(v);
        actualizaAltura(v);
        actualizaAltura(avl(v.padre));
    }

    /* Gira VerticeAVL a la izquierda y maneja las actualizaciones de altura */
    private void giraIzquierda(VerticeAVL v) {
        super.giraIzquierda(v);
        actualizaAltura(v);
        actualizaAltura(avl(v.padre));
    }

    /* Recalcula y actualiza la altura de un vértice. */
    private void actualizaAltura(VerticeAVL v) {
        v.altura = max(altura(v.izquierdo), altura(v.derecho)) + 1;
    }

    /* Encuentra el mínimo entre dos números */
    private int max(int a, int b) {
        return a > b ? a : b;
    }

    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles AVL
     * no pueden ser girados a la derecha por los usuarios de la clase, porque
     * se desbalancean.
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override public void giraDerecha(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException("Los árboles AVL no  pueden " +
                                                "girar a la izquierda por el " +
                                                "usuario.");
    }

    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles AVL
     * no pueden ser girados a la izquierda por los usuarios de la clase, porque
     * se desbalancean.
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override public void giraIzquierda(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException("Los árboles AVL no  pueden " +
                                                "girar a la derecha por el " +
                                                "usuario.");
    }
}
