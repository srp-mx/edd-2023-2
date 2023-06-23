package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para montículos mínimos (<i>min heaps</i>).
 */
public class MonticuloMinimo<T extends ComparableIndexable<T>>
    implements Coleccion<T>, MonticuloDijkstra<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Índice del iterador. */
        private int indice;

        /* Nos dice si hay un siguiente elemento. */
        @Override public boolean hasNext() {
            return indice < elementos;
        }

        /* Regresa el siguiente elemento. */
        @Override public T next() {
            if (!hasNext())
                throw new NoSuchElementException("Se nos acabó el montículo.");
            return arbol[indice++];
        }
    }

    /* Clase estática privada para adaptadores. */
    private static class Adaptador<T  extends Comparable<T>>
        implements ComparableIndexable<Adaptador<T>> {

        /* El elemento. */
        private T elemento;
        /* El índice. */
        private int indice;

        /* Crea un nuevo comparable indexable. */
        public Adaptador(T elemento) {
            this.elemento = elemento;
        }

        /* Regresa el índice. */
        @Override public int getIndice() {
            return indice;
        }

        /* Define el índice. */
        @Override public void setIndice(int indice) {
            this.indice = indice;
        }

        /* Compara un adaptador con otro. */
        @Override public int compareTo(Adaptador<T> adaptador) {
            return elemento.compareTo(adaptador.elemento);
        }
    }

    /* El número de elementos en el arreglo. */
    private int elementos;
    /* Usamos un truco para poder utilizar arreglos genéricos. */
    private T[] arbol;

    /* Truco para crear arreglos genéricos. Es necesario hacerlo así por cómo
       Java implementa sus genéricos; de otra forma obtenemos advertencias del
       compilador. */
    @SuppressWarnings("unchecked") private T[] nuevoArreglo(int n) {
        return (T[])(new ComparableIndexable[n]);
    }

    /**
     * Constructor sin parámetros. Es más eficiente usar {@link
     * #MonticuloMinimo(Coleccion)} o {@link #MonticuloMinimo(Iterable,int)},
     * pero se ofrece este constructor por completez.
     */
    public MonticuloMinimo() {
        arbol = nuevoArreglo(256);
    }

    /**
     * Constructor para montículo mínimo que recibe una colección. Es más barato
     * construir un montículo con todos sus elementos de antemano (tiempo
     * <i>O</i>(<i>n</i>)), que el insertándolos uno por uno (tiempo
     * <i>O</i>(<i>n</i> log <i>n</i>)).
     * @param coleccion la colección a partir de la cuál queremos construir el
     *                  montículo.
     */
    public MonticuloMinimo(Coleccion<T> coleccion) {
        this(coleccion, coleccion.getElementos());
    }

    /**
     * Constructor para montículo mínimo que recibe un iterable y el número de
     * elementos en el mismo. Es más barato construir un montículo con todos sus
     * elementos de antemano (tiempo <i>O</i>(<i>n</i>)), que el insertándolos
     * uno por uno (tiempo <i>O</i>(<i>n</i> log <i>n</i>)).
     * @param iterable el iterable a partir de la cuál queremos construir el
     *                 montículo.
     * @param n el número de elementos en el iterable.
     */
    public MonticuloMinimo(Iterable<T> iterable, int n) {
        arbol = nuevoArreglo(n);
        for (T t : iterable)
            coloca(t, elementos++);
        for (int i = n/2; i >= 0; i--)
            acomodaAbajo(i);
    }

    /* Coloca un elemento en un índice en el arreglo. */
    private void coloca(T elemento, int i) {
        elemento.setIndice(i);
        arbol[i] = elemento;
    }

    /**
     * Agrega un nuevo elemento en el montículo.
     * @param elemento el elemento a agregar en el montículo.
     */
    @Override public void agrega(T elemento) {
        if (elementos == arbol.length)
            crece();
        coloca(elemento, elementos);
        acomodaArriba(elementos++);
    }

    /* Reemplaza el arreglo con otro más grande con los mismos elementos. */
    private void crece() {
        T[] reemplazo = nuevoArreglo(elementos * 2);
        for (int i = 0; i < elementos; i++)
            reemplazo[i] = arbol[i];
        arbol = reemplazo;
    }

    /**
     * Elimina el elemento mínimo del montículo.
     * @return el elemento mínimo del montículo.
     * @throws IllegalStateException si el montículo es vacío.
     */
    @Override public T elimina() {
        if (elementos == 0)
            throw new IllegalStateException("El montículo es vacío.");
        T minimo = arbol[0];
        elimina(0);
        return minimo;
    }

    /* Elimina el i-ésimo elemento. */
    private void elimina(int i) {
        intercambia(i, --elementos);
        eliminaHoja(elementos);
        if (i != elementos)
            acomoda(i);
    }

    /* Intercambia los elementos de dos índices. */
    private void intercambia(int i, int j) {
        T tmp = arbol[i];
        coloca(arbol[j], i);
        coloca(tmp, j);
    }

    /* Elimina una hoja. */
    private void eliminaHoja(int i) {
        arbol[i].setIndice(-1);
        arbol[i] = null;
    }

    /* Acomoda hacia arriba o abajo según sea necesario. */
    private void acomoda(int i) {
        if (acomodaArriba(i))
            return;
        if (acomodaAbajo(i))
            return;
    }

    /* Acomoda hacia arriba para asegurar un montículo mínimo. */
    private boolean acomodaArriba(int i) {
        int padre = padre(i);
        if (padre < 0 || !esMenor(i, padre))
            return false;
        intercambia(i, padre);
        acomodaArriba(padre);
        return true;
    }

    /* Acomoda hacia abajo para asegurar un montículo mínimo. */
    private boolean acomodaAbajo(int i) {
        int hijoMin = hijoMin(i);
        if (hijoMin >= elementos || !esMenor(hijoMin, i))
            return false;
        intercambia(i, hijoMin);
        acomodaAbajo(hijoMin);
        return true;
    }

    /* Regresa el índice del hijo más pequeño de un vértice con índice i. */
    private int hijoMin(int i) {
        int izq = izq(i);
        int der = der(i);
        if (der >= elementos) return izq;
        return arbol[izq].compareTo(arbol[der]) < 0 ? izq : der;
    }

    /* Regresa el índice tentativo del hijo izquierdo del vértice en i. */
    private int izq(int i) {
        return 2*i + 1;
    }

    /* Regresa el índice tentativo del hijo derecho del vértice en i. */
    private int der(int i) {
        return 2*i + 2;
    }

    /* Regresa el índice tentativo del padre de un vértice con índice i. */
    private int padre(int i) {
        return i == 0 ? -1 : (i-1)/2;
    }

    /* Indica si el elemento del árbol en i es menor que el de j. */
    private boolean esMenor(int i, int j) {
        return arbol[i].compareTo(arbol[j]) < 0;
    }

    /**
     * Elimina un elemento del montículo.
     * @param elemento a eliminar del montículo.
     */
    @Override public void elimina(T elemento) {
        if (elemento.getIndice() >= 0)
            elimina(elemento.getIndice());
    }

    /**
     * Nos dice si un elemento está contenido en el montículo.
     * @param elemento el elemento que queremos saber si está contenido.
     * @return <code>true</code> si el elemento está contenido,
     *         <code>false</code> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
        if (elemento == null) return false;
        int i = elemento.getIndice();
        return i >= 0 && i < elementos && arbol[i].equals(elemento);
    }

    /**
     * Nos dice si el montículo es vacío.
     * @return <code>true</code> si ya no hay elementos en el montículo,
     *         <code>false</code> en otro caso.
     */
    @Override public boolean esVacia() {
        return elementos == 0;
    }

    /**
     * Limpia el montículo de elementos, dejándolo vacío.
     */
    @Override public void limpia() {
        elementos = 0;
        for (int i = 0; i < elementos; i++) {
            arbol[i].setIndice(-1);
            arbol[i] = null;
        }
    }

   /**
     * Reordena un elemento en el árbol.
     * @param elemento el elemento que hay que reordenar.
     */
    @Override public void reordena(T elemento) {
        acomoda(elemento.getIndice());
    }

    /**
     * Regresa el número de elementos en el montículo mínimo.
     * @return el número de elementos en el montículo mínimo.
     */
    @Override public int getElementos() {
        return elementos;
    }

    /**
     * Regresa el <i>i</i>-ésimo elemento del árbol, por niveles.
     * @param i el índice del elemento que queremos, en <em>in-order</em>.
     * @return el <i>i</i>-ésimo elemento del árbol, por niveles.
     * @throws NoSuchElementException si i es menor que cero, o mayor o igual
     *         que el número de elementos.
     */
    @Override public T get(int i) {
        if (i < 0 || i >= elementos)
            throw new NoSuchElementException("Índice fuera de rango.");
        return arbol[i];
    }

    /**
     * Regresa una representación en cadena del montículo mínimo.
     * @return una representación en cadena del montículo mínimo.
     */
    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < elementos; i++) {
            sb.append(arbol[i].toString());
            sb.append(", ");
        }
        return sb.toString();
    }

    /**
     * Nos dice si el montículo mínimo es igual al objeto recibido.
     * @param objeto el objeto con el que queremos comparar el montículo mínimo.
     * @return <code>true</code> si el objeto recibido es un montículo mínimo
     *         igual al que llama el método; <code>false</code> en otro caso.
     */
    @Override public boolean equals(Object objeto) {
        if (objeto == null || getClass() != objeto.getClass())
            return false;
        @SuppressWarnings("unchecked") MonticuloMinimo<T> monticulo =
            (MonticuloMinimo<T>)objeto;
        
        if (elementos != monticulo.elementos)
            return false;

        for (int i = 0; i < elementos; i++)
            if (!arbol[i].equals(monticulo.arbol[i]))
                return false;
        return true;
    }

    /**
     * Regresa un iterador para iterar el montículo mínimo. El montículo se
     * itera en orden BFS.
     * @return un iterador para iterar el montículo mínimo.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }

    /**
     * Ordena la colección usando HeapSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param coleccion la colección a ordenar.
     * @return una lista ordenada con los elementos de la colección.
     */
    public static <T extends Comparable<T>>
    Lista<T> heapSort(Coleccion<T> coleccion) {
        Iterable<Adaptador<T>> it = () -> new Iterator<Adaptador<T>>() {
            private Iterator<T> it = coleccion.iterator();
            public boolean hasNext() { return it.hasNext(); }
            public Adaptador<T> next() { return new Adaptador<T>(it.next()); }
        };

        MonticuloMinimo<Adaptador<T>> m =
            new MonticuloMinimo<Adaptador<T>>(it, coleccion.getElementos());

        Lista<T> resultado = new Lista<T>();
        while (!m.esVacia())
            resultado.agrega(m.elimina().elemento);
        return resultado;
    }
}
