package mx.unam.ciencias.edd;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>Clase genérica para listas doblemente ligadas.</p>
 *
 * <p>Las listas nos permiten agregar elementos al inicio o final de la lista,
 * eliminar elementos de la lista, comprobar si un elemento está o no en la
 * lista, y otras operaciones básicas.</p>
 *
 * <p>Las listas no aceptan a <code>null</code> como elemento.</p>
 *
 * @param <T> El tipo de los elementos de la lista.
 */
public class Lista<T> implements Coleccion<T> {

    /* Clase interna privada para nodos. */
    private class Nodo {
        /* El elemento del nodo. */
        private T elemento;
        /* El nodo anterior. */
        private Nodo anterior;
        /* El nodo siguiente. */
        private Nodo siguiente;

        /* Construye un nodo con un elemento. */
        private Nodo(T elemento) {
            this.elemento = elemento;
        }
    }

    /* Clase interna privada para iteradores. */
    private class Iterador implements IteradorLista<T> {
        /* El nodo anterior. */
        private Nodo anterior;
        /* El nodo siguiente. */
        private Nodo siguiente;

        /* Construye un nuevo iterador. */
        private Iterador() {
            siguiente = cabeza;
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override public boolean hasNext() {
            return siguiente != null;
        }

        /* Nos da el elemento siguiente. */
        @Override public T next() {
            if (!hasNext())
                throw new NoSuchElementException("Se nos acabó la lista.");

            anterior = siguiente;
            siguiente = siguiente.siguiente;
            return anterior.elemento;
        }

        /* Nos dice si hay un elemento anterior. */
        @Override public boolean hasPrevious() {
            return anterior != null;
        }

        /* Nos da el elemento anterior. */
        @Override public T previous() {
            if (!hasPrevious())
                throw new NoSuchElementException("Se nos acabó la lista.");

            siguiente = anterior;
            anterior = anterior.anterior;
            return siguiente.elemento;
        }

        /* Mueve el iterador al inicio de la lista. */
        @Override public void start() {
            siguiente = cabeza;
            anterior = null;
        }

        /* Mueve el iterador al final de la lista. */
        @Override public void end() {
            siguiente = null;
            anterior = rabo;
        }
    }

    /* Primer elemento de la lista. */
    private Nodo cabeza;
    /* Último elemento de la lista. */
    private Nodo rabo;
    /* Número de elementos en la lista. */
    private int longitud;

    /**
     * Regresa la longitud de la lista. El método es idéntico a {@link
     * #getElementos}.
     * @return la longitud de la lista, el número de elementos que contiene.
     */
    public int getLongitud() {
        return getElementos();
    }

    /**
     * Regresa el número elementos en la lista. El método es idéntico a {@link
     * #getLongitud}.
     * @return el número elementos en la lista.
     */
    @Override public int getElementos() {
        return longitud;
    }

    /**
     * Nos dice si la lista es vacía.
     * @return <code>true</code> si la lista es vacía, <code>false</code> en
     *         otro caso.
     */
    @Override public boolean esVacia() {
        return longitud == 0;
    }

