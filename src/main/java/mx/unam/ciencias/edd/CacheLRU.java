package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para cachés LRU. Una caché LRU contiene un {@link Diccionario} que no
 * puede rebasar una capacidad dada, de modo que al exceder el límite de valores,
 * se desaloja el valor usado menos recientemente.
 */
public class CacheLRU<K, V> implements Iterable<V> {
    /* Clase interna privada para nodos. */
    private class Nodo {
        /* La llave del nodo */
        private K llave;
        /* El valor del nodo. */
        private V valor;
        /* El nodo anterior. */
        private Nodo anterior;
        /* El nodo siguiente. */
        private Nodo siguiente;

        /** 
         * Construye un nodo con una llave y un valor.
         */
        public Nodo(K llave, V valor) {
            this.llave = llave;
            this.valor = valor;
        }
    }

    /* Clase interna privada para iteradores. */
    private class IteradorNodos {
        /* El nodo anterior. */
        private Nodo anterior;
        /* El nodo siguiente. */
        private Nodo siguiente;

        /* Construye un nuevo iterador. */
        private IteradorNodos() {
            siguiente = cabezaMRU;
        }

        /* Nos dice si hay un elemento siguiente. */
        private boolean hasNext() {
            return siguiente != null;
        }

        /* Nos da el elemento siguiente. */
        private Nodo next() {
            if (!hasNext())
                throw new NoSuchElementException("Se nos acabó el caché.");

            anterior = siguiente;
            siguiente = siguiente.siguiente;
            return anterior;
        }

        /* Nos dice si hay un elemento anterior. */
        private boolean hasPrevious() {
            return anterior != null;
        }

        /* Nos da el elemento anterior. */
        private Nodo previous() {
            if (!hasPrevious())
                throw new NoSuchElementException("Se nos acabó el caché.");

            siguiente = anterior;
            anterior = anterior.anterior;
            return siguiente;
        }

        /* Mueve el iterador al inicio de la lista. */
        private void start() {
            siguiente = cabezaMRU;
            anterior = null;
        }

        /* Mueve el iterador al final de la lista. */
        private void end() {
            siguiente = null;
            anterior = raboLRU;
        }
    }

    /* Clase interna privada para iteradores de llaves. */
    private class IteradorLlaves implements IteradorLista<K> {
        /* Iterador sobre los nodos. */
        IteradorNodos it;

        /* Construye un nuevo iterador. */
        private IteradorLlaves() {
            it = new IteradorNodos();
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override public boolean hasNext() {
            return it.hasNext();
        }

        /* Nos da el elemento siguiente. */
        @Override public K next() {
            return it.next().llave;
        }

        /* Nos dice si hay un elemento anterior. */
        @Override public boolean hasPrevious() {
            return it.hasPrevious();
        }

        /* Nos da el elemento anterior. */
        @Override public K previous() {
            return it.previous().llave;
        }

        /* Mueve el iterador al inicio de la lista. */
        @Override public void start() {
            it.start();
        }

        /* Mueve el iterador al final de la lista. */
        @Override public void end() {
            it.end();
        }
    }

    /* Clase interna privada para iteradores de valores. */
    private class IteradorValores implements IteradorLista<V> {
        /* Iterador sobre los nodos. */
        IteradorNodos it;

        /* Construye un nuevo iterador. */
        private IteradorValores() {
            it = new IteradorNodos();
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override public boolean hasNext() {
            return it.hasNext();
        }

        /* Nos da el elemento siguiente. */
        @Override public V next() {
            return it.next().valor;
        }

        /* Nos dice si hay un elemento anterior. */
        @Override public boolean hasPrevious() {
            return it.hasPrevious();
        }

        /* Nos da el elemento anterior. */
        @Override public V previous() {
            return it.previous().valor;
        }

        /* Mueve el iterador al inicio de la lista. */
        @Override public void start() {
            it.start();
        }

        /* Mueve el iterador al final de la lista. */
        @Override public void end() {
            it.end();
        }
    }

    /* Número máximo de elementos en el caché. */
    private final int capacidad;
    /* Caché de nodos para acceso constante. */
    private Diccionario<K, Nodo> cache;
    /* El nodo más recientemente usado. */
    private Nodo cabezaMRU;
    /* El nodo menos recientemente usado. */
    private Nodo raboLRU;

    /**
     * Construye un caché que desaloja al elemento menos recientemente usado si
     * se excede la capacidad de la caché.
     * @param capacidad la cantidad máxima de elementos en el caché antes de
     *                  desalojar al menos usado tras agregar.
     * @throws IllegalArgumentException si la capacidad es menor a 2.
     */
    public CacheLRU(int capacidad) {
        if (capacidad < 2)
            throw new IllegalArgumentException("La capacidad debe ser al menos 2.");
        this.capacidad = capacidad;
        this.cache = new Diccionario<>(capacidad << 1);;
    }

    /**
     * Regresa la cantidad de elementos en el caché.
     * @return el número de elementos en el caché.
     */
    public int getElementos() {
        return cache.getElementos();
    }

