package mx.unam.ciencias.edd;

import java.util.Iterator;

/**
 * Clase para conjuntos.
 */
public class Conjunto<T> implements Coleccion<T> {

    /* El conjunto de elementos. */
    private Diccionario<T, T> conjunto;

    /**
     * Crea un nuevo conjunto.
     */
    public Conjunto() {
        conjunto = new Diccionario<T,T>();
    }

    /**
     * Crea un nuevo conjunto para un número determinado de elementos.
     * @param n el número tentativo de elementos.
     */
    public Conjunto(int n) {
        conjunto = new Diccionario<T,T>(n<<1);
    }

    /**
     * Agrega un elemento al conjunto.
     * @param elemento el elemento que queremos agregar al conjunto.
     * @throws IllegalArgumentException si el elemento es <code>null</code>.
     */
    @Override public void agrega(T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException("No podemos agregar null.");
        conjunto.agrega(elemento, elemento);
    }

    /**
     * Nos dice si el elemento está en el conjunto.
     * @param elemento el elemento que queremos saber si está en el conjunto.
     * @return <code>true</code> si el elemento está en el conjunto,
     *         <code>false</code> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
        return conjunto.contiene(elemento);
    }

    /**
     * Elimina el elemento del conjunto, si está.
     * @param elemento el elemento que queremos eliminar del conjunto.
     */
    @Override public void elimina(T elemento) {
        if (conjunto.contiene(elemento))
            conjunto.elimina(elemento);
    }

    /**
     * Nos dice si el conjunto es vacío.
     * @return <code>true</code> si el conjunto es vacío, <code>false</code> en
     *         otro caso.
     */
    @Override public boolean esVacia() {
        return conjunto.esVacia();
    }

    /**
     * Regresa el número de elementos en el conjunto.
     * @return el número de elementos en el conjunto.
     */
    @Override public int getElementos() {
        return conjunto.getElementos();
    }

    /**
     * Limpia el conjunto de elementos, dejándolo vacío.
     */
    @Override public void limpia() {
        conjunto.limpia();
    }

    /**
     * Regresa la intersección del conjunto y el conjunto recibido.
     * @param conjunto el conjunto que queremos intersectar con éste.
     * @return la intersección del conjunto y el conjunto recibido.
     */
    public Conjunto<T> interseccion(Conjunto<T> conjunto) {
        Conjunto<T> interseccion = new Conjunto<T>(minN(this, conjunto));
        if (conjunto == null)
            return interseccion;
        for (T e : this.conjunto)
            if (conjunto.contiene(e))
                interseccion.agrega(e);
        return interseccion;
    }

    /* Regresa el número que corresponde a la cardinalidad menor. */
    private int minN(Conjunto<T> a, Conjunto<T> b) {
        int n = a != null ? a.getElementos() : 0;
        int m = b != null ? b.getElementos() : 0;
        return Integer.min(n, m);
    }

    /**
     * Regresa la unión del conjunto y el conjunto recibido.
     * @param conjunto el conjunto que queremos unir con éste.
     * @return la unión del conjunto y el conjunto recibido.
     */
    public Conjunto<T> union(Conjunto<T> conjunto) {
        Conjunto<T> union = new Conjunto<T>(sumaN(this, conjunto));
        for (T e : this.conjunto)
            union.agrega(e);
        if (conjunto == null)
            return union;
        for (T e : conjunto.conjunto)
            union.agrega(e);
        return union;
    }

    /* Regresa la suma de las cardinalidades. */
    private int sumaN(Conjunto<T> a, Conjunto<T> b) {
        int n = a != null ? a.getElementos() : 0;
        int m = b != null ? b.getElementos() : 0;
        return n+m;
    }

    /**
     * Regresa una representación en cadena del conjunto.
     * @return una representación en cadena del conjunto.
     */
    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<T> it = conjunto.iterator();
        sb.append("{ ");
        while (it.hasNext())
            sb.append(it.next().toString()).append(it.hasNext() ? ", " : " ");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Nos dice si el conjunto es igual al objeto recibido.
     * @param o el objeto que queremos saber si es igual al conjunto.
     * @return <code>true</code> si el objeto recibido es instancia de Conjunto,
     *         y tiene los mismos elementos.
     */
    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        @SuppressWarnings("unchecked") Conjunto<T> c = (Conjunto<T>)o;
        return this.conjunto.equals(c.conjunto);
    }

    /**
     * Regresa un iterador para iterar el conjunto.
     * @return un iterador para iterar el conjunto.
     */
    @Override public Iterator<T> iterator() {
        return conjunto.iterator();
    }
}