    /**
     * Agrega un elemento a la lista. Si la lista no tiene elementos, el
     * elemento a agregar será el primero y último. El método es idéntico a
     * {@link #agregaFinal}.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    @Override public void agrega(T elemento) {
        agregaFinal(elemento);
    }

    /**
     * Agrega un elemento al final de la lista. Si la lista no tiene elementos,
     * el elemento a agregar será el primero y último.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    public void agregaFinal(T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException("El elemento no puede ser null.");

        longitud++;

        Nodo nodo = new Nodo(elemento);

        if (cabeza == null) {
            rabo = cabeza = nodo;
            return;
        }

        nodo.anterior = rabo;
        rabo.siguiente = nodo;
        rabo = nodo;
    }

    /**
     * Agrega un elemento al inicio de la lista. Si la lista no tiene elementos,
     * el elemento a agregar será el primero y último.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    public void agregaInicio(T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException("El elemento no puede ser null.");

        longitud++;

        Nodo nodo = new Nodo(elemento);

        if (cabeza == null) {
            rabo = cabeza = nodo;
            return;
        }

        nodo.siguiente = cabeza;
        cabeza.anterior = nodo;
        cabeza = nodo;
    }

    /**
     * Inserta un elemento en un índice explícito.
     *
     * Si el índice es menor o igual que cero, el elemento se agrega al inicio
     * de la lista. Si el índice es mayor o igual que el número de elementos en
     * la lista, el elemento se agrega al fina de la misma. En otro caso,
     * después de mandar llamar el método, el elemento tendrá el índice que se
     * especifica en la lista.
     * @param i el índice dónde insertar el elemento. Si es menor que 0 el
     *          elemento se agrega al inicio de la lista, y si es mayor o igual
     *          que el número de elementos en la lista se agrega al final.
     * @param elemento el elemento a insertar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    public void inserta(int i, T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException("El elemento no puede ser null.");

        if (i <= 0) {
            agregaInicio(elemento);
            return;
        }

        if (i >= longitud) {
            agregaFinal(elemento);
            return;
        }

        longitud++;
        Nodo nuevo = new Nodo(elemento);

        Nodo antiguo = cabeza;
        while (i-- > 0) {
            antiguo = antiguo.siguiente;
        }

        nuevo.anterior = antiguo.anterior;
        nuevo.siguiente = antiguo;
        nuevo.anterior.siguiente = nuevo;
        antiguo.anterior = nuevo;
    }

    /**
     * Elimina un elemento de la lista. Si el elemento no está contenido en la
     * lista, el método no la modifica.
     * @param elemento el elemento a eliminar.
     */
    @Override public void elimina(T elemento) {
        Nodo n = busca(elemento);
        if (n == null)
            return;

        if (longitud <= 1) {
            limpia();
            return;
        }

        if (n == cabeza) {
            eliminaPrimero();
            return;
        }

        if (n == rabo) {
            eliminaUltimo();
            return;
        }

        n.anterior.siguiente = n.siguiente;
        n.siguiente.anterior = n.anterior;

        longitud--;
    }

    /**
     * Elimina el primer elemento de la lista y lo regresa.
     * @return el primer elemento de la lista antes de eliminarlo.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T eliminaPrimero() {
        if (cabeza == null)
            throw new NoSuchElementException("La lista es vacía, no podemos eliminar al primer elemento.");

        Nodo aEliminar = cabeza;
        if (longitud == 1) {
            limpia();
        } else {
            cabeza = cabeza.siguiente;
            cabeza.anterior = null;
            longitud--;
        }
        return aEliminar.elemento;
    }

    /**
     * Elimina el último elemento de la lista y lo regresa.
     * @return el último elemento de la lista antes de eliminarlo.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T eliminaUltimo() {
        if (rabo == null)
            throw new NoSuchElementException("La lista es vacía, no podemos eliminar al ultimo elemento.");

        Nodo aEliminar = rabo;
        if (longitud <= 1) {
            limpia();
        } else {
            rabo = rabo.anterior;
            rabo.siguiente = null;
            longitud--;
        }

        return aEliminar.elemento;
    }

    /**
     * Nos dice si un elemento está en la lista.
     * @param elemento el elemento que queremos saber si está en la lista.
     * @return <code>true</code> si <code>elemento</code> está en la lista,
     *         <code>false</code> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
        return indiceDe(elemento) != -1;
    }

    /**
     * Regresa la reversa de la lista.
     * @return una nueva lista que es la reversa la que manda llamar el método.
     */
    public Lista<T> reversa() {
        Lista<T> lista = new Lista<>();
        for (Nodo nodo = cabeza; nodo != null; nodo = nodo.siguiente) {
            lista.agregaInicio(nodo.elemento);
        }
        return lista;
    }

    /**
     * Regresa una copia de la lista. La copia tiene los mismos elementos que la
     * lista que manda llamar el método, en el mismo orden.
     * @return una copiad de la lista.
     */
    public Lista<T> copia() {
        Lista<T> lista = new Lista<>();
        for (Nodo nodo = cabeza; nodo != null; nodo = nodo.siguiente) {
            lista.agregaFinal(nodo.elemento);
        }
        return lista;
    }

