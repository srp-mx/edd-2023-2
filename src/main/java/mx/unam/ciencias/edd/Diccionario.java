package mx.unam.ciencias.edd;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para diccionarios (<em>hash tables</em>). Un diccionario generaliza el
 * concepto de arreglo, mapeando un conjunto de <em>llaves</em> a una colección
 * de <em>valores</em>.
 */
public class Diccionario<K, V> implements Iterable<V> {

    /* Clase interna privada para entradas. */
    private class Entrada {

        /* La llave. */
        public K llave;
        /* El valor. */
        public V valor;

        /* Construye una nueva entrada. */
        public Entrada(K llave, V valor) {
            this.llave = llave;
            this.valor = valor;
        }
    }

    /* Clase interna privada para iteradores. */
    private class Iterador {

        /* En qué lista estamos. */
        private int indice;
        /* Iterador auxiliar. */
        private Iterator<Entrada> iterador;

        /*
         * Construye un nuevo iterador, auxiliándose de las listas del
         * diccionario.
         */
        public Iterador() {
            indice = -1;
            if (elementos > 0)
                sigIt();
        }

        /* Nos dice si hay una siguiente entrada. */
        public boolean hasNext() {
            return iterador != null && (iterador.hasNext() ? true : sigIt());
        }

        /* Regresa la siguiente entrada. */
        public Entrada siguiente() {
            if (!hasNext())
                throw new NoSuchElementException("No hay siguiente.");
            return iterador.next();
        }

        /*
         * Reemplaza el iterador con el de la siguiente lista de entradas.
         * Regresa true si hay siguiente y false de lo contrario.
         */
        private boolean sigIt() {
            while (enRango(++indice) && entradas[indice] == null)
                ;
            if (!enRango(indice))
                return false;
            iterador = entradas[indice].iterator();
            return true;
        }

        /* Regresa true si el indice es accesible en el arreglo de entradas. */
        private boolean enRango(int indice) {
            return indice < entradas.length;
        }
    }

    /* Clase interna privada para iteradores de llaves. */
    private class IteradorLlaves extends Iterador
            implements Iterator<K> {

        /* Regresa el siguiente elemento. */
        @Override
        public K next() {
            return siguiente().llave;
        }
    }

    /* Clase interna privada para iteradores de valores. */
    private class IteradorValores extends Iterador
            implements Iterator<V> {

        /* Regresa el siguiente elemento. */
        @Override
        public V next() {
            return siguiente().valor;
        }
    }

    /** Máxima carga permitida por el diccionario. */
    public static final double MAXIMA_CARGA = 0.72;

    /* Capacidad mínima; decidida arbitrariamente a 2^6. */
    private static final int MINIMA_CAPACIDAD = 64;

    /* Dispersor. */
    private Dispersor<K> dispersor;
    /* Nuestro diccionario. */
    private Lista<Entrada>[] entradas;
    /* Número de valores. */
    private int elementos;

    /*
     * Truco para crear un arreglo genérico. Es necesario hacerlo así por cómo
     * Java implementa sus genéricos; de otra forma obtenemos advertencias del
     * compilador.
     */
    @SuppressWarnings("unchecked")
    private Lista<Entrada>[] nuevoArreglo(int n) {
        return (Lista<Entrada>[]) Array.newInstance(Lista.class, n);
    }

    /**
     * Construye un diccionario con una capacidad inicial y dispersor
     * predeterminados.
     */
    public Diccionario() {
        this(MINIMA_CAPACIDAD, (K llave) -> llave.hashCode());
    }

    /**
     * Construye un diccionario con una capacidad inicial definida por el
     * usuario, y un dispersor predeterminado.
     * 
     * @param capacidad la capacidad a utilizar.
     */
    public Diccionario(int capacidad) {
        this(capacidad, (K llave) -> llave.hashCode());
    }

    /**
     * Construye un diccionario con una capacidad inicial predeterminada, y un
     * dispersor definido por el usuario.
     * 
     * @param dispersor el dispersor a utilizar.
     */
    public Diccionario(Dispersor<K> dispersor) {
        this(MINIMA_CAPACIDAD, dispersor);
    }