    /**
     * Regresa la capacidad de la caché.
     * @return La cantidad máxima de elementos en la caché.
     */
    public int getCapacidad() {
        return capacidad;
    }

    /**
     * Nos dice si la caché es vacía.
     * @return <code>true</code> si la caché es vacía,
     *         <code>false</code> en otro caso.
     */
    public boolean esVacia() {
        return cache.esVacia();
    }

    /**
     * Indica si la caché contiene la llave. No cuenta como uso.
     * @param llave la llave para buscar el valor
     * @return <code>true</code> si la llave está en el diccionario,
     *         <code>false</code> en otro caso.
     */
    public boolean contiene(K llave) {
        return cache.contiene(llave);
    }

    /**
     * Nos da el valor asociado a la llave y lo marca como usado recientemente.
     * @param llave la llave para buscar el valor
     * @return El valor asociado a la llave.
     * @throws IllegalArgumentException si la llave es nula
     * @throws NoSuchElementException si la llave no está en la caché
     */
    public V get(K llave) {
        Nodo nodo = cache.get(llave);
        agregaNodo(nodo);
        return nodo.valor;
    }

    /**
     * Agrega un nuevo valor al caché, usando la llave proporcionada. Si
     * la llave ya había sido utilizada antes para agregar un valor, la
     * caché reemplaza ese valor con el recibido. Si la caché está a capacidad,
     * se desalojará al elemento menos utilizado.
     * @param llave la llave para agregar el valor.
     * @param valor el valor a agregar.
     * @throws IllegalArgumentException si la llave o el valor son nulos.
     */
    public void agrega(K llave, V valor) {
        if (llave == null)
            throw new IllegalArgumentException("La llave no puede ser null.");
        if (valor == null)
            throw new IllegalArgumentException("El valor no puede ser null.");
        agregaNodo(new Nodo(llave, valor));
    }

    /* Método privado para agregar nodos existentes y marcar el uso. */
    private void agregaNodo(Nodo nodo) {
        if (cache.contiene(nodo.llave))
            elimina(nodo.llave);
        if (cabezaMRU == null) {
            raboLRU = cabezaMRU = nodo;
            cache.agrega(nodo.llave, nodo);
            return;
        }
        if (getElementos() == capacidad) {
            cache.elimina(raboLRU.llave);
            raboLRU = raboLRU.anterior;
            raboLRU.siguiente = null;
        }
        nodo.siguiente = cabezaMRU;
        cabezaMRU.anterior = nodo;
        cabezaMRU = nodo;
        cache.agrega(nodo.llave, nodo);
    }

    /**
     * Elimina el valor de la caché asociada a la llave.
     * @param llave la llave para buscar el valor a eliminar.
     * @throws IllegalArgumentException si la llave es nula.
     * @throws NoSuchElementException si la llave no se encuentra en la caché.
     */
    public void elimina(K llave) {
        if (llave == null)
            throw new IllegalArgumentException("La llave no puede ser null");
        if (!cache.contiene(llave))
            throw new NoSuchElementException("No se encuentra esa llave.");
        Nodo n = cache.get(llave);
        if (getElementos() <= 1) {
            limpia();
            return;
        }
        if (n == cabezaMRU) {
            eliminaMRU();
            return;
        }
        if (n == raboLRU) {
            eliminaLRU();
            return;
        }
        n.anterior.siguiente = n.siguiente;
        n.siguiente.anterior = n.anterior;
        cache.elimina(llave);
    }

    /**
     * Elimina el valor usado más recientemente y lo regresa.
     * @return El valor usado más recientemente.
     * @throws NoSuchElementException si la caché es vacía.
     */
    public V eliminaMRU() {
        if (cabezaMRU == null)
            throw new NoSuchElementException("La caché está vacía.");
        Nodo aEliminar = cabezaMRU;
        if (getElementos() == 1) {
            limpia();
        } else {
            cabezaMRU = cabezaMRU.siguiente;
            cabezaMRU.anterior = null;
            cache.elimina(aEliminar.llave);
        }
        return aEliminar.valor;
    }

    /**
     * Elimina el valor usado menos recientemente y lo regresa.
     * @return El valor usado menos recientemente.
     * @throws NoSuchElementException si la caché es vacía.
     */
    public V eliminaLRU() {
        if (raboLRU == null)
            throw new NoSuchElementException("La caché está vacía.");
        Nodo aEliminar = raboLRU;
        if (getElementos() <= 1) {
            limpia();
        } else {
            raboLRU = raboLRU.anterior;
            raboLRU.siguiente = null;
            cache.elimina(aEliminar.llave);
        }
        return aEliminar.valor;
    }

    /**
     * Vacía la caché.
     */
    public void limpia() {
        cache.limpia();
        cabezaMRU = raboLRU = null;
    }