    /**
     * Limpia la lista de elementos, dejándola vacía.
     */
    @Override public void limpia() {
        cabeza = rabo = null;
        longitud = 0;
    }

    /**
     * Regresa el primer elemento de la lista.
     * @return el primer elemento de la lista.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T getPrimero() {
        if (cabeza == null)
            throw new NoSuchElementException("La lista es vacía, no hay primero.");
        return cabeza.elemento;
    }

    /**
     * Regresa el último elemento de la lista.
     * @return el primer elemento de la lista.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T getUltimo() {
        if (rabo == null)
            throw new NoSuchElementException("La lista es vacía, no hay ultimo.");
        return rabo.elemento;
    }

    /**
     * Regresa el <em>i</em>-ésimo elemento de la lista.
     * @param i el índice del elemento que queremos.
     * @return el <em>i</em>-ésimo elemento de la lista.
     * @throws ExcepcionIndiceInvalido si <em>i</em> es menor que cero o mayor o
     *         igual que el número de elementos en la lista.
     */
    public T get(int i) {
        if (i < 0 || i >= longitud)
            throw new ExcepcionIndiceInvalido();

        Nodo nodo = cabeza;
        while (i-- > 0) {
            nodo = nodo.siguiente;
        }
        return nodo.elemento;
    }

    /**
     * Regresa el índice del elemento recibido en la lista.
     * @param elemento el elemento del que se busca el índice.
     * @return el índice del elemento recibido en la lista, o -1 si el elemento
     *         no está contenido en la lista.
     */
    public int indiceDe(T elemento) {
        int i = 0;
        for (Nodo nodo = cabeza; nodo != null; nodo = nodo.siguiente, i++) {
            if (nodo.elemento.equals(elemento))
                return i;
        }

        return -1;
    }

    /* Busca el primer nodo que tenga al elemento especificado */
    private Nodo busca(T elemento) {
        Nodo nodo;
        for (nodo = cabeza; nodo != null; nodo = nodo.siguiente) {
            if (nodo.elemento.equals(elemento))
                break;
        }
        return nodo;
    }

