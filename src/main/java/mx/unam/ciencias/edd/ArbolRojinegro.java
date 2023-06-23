package mx.unam.ciencias.edd;

/**
 * Clase para árboles rojinegros. Un árbol rojinegro cumple las siguientes
 * propiedades:
 *
 * <ol>
 *  <li>Todos los vértices son NEGROS o ROJOS.</li>
 *  <li>La raíz es NEGRA.</li>
 *  <li>Todas las hojas (<code>null</code>) son NEGRAS (al igual que la raíz).</li>
 *  <li>Un vértice ROJO siempre tiene dos hijos NEGROS.</li>
 *  <li>Todo camino de un vértice a alguna de sus hojas descendientes tiene el
 *      mismo número de vértices NEGROS.</li>
 * </ol>
 *
 * Los árboles rojinegros se autobalancean.
 */
public class ArbolRojinegro<T extends Comparable<T>>
    extends ArbolBinarioOrdenado<T> {

    /**
     * Clase interna protegida para vértices.
     */
    protected class VerticeRojinegro extends Vertice {

        /** El color del vértice. */
        public Color color;

        /**
         * Constructor único que recibe un elemento.
         * @param elemento el elemento del vértice.
         */
        public VerticeRojinegro(T elemento) {
            super(elemento);
            this.color = Color.NINGUNO;
        }

        /**
         * Regresa una representación en cadena del vértice rojinegro.
         * @return una representación en cadena del vértice rojinegro.
         */
        @Override public String toString() {
            return (color == Color.ROJO ? "R" : "N")+"{"+super.toString()+"}";
        }

        /**
         * Compara el vértice con otro objeto. La comparación es
         * <em>recursiva</em>.
         * @param objeto el objeto con el cual se comparará el vértice.
         * @return <code>true</code> si el objeto es instancia de la clase
         *         {@link VerticeRojinegro}, su elemento es igual al elemento de
         *         éste vértice, los descendientes de ambos son recursivamente
         *         iguales, y los colores son iguales; <code>false</code> en
         *         otro caso.
         */
        @Override public boolean equals(Object objeto) {
            if (objeto == null || getClass() != objeto.getClass())
                return false;
            @SuppressWarnings("unchecked")
                VerticeRojinegro vertice = (VerticeRojinegro)objeto;
            return subarbolesIguales(this, vertice);   
        }

        /* Revisa si dos subárboles son iguales recursivamente */
        private boolean 
        subarbolesIguales(VerticeRojinegro a, VerticeRojinegro b) {
            if (a == b)
                return true;

            if (a == null || b == null)
                return false;

            if (a.color != b.color || !a.elemento.equals(b.elemento))
                return false;

            boolean igualesIzq = 
                subarbolesIguales(rn(a.izquierdo), rn(b.izquierdo));
            boolean igualesDer = 
                subarbolesIguales(rn(a.derecho), rn(b.derecho));
            return igualesIzq && igualesDer;
        }
    }

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinarioOrdenado}.
     */
    public ArbolRojinegro() { super(); }

    /**
     * Construye un árbol rojinegro a partir de una colección. El árbol
     * rojinegro tiene los mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol
     *        rojinegro.
     */
    public ArbolRojinegro(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Construye un nuevo vértice, usando una instancia de {@link
     * VerticeRojinegro}.
     * @param elemento el elemento dentro del vértice.
     * @return un nuevo vértice rojinegro con el elemento recibido dentro del mismo.
     */
    @Override protected Vertice nuevoVertice(T elemento) {
        return new VerticeRojinegro(elemento);
    }

    /**
     * Regresa el color del vértice rojinegro.
     * @param vertice el vértice del que queremos el color.
     * @return el color del vértice rojinegro.
     * @throws ClassCastException si el vértice no es instancia de {@link
     *         VerticeRojinegro}.
     */
    public Color getColor(VerticeArbolBinario<T> vertice) {
        return rn(vertice).color;
    }


    /* Atajo para audicionar un VerticeArbolBinario como VerticeRojinegro */
    private VerticeRojinegro rn(VerticeArbolBinario<T> v) {
        return (VerticeRojinegro)v;
    }

    /**
     * Agrega un nuevo elemento al árbol. El método invoca al método {@link
     * ArbolBinarioOrdenado#agrega}, y después balancea el árbol recoloreando
     * vértices y girando el árbol como sea necesario.
     * @param elemento el elemento a agregar.
     */
    @Override public void agrega(T elemento) {
        super.agrega(elemento);
        rn(ultimoAgregado).color = Color.ROJO;
        rebalanceaAgregado(rn(ultimoAgregado));
        rn(raiz).color = Color.NEGRO;
    }

    /* Realiza los cinco casos de agregar un vértice rojinegro */
    private void rebalanceaAgregado(VerticeRojinegro v) {
        VerticeRojinegro padre = rn(v.padre);
        if (esNegro(padre))
            return;

        VerticeRojinegro abuelo = rn(padre.padre);
        VerticeRojinegro tio = tio(v);
        if (esRojo(tio)) {
            abuelo.color = Color.ROJO;
            rn(abuelo.izquierdo).color = Color.NEGRO;
            rn(abuelo.derecho).color = Color.NEGRO;
            rebalanceaAgregado(abuelo);
            return;
        }

        if (cruzados(abuelo, padre, v)) {
            if (padre.izquierdo == v)
                super.giraDerecha(padre);
            else
                super.giraIzquierda(padre);
            VerticeRojinegro tmp = v;
            v = padre;
            padre = tmp;
        }

        padre.color = Color.NEGRO;
        abuelo.color = Color.ROJO;
        if (padre.izquierdo == v)
            super.giraDerecha(abuelo);
        else
            super.giraIzquierda(abuelo);
    }

    /* Determina si el padre y el hijo están cruzados. */
    private boolean cruzados(Vertice abuelo, Vertice padre, Vertice hijo) {
        if (abuelo.izquierdo == padre && padre.derecho == hijo)
            return true;
        if (abuelo.derecho == padre && padre.izquierdo == hijo)
            return true;
        return false;
    }

    /* Nos da al vértice tío de v. Supone que el padre no es nulo. */
    private VerticeRojinegro tio(VerticeRojinegro v) {
        VerticeRojinegro padre = rn(v.padre);
        VerticeRojinegro abuelo = rn(padre.padre);
        return rn(abuelo.izquierdo == padre ? abuelo.derecho : abuelo.izquierdo);
    }

    /* Determina si un vértice es rojo. */
    private boolean esRojo(VerticeRojinegro v) {
        return v != null && v.color == Color.ROJO;
    }

    /* Determina si un vértice es negro. */
    private boolean esNegro(VerticeRojinegro v) {
        return v == null || v.color == Color.NEGRO;
    }

    /**
     * Elimina un elemento del árbol. El método elimina el vértice que contiene
     * el elemento, y recolorea y gira el árbol como sea necesario para
     * rebalancearlo.
     * @param elemento el elemento a eliminar del árbol.
     */
    @Override public void elimina(T elemento) {
        Vertice aEliminar = (Vertice)busca(elemento);
        if (aEliminar == null)
            return;

        elementos--;

        if (aEliminar.izquierdo != null && aEliminar.derecho != null)
            aEliminar = intercambiaEliminable((Vertice)aEliminar);

        VerticeRojinegro gaspar = null; 

        if (aEliminar.izquierdo == null && aEliminar.derecho == null) {
            gaspar = new VerticeRojinegro(null);
            aEliminar.izquierdo = gaspar;
            gaspar.padre = aEliminar;
            gaspar.color = Color.NEGRO;
        }

        VerticeRojinegro hijo = rn(aEliminar.izquierdo != null ? 
                                    aEliminar.izquierdo : aEliminar.derecho);

        eliminaVertice(aEliminar);

        if (esRojo(hijo))
            hijo.color = Color.NEGRO;
        else
            if (rn(aEliminar).color == Color.NEGRO)
                rebalanceaEliminado(hijo);

        if (gaspar != null)
            eliminaVertice(gaspar);

        if (elementos == 0)
            raiz = null;
    }

    /* Rebalancea tras eliminar sobre el hijo único negro del eliminado. */
    private void rebalanceaEliminado(VerticeRojinegro v) {
        if (v.padre == null)
            return;

        VerticeRojinegro padre = rn(v.padre);
        VerticeRojinegro hermano = hermano(v);
        if (esRojo(hermano)) {
            padre.color = Color.ROJO;
            hermano.color = Color.NEGRO;
            if (padre.izquierdo == v)
                super.giraIzquierda(padre);
            else
                super.giraDerecha(padre);
            hermano = hermano(v);
        }

        VerticeRojinegro sobIzq = rn(hermano.izquierdo);
        VerticeRojinegro sobDer = rn(hermano.derecho);
        boolean negros = esNegro(hermano) && esNegro(sobIzq) && esNegro(sobDer);
        if (esNegro(padre) && negros) {
            hermano.color = Color.ROJO;
            rebalanceaEliminado(padre);
            return;
        }

        if (negros) {
            hermano.color = Color.ROJO;
            padre.color = Color.NEGRO;
            return;
        }

        if (padre.izquierdo == v && esRojo(sobIzq)) {
            hermano.color = Color.ROJO;
            sobIzq.color = Color.NEGRO;
            super.giraDerecha(hermano);
            hermano = hermano(v);
            sobDer = rn(hermano.derecho);
        } else if (padre.derecho == v && esRojo(sobDer)) {
            hermano.color = Color.ROJO;
            sobDer.color = Color.NEGRO;
            super.giraIzquierda(hermano);
            hermano = hermano(v);
            sobIzq = rn(hermano.izquierdo);
        }

        hermano.color = padre.color;
        padre.color = Color.NEGRO;
        if (padre.izquierdo == v) {
            if (sobDer != null)
                sobDer.color = Color.NEGRO;
            super.giraIzquierda(padre);
            return;
        }
        sobIzq.color = Color.NEGRO;
        super.giraDerecha(padre);
    }

    /* Nos da el hermano de un vértice, suponiendo que el padre no es nulo. */
    private VerticeRojinegro hermano(VerticeRojinegro v) {
        if (v.padre.izquierdo == v)
            return rn(v.padre.derecho);
        return rn(v.padre.izquierdo);
    }
    
    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles
     * rojinegros no pueden ser girados a la izquierda por los usuarios de la
     * clase, porque se desbalancean.
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override public void giraIzquierda(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException("Los árboles rojinegros no " +
                                                "pueden girar a la izquierda " +
                                                "por el usuario.");
    }

    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles
     * rojinegros no pueden ser girados a la derecha por los usuarios de la
     * clase, porque se desbalancean.
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override public void giraDerecha(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException("Los árboles rojinegros no " +
                                                "pueden girar a la derecha " +
                                                "por el usuario.");
    }
}