    /**
     * Construye un diccionario con una capacidad inicial y un método de
     * dispersor definidos por el usuario.
     * 
     * @param capacidad la capacidad inicial del diccionario.
     * @param dispersor el dispersor a utilizar.
     */
    public Diccionario(int capacidad, Dispersor<K> dispersor) {
        this.dispersor = dispersor;
        if (capacidad < MINIMA_CAPACIDAD)
            entradas = nuevoArreglo(MINIMA_CAPACIDAD);
        else
            entradas = nuevoArreglo(siguientePow2(capacidad));
    }

    /* Obtiene la potencia de dos mayor más cercana a x. */
    private int siguientePow2(int x) {
        return Integer.highestOneBit(x - 1) << 2;
    }

    /**
     * Agrega un nuevo valor al diccionario, usando la llave proporcionada. Si
     * la llave ya había sido utilizada antes para agregar un valor, el
     * diccionario reemplaza ese valor con el recibido aquí.
     * 
     * @param llave la llave para agregar el valor.
     * @param valor el valor a agregar.
     * @throws IllegalArgumentException si la llave o el valor son nulos.
     */
    public void agrega(K llave, V valor) {
        if (llave == null)
            throw new IllegalArgumentException("La llave no puede ser null.");
        if (valor == null)
            throw new IllegalArgumentException("El valor no puede ser null.");
        mete(hash(llave), llave, valor);
    }

    /* Mete una llave y valor al diccionario en el índice especificado. */
    private void mete(int indice, K llave, V valor) {
        Entrada entradaRepetida = buscador(llave);
        if (entradaRepetida != null) {
            entradaRepetida.valor = valor;
            return;
        }

        if (entradas[indice] == null)
            entradas[indice] = new Lista<Entrada>();

        entradas[indice].agrega(new Entrada(llave, valor));
        elementos++;
        verificaCarga();
    }

    /* Crece el arreglo de entradas de ser necesario. */
    private void verificaCarga() {
        if (elementos / (double) entradas.length > MAXIMA_CARGA)
            crece();
    }

    /* Hace crecer el arreglo. */
    private void crece() {
        Lista<Entrada>[] viejo = entradas;
        entradas = nuevoArreglo(viejo.length * 2);
        elementos = 0;
        for (int i = 0; i < viejo.length; i++) {
            if (viejo[i] == null)
                continue;
            for (Entrada entrada : viejo[i])
                mete(hash(entrada.llave), entrada.llave, entrada.valor);
        }
    }

    /* Obtiene el índice en el arreglo que le corresponde a la llave. */
    private int hash(K llave) {
        return dispersor.dispersa(llave) & (entradas.length - 1);
    }

    /**
     * Regresa el valor del diccionario asociado a la llave proporcionada.
     * 
     * @param llave la llave para buscar el valor.
     * @return el valor correspondiente a la llave.
     * @throws IllegalArgumentException si la llave es nula.
     * @throws NoSuchElementException   si la llave no está en el diccionario.
     */
    public V get(K llave) {
        if (llave == null)
            throw new IllegalArgumentException("La llave no puede ser null.");
        Entrada entrada = buscador(llave);
        if (entrada == null)
            throw new NoSuchElementException("No hay entrada con tal llave.");
        return entrada.valor;
    }

    /* Regresa null o la entrada que tiene la llave dada. */
    private Entrada buscador(K llave) {
        Lista<Entrada> cubeta = entradas[hash(llave)];
        if (cubeta == null)
            return null;
        for (Entrada entrada : cubeta)
            if (entrada.llave.equals(llave))
                return entrada;
        return null;
    }

    /**
     * Nos dice si una llave se encuentra en el diccionario.
     * 
     * @param llave la llave que queremos ver si está en el diccionario.
     * @return <code>true</code> si la llave está en el diccionario,
     *         <code>false</code> en otro caso.
     */
    public boolean contiene(K llave) {
        return llave != null ? buscador(llave) != null : false;
    }