    /**
     * Regresa una representación en cadena de la lista.
     * @return una representación en cadena de la lista.
     */
    @Override public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Nodo nodo = cabeza; nodo != null; nodo = nodo.siguiente) {
            sb.append(nodo.elemento.toString());
            if (nodo != rabo)
                sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Nos dice si la lista es igual al objeto recibido.
     * @param objeto el objeto con el que hay que comparar.
     * @return <code>true</code> si la lista es igual al objeto recibido;
     *         <code>false</code> en otro caso.
     */
    @Override public boolean equals(Object objeto) {
        if (objeto == null || getClass() != objeto.getClass())
            return false;
        @SuppressWarnings("unchecked") Lista<T> lista = (Lista<T>)objeto;
        if (lista.longitud != longitud)
            return false;

        Nodo miNodo = cabeza;
        Nodo suNodo = lista.cabeza;
        while (suNodo != null)
        {
            if (!suNodo.elemento.equals(miNodo.elemento))
                return false;
            suNodo = suNodo.siguiente;
            miNodo = miNodo.siguiente;
        }
        return true;
    }

    /**
     * Regresa un iterador para recorrer la lista en una dirección.
     * @return un iterador para recorrer la lista en una dirección.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }

    /**
     * Regresa un iterador para recorrer la lista en ambas direcciones.
     * @return un iterador para recorrer la lista en ambas direcciones.
     */
    public IteradorLista<T> iteradorLista() {
        return new Iterador();
    }

    /**
     * Regresa una copia de la lista, pero ordenada. Para poder hacer el
     * ordenamiento, el método necesita una instancia de {@link Comparator} para
     * poder comparar los elementos de la lista.
     * @param comparador el comparador que la lista usará para hacer el
     *                   ordenamiento.
     * @return una copia de la lista, pero ordenada.
     */
    public Lista<T> mergeSort(Comparator<T> comparador) {
        if (longitud <= 1)
            return copia();

        return auxiliarMergeSort(comparador, this);
    }

    /* Llamadas recursivas de Merge Sort */
    private Lista<T> auxiliarMergeSort(Comparator<T> c, Lista<T> l) {
        if (l.longitud <= 1) {
            return l;
        }

        Lista<T> l1 = new Lista<T>();
        Lista<T> l2 = new Lista<T>();
        divideMitad(l, l1, l2);

        l1 = auxiliarMergeSort(c, l1);
        l2 = auxiliarMergeSort(c, l2);
        return mezclaMergeSort(c, l1, l2);
    }

    /* Llena dos listas vacías con las mitades de otra */
    private void divideMitad(Lista<T> l, Lista<T> l1, Lista<T> l2) {
        Nodo ptrLento = l.cabeza;
        Nodo ptrRapido = l.cabeza.siguiente;

        while (ptrRapido != null) {
            l1.agregaFinal(ptrLento.elemento);
            ptrLento = ptrLento.siguiente;
            ptrRapido = ptrRapido.siguiente;
            ptrRapido = ptrRapido == null ? null : ptrRapido.siguiente;
        }

        while (ptrLento != null) {
            l2.agregaFinal(ptrLento.elemento);
            ptrLento = ptrLento.siguiente;
        }
    }

    /* Toma dos listas ordenadas y las mezcla en una lista ordenada */
    private Lista<T> 
    mezclaMergeSort(Comparator<T> c, Lista<T> l1, Lista<T> l2) {
        Lista<T> l = new Lista<T>();

        Nodo ptr1 = l1.cabeza;
        Nodo ptr2 = l2.cabeza;

        while (ptr1 != null && ptr2 != null) {
            if (c.compare(ptr1.elemento, ptr2.elemento) <= 0) {
                l.agregaFinal(ptr1.elemento);
                ptr1 = ptr1.siguiente;
            } else {
                l.agregaFinal(ptr2.elemento);
                ptr2 = ptr2.siguiente;
            }
        }

        for (; ptr1 != null; ptr1 = ptr1.siguiente)
            l.agregaFinal(ptr1.elemento);

        for (; ptr2 != null; ptr2 = ptr2.siguiente)
            l.agregaFinal(ptr2.elemento);

        return l;
    }

    /**
     * Regresa una copia de la lista recibida, pero ordenada. La lista recibida
     * tiene que contener nada más elementos que implementan la interfaz {@link
     * Comparable}.
     * @param <T> tipo del que puede ser la lista.
     * @param lista la lista que se ordenará.
     * @return una copia de la lista recibida, pero ordenada.
     */
    public static <T extends Comparable<T>>
    Lista<T> mergeSort(Lista<T> lista) {
        return lista.mergeSort((a, b) -> a.compareTo(b));
    }

    /**
     * Busca un elemento en la lista ordenada, usando el comparador recibido. El
     * método supone que la lista está ordenada usando el mismo comparador.
     * @param elemento el elemento a buscar.
     * @param comparador el comparador con el que la lista está ordenada.
     * @return <code>true</code> si el elemento está contenido en la lista,
     *         <code>false</code> en otro caso.
     */
    public boolean busquedaLineal(T elemento, Comparator<T> comparador) {
        for (Nodo nodo = cabeza; nodo != null; nodo = nodo.siguiente) {
            int comparacion = comparador.compare(nodo.elemento, elemento);
            if (comparacion == 0)
                return true;
            if (comparacion > 0)
                return false;
        }
        return false;
    }

    /**
     * Busca un elemento en una lista ordenada. La lista recibida tiene que
     * contener nada más elementos que implementan la interfaz {@link
     * Comparable}, y se da por hecho que está ordenada.
     * @param <T> tipo del que puede ser la lista.
     * @param lista la lista donde se buscará.
     * @param elemento el elemento a buscar.
     * @return <code>true</code> si el elemento está contenido en la lista,
     *         <code>false</code> en otro caso.
     */
    public static <T extends Comparable<T>>
    boolean busquedaLineal(Lista<T> lista, T elemento) {
        return lista.busquedaLineal(elemento, (a, b) -> a.compareTo(b));
    }
}