    /**
     * Regresa una copia de la caché. La copia tiene los mismos elementos en
     * el mismo orden, i.e. conserva la jerarquía de uso reciente.
     * @return una copia de la caché.
     */
    public CacheLRU<K, V> copia() {
        CacheLRU<K, V> cache = new CacheLRU<>(capacidad);
        for (Nodo nodo = raboLRU; nodo != null; nodo = nodo.anterior)
            cache.agrega(nodo.llave, nodo.valor);
        return cache;
    }

    /**
     * Regresa el valor usado más recientemente. No cuenta como uso.
     * @return El valor usado más recientemente.
     * @throws NoSuchElementException si la caché es vacía.
     */
    public V getMRU() {
        if (cabezaMRU == null)
            throw new NoSuchElementException("La caché está vacía.");
        return cabezaMRU.valor;
    }

    /**
     * Regresa el valor usado menos recientemente. No cuenta como uso.
     * @return El valor usado menos recientemente.
     * @throws NoSuchElementException si la caché es vacía.
     */
    public V getLRU() {
        if (raboLRU == null)
            throw new NoSuchElementException("La caché está vacía.");
        return raboLRU.valor;
    }

    /**
     * Nos dice cuántas colisiones hay en la caché.
     * @return cuántas colisiones hay en la caché.
     */
    public int colisiones() {
        return cache.colisiones();
    }

    /**
     * Nos dice el máximo número de colisiones para una misma llave en la caché.
     * @return el máximo número de colisiones para una misma llave.
     */
    public int colisionMaxima() {
        return cache.colisionMaxima();
    }

    /**
     * Regresa una representación en cadena de la caché con el más reciente antes.
     * @return una representación en cadena de la caché con el más reciente antes.
     */
    @Override public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Nodo nodo = cabezaMRU; nodo != null; nodo = nodo.siguiente) {
            concatenaNodo(nodo, sb);
            if(nodo != raboLRU)
                sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    /* Agrega un nodo como cadena a un StringBuilder. */
    private void concatenaNodo(Nodo nodo, StringBuilder sb) {
        sb.append('\'').append(nodo.llave.toString()).append("': '")
          .append(nodo.valor.toString()).append('\'').toString();
    }

    /**
     * Nos dice si la caché es igual al objeto recibido.
     *
     * @param o el objeto a comparar
     * @return <code>true</code> si el objeto recibido es instancia de CacheLRU,
     *         tiene las mismas llaves asociadas a los mismos valores, tienen la
     *         misma capacidad, y se encuentran en el mismo orden de uso.
     */
    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        @SuppressWarnings("unchecked") CacheLRU<K, V> c = (CacheLRU<K, V>)o;
        if (capacidad != c.capacidad)
            return false;
        if (getElementos() != c.getElementos())
            return false;
        Nodo nuestro = cabezaMRU;
        Nodo otro = c.cabezaMRU;
        while (nuestro != null && otro != null) {
            if (!nuestro.llave.equals(otro.llave))
                return false;
            if (!nuestro.valor.equals(otro.valor))
                return false;
            nuestro = nuestro.siguiente;
            otro = otro.siguiente;
        }
        if (nuestro != null || otro != null)
            return false;
        return true;
    }

    /**
     * Regresa un iterador para iterar las llaves de la caché en ambas direcciones.
     * Se ordena de usado más recientemente a menos recientemente.
     * No altera el orden.
     * @return un iterador para iterar las llaves de la caché en ambas direcciones.
     */
    public IteradorLista<K> iteradorListaLlaves() {
        return new IteradorLlaves();
    }

    /**
     * Regresa un iterador para iterar las llaves de la caché en una dirección.
     * Se ordena de usado más recientemente a menos recientemente.
     * No altera el orden.
     * @return un iterador para iterar las llaves de la caché en una dirección.
     */
    public Iterator<K> iteradorLlaves() {
        return new IteradorLlaves();
    }

    /**
     * Regresa un iterador para iterar los valores de la caché en ambas direcciones.
     * Se ordena de usado más recientemente a menos recientemente.
     * No altera el orden.
     * @return un iterador para iterar los valores de la caché en ambas direcciones.
     */
    public IteradorLista<V> iteradorLista() {
        return new IteradorValores();
    }

    /**
     * Regresa un iterador para iterar los valores de la caché en una dirección.
     * Se ordena de usado más recientemente a menos recientemente.
     * No altera el orden.
     * @return un iterador para iterar los valores de la caché en una dirección.
     */
    @Override public Iterator<V> iterator() {
        return new IteradorValores();
    }

    /**
     * Obtiene el valor asociado a una llave. Si no lo encuentra, corre el
     * código de la interfaz funcional para guardar y regresar el resultado.
     * @param llave la llave asociada con el valor.
     * @param siFalla si no se encuentra la llave en la caché, la agregará con
     *                el valor que resulte de correr esta función. Idealmente es
     *                determinista dada la llave y caro de computar.
     * @return El valor asociado a la llave dada en la caché.
     */
    public V tryGet(K llave, AccionCache<V> siFalla) {
        if (!contiene(llave))
            agrega(llave, siFalla.computa());
        return get(llave);
    }
}