    /**
     * Elimina el valor del diccionario asociado a la llave proporcionada.
     * 
     * @param llave la llave para buscar el valor a eliminar.
     * @throws IllegalArgumentException si la llave es nula.
     * @throws NoSuchElementException   si la llave no se encuentra en
     *                                  el diccionario.
     */
    public void elimina(K llave) {
        if (llave == null)
            throw new IllegalArgumentException("La llave no puede ser null.");
        Entrada entrada = buscador(llave);
        if (entrada == null)
            throw new NoSuchElementException("No se encuentra esa llave");
        entradas[hash(llave)].elimina(entrada);
        if (entradas[hash(llave)].getElementos() == 0)
            entradas[hash(llave)] = null;
        elementos--;
    }

    /**
     * Nos dice cuántas colisiones hay en el diccionario.
     * 
     * @return cuántas colisiones hay en el diccionario.
     */
    public int colisiones() {
        int n = 0;
        for (int i = 0; i < entradas.length; i++)
            if (entradas[i] != null && entradas[i].getElementos() > 1)
                n++;
        return n;
    }

    /* Obtiene el máximo de dos enteros. */
    private int max(int a, int b) {
        return a < b ? b : a;
    }

    /**
     * Nos dice el máximo número de colisiones para una misma llave que tenemos
     * en el diccionario.
     * 
     * @return el máximo número de colisiones para una misma llave.
     */
    public int colisionMaxima() {
        int n = 0;
        for (int i = 0; i < entradas.length; i++)
            if (entradas[i] != null)
                n = max(n, entradas[i].getElementos() - 1);
        return n;
    }

    /**
     * Nos dice la carga del diccionario.
     * 
     * @return la carga del diccionario.
     */
    public double carga() {
        return (double) elementos / (double) entradas.length;
    }

    /**
     * Regresa el número de entradas en el diccionario.
     * 
     * @return el número de entradas en el diccionario.
     */
    public int getElementos() {
        return elementos;
    }

    /**
     * Nos dice si el diccionario es vacío.
     * 
     * @return <code>true</code> si el diccionario es vacío, <code>false</code>
     *         en otro caso.
     */
    public boolean esVacia() {
        return elementos == 0;
    }

    /**
     * Limpia el diccionario de elementos, dejándolo vacío.
     */
    public void limpia() {
        entradas = nuevoArreglo(MINIMA_CAPACIDAD);
        elementos = 0;
    }

    /**
     * Regresa una representación en cadena del diccionario.
     * 
     * @return una representación en cadena del diccionario.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        if (elementos > 0)
            sb.append(' ');
        for (int i = 0; i < entradas.length; i++) {
            if (entradas[i] == null)
                continue;
            for (Entrada entrada : entradas[i])
                concatenaEntrada(entrada, sb);
        }
        sb.append('}');
        return sb.toString();
    }

    /* Agrega una entrada como cadena a un StringBuilder. */
    private void concatenaEntrada(Entrada entrada, StringBuilder sb) {
        sb.append('\'');
        sb.append(entrada.llave.toString());
        sb.append('\'').append(':').append(' ').append('\'');
        sb.append(entrada.valor.toString());
        sb.append('\'').append(',').append(' ');
    }

    /**
     * Nos dice si el diccionario es igual al objeto recibido.
     * 
     * @param o el objeto que queremos saber si es igual al diccionario.
     * @return <code>true</code> si el objeto recibido es instancia de
     *         Diccionario, y tiene las mismas llaves asociadas a los mismos
     *         valores.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        @SuppressWarnings("unchecked")
        Diccionario<K, V> d = (Diccionario<K, V>) o;
        if (elementos != d.elementos)
            return false;
        for (int i = 0; i < entradas.length; i++) {
            if (entradas[i] == null)
                continue;
            for (Entrada entrada : entradas[i]) {
                Entrada otra = d.buscador(entrada.llave);
                if (otra == null)
                    return false;
                if (!otra.valor.equals(entrada.valor))
                    return false;
            }
        }
        return true;
    }

    /**
     * Regresa un iterador para iterar las llaves del diccionario. El
     * diccionario se itera sin ningún orden específico.
     * 
     * @return un iterador para iterar las llaves del diccionario.
     */
    public Iterator<K> iteradorLlaves() {
        return new IteradorLlaves();
    }

    /**
     * Regresa un iterador para iterar los valores del diccionario. El
     * diccionario se itera sin ningún orden específico.
     * 
     * @return un iterador para iterar los valores del diccionario.
     */
    @Override
    public Iterator<V> iterator() {
        return new IteradorValores();
    }
}
