package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para gráficas. Una gráfica es un conjunto de vértices y aristas, tales
 * que las aristas son un subconjunto del producto cruz de los vértices.
 */
public class Grafica<T> implements Coleccion<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Iterador auxiliar. */
        private Iterator<Vertice> iterador;

        /* Construye un nuevo iterador, auxiliándose de la lista de vértices. */
        public Iterador() {
            iterador = vertices.iterator();
        }

        /* Nos dice si hay un siguiente elemento. */
        @Override public boolean hasNext() {
            return iterador.hasNext();
        }

        /* Regresa el siguiente elemento. */
        @Override public T next() {
            return iterador.next().elemento;
        }
    }

    /* Clase interna privada para vértices. */
    private class Vertice implements VerticeGrafica<T>,
                          ComparableIndexable<Vertice> {

        /* El elemento del vértice. */
        private T elemento;
        /* El color del vértice. */
        private Color color;
        /* La distancia del vértice. */
        private double distancia;
        /* El índice del vértice. */
        private int indice;
        /* El diccionario de vecinos del vértice. */
        private Diccionario<T, Vecino> vecinos;

        /* Crea un nuevo vértice a partir de un elemento. */
        public Vertice(T elemento) {
            this.elemento = elemento;
            this.color = Color.NINGUNO;
            this.distancia = Double.POSITIVE_INFINITY;
            this.indice = -1;
            this.vecinos = new Diccionario<T, Vecino>();
        }

        /* Regresa el elemento del vértice. */
        @Override public T get() {
            return elemento;
        }

        /* Regresa el grado del vértice. */
        @Override public int getGrado() {
            return vecinos.getElementos();
        }

        /* Regresa el color del vértice. */
        @Override public Color getColor() {
            return color;
        }

        /* Regresa un iterable para los vecinos. */
        @Override public Iterable<? extends VerticeGrafica<T>> vecinos() {
            return vecinos;
        }

        /* Define el índice del vértice. */
        @Override public void setIndice(int indice) {
            this.indice = indice;
        }

        /* Regresa el índice del vértice. */
        @Override public int getIndice() {
            return indice;
        }

        /* Compara dos vértices por distancia. */
        @Override public int compareTo(Vertice vertice) {
            if (distancia < vertice.distancia) return -1;
            if (distancia == vertice.distancia) return 0;
            return 1;
        }
    }

    /* Clase interna privada para vértices vecinos. */
    private class Vecino implements VerticeGrafica<T> {

        /* El vértice vecino. */
        public Vertice vecino;
        /* El peso de la arista conectando al vértice con su vértice vecino. */
        public double peso;

        /* Construye un nuevo vecino con el vértice recibido como vecino y el
         * peso especificado. */
        public Vecino(Vertice vecino, double peso) {
            this.vecino = vecino;
            this.peso = peso;
        }

        /* Regresa el elemento del vecino. */
        @Override public T get() {
            return vecino.get();
        }

        /* Regresa el grado del vecino. */
        @Override public int getGrado() {
            return vecino.getGrado();
        }

        /* Regresa el color del vecino. */
        @Override public Color getColor() {
            return vecino.getColor();
        }

        /* Regresa un iterable para los vecinos del vecino. */
        @Override public Iterable<? extends VerticeGrafica<T>> vecinos() {
            return vecino.vecinos();
        }
    }

    /* Interface para poder usar lambdas al buscar el elemento que sigue al
     * reconstruir un camino. */
    @FunctionalInterface
    private interface BuscadorCamino<T> {
        /* Regresa true si el vértice se sigue del vecino. */
        public boolean seSiguen(Grafica<T>.Vertice v, Grafica<T>.Vecino a);
    }

    /* Vértices. */
    private Diccionario<T, Vertice> vertices;
    /* Número de aristas. */
    private int aristas;

    /**
     * Constructor único.
     */
    public Grafica() {
        vertices = new Diccionario<T, Vertice>();
    }

    /**
     * Regresa el número de elementos en la gráfica. El número de elementos es
     * igual al número de vértices.
     * @return el número de elementos en la gráfica.
     */
    @Override public int getElementos() {
        return vertices.getElementos();
    }

    /**
     * Regresa el número de aristas.
     * @return el número de aristas.
     */
    public int getAristas() {
        return aristas;
    }

    /**
     * Agrega un nuevo elemento a la gráfica.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si el elemento ya había sido agregado a
     *         la gráfica.
     */
    @Override public void agrega(T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException("No podemos meter null.");
        if (contiene(elemento))
            throw new IllegalArgumentException("Ya teníamos al elemento.");
        vertices.agrega(elemento, new Vertice(elemento));
    }

    /**
     * Conecta dos elementos de la gráfica. Los elementos deben estar en la
     * gráfica. El peso de la arista que conecte a los elementos será 1.
     * @param a el primer elemento a conectar.
     * @param b el segundo elemento a conectar.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b ya están conectados, o si a es
     *         igual a b.
     */
    public void conecta(T a, T b) {
        conecta(a, b, 1.0);
    }

    /**
     * Conecta dos elementos de la gráfica. Los elementos deben estar en la
     * gráfica.
     * @param a el primer elemento a conectar.
     * @param b el segundo elemento a conectar.
     * @param peso el peso de la nueva vecino.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b ya están conectados, si a es
     *         igual a b, o si el peso es no positivo.
     */
    public void conecta(T a, T b, double peso) {
        if (peso <= 0)
            throw new IllegalArgumentException("El peso debe ser >0.");
        Vertice u = v(a);
        Vertice v = v(b);
        if (u == v)
            throw new IllegalArgumentException("No permitimos lazos.");
        if (sonVecinos(a, b))
            throw new IllegalArgumentException("Ya estaban conectados.");
        u.vecinos.agrega(b, new Vecino(v, peso));
        v.vecinos.agrega(a, new Vecino(u, peso));
        aristas++;
    }

    /* Dado un elemento regresa su vértice en la gráfica o lanza excepción. */
    private Vertice v(T elemento) {
        if (vertices.contiene(elemento))
            return vertices.get(elemento);
        throw new NoSuchElementException("No hay tal vértice en la gráfica.");
    }

    /**
     * Desconecta dos elementos de la gráfica. Los elementos deben estar en la
     * gráfica y estar conectados entre ellos.
     * @param a el primer elemento a desconectar.
     * @param b el segundo elemento a desconectar.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b no están conectados.
     */
    public void desconecta(T a, T b) {
        Vertice u = v(a);
        Vertice v = v(b);
        if (!sonVecinos(u, v))
            throw new IllegalArgumentException("Intentamos desconectar " +
                    "vértices que no estaban conectados.");
        desconectaVertices(u, v);
    }

    /* Indica si u tiene a v como vecino. */
    private boolean sonVecinos(Vertice u, Vertice v) {
        return u.vecinos.contiene(v.elemento);
    }

    /* Desconecta dos vértices. */
    private void desconectaVertices(Vertice u, Vertice v) {
        Vecino uv = null, vu = null;
        uv = u.vecinos.get(v.elemento);
        vu = v.vecinos.get(u.elemento);
        desconectaVecinos(uv, vu);
    }

    /* Desconecta dos vértices vecinos. */
    private void desconectaVecinos(Vecino uv, Vecino vu) {
        vu.vecino.vecinos.elimina(uv.vecino.elemento);
        uv.vecino.vecinos.elimina(vu.vecino.elemento);
        aristas--;
    }

    /**
     * Nos dice si el elemento está contenido en la gráfica.
     * @return <code>true</code> si el elemento está contenido en la gráfica,
     *         <code>false</code> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
        return vertices.contiene(elemento);
    }

    /**
     * Elimina un elemento de la gráfica. El elemento tiene que estar contenido
     * en la gráfica.
     * @param elemento el elemento a eliminar.
     * @throws NoSuchElementException si el elemento no está contenido en la
     *         gráfica.
     */
    @Override public void elimina(T elemento) {
        Vertice v = v(elemento);
        for (Vecino vu : v.vecinos)
            desconectaVecinos(vu, vu.vecino.vecinos.get(elemento));
        vertices.elimina(elemento);
    }

    /**
     * Nos dice si dos elementos de la gráfica están conectados. Los elementos
     * deben estar en la gráfica.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @return <code>true</code> si a y b son vecinos, <code>false</code> en otro caso.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     */
    public boolean sonVecinos(T a, T b) {
        return v(a).vecinos.contiene(v(b).get());
    }

    /**
     * Regresa el peso de la arista que comparten los vértices que contienen a
     * los elementos recibidos.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @return el peso de la arista que comparten los vértices que contienen a
     *         los elementos recibidos.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b no están conectados.
     */
    public double getPeso(T a, T b) {
        if (!sonVecinos(a, b))
            throw new IllegalArgumentException("No estan conectados.");
        return v(a).vecinos.get(v(b).elemento).peso;
    }

    /**
     * Define el peso de la arista que comparten los vértices que contienen a
     * los elementos recibidos.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @param peso el nuevo peso de la arista que comparten los vértices que
     *        contienen a los elementos recibidos.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b no están conectados, o si peso
     *         es menor o igual que cero.
     */
    public void setPeso(T a, T b, double peso) {
        if (peso <= 0)
            throw new IllegalArgumentException("El peso debe ser >0.");
        if (!sonVecinos(a, b))
            throw new IllegalArgumentException("No están conectados.");
        v(a).vecinos.get(b).peso = peso;
        v(b).vecinos.get(a).peso = peso;
    }

    /**
     * Regresa el vértice correspondiente el elemento recibido.
     * @param elemento el elemento del que queremos el vértice.
     * @throws NoSuchElementException si elemento no es elemento de la gráfica.
     * @return el vértice correspondiente el elemento recibido.
     */
    public VerticeGrafica<T> vertice(T elemento) {
        return v(elemento);
    }

    /**
     * Define el color del vértice recibido.
     * @param vertice el vértice al que queremos definirle el color.
     * @param color el nuevo color del vértice.
     * @throws IllegalArgumentException si el vértice no es válido.
     */
    public void setColor(VerticeGrafica<T> vertice, Color color) {
        if (vertice.getClass() == Vertice.class)
            setColorVertice((Vertice)vertice, color);
        else if (vertice.getClass() == Vecino.class)
            setColorVertice(((Vecino)vertice).vecino, color);
        else
            throw new IllegalArgumentException("Vértice inválido.");
    }

    /* Colorea un vértice. */
    private void setColorVertice(Vertice v, Color color) {
        if (!vertices.contiene(v.elemento))
            throw new IllegalArgumentException("No está en la gráfica");
        v.color = color;
    }

    /**
     * Nos dice si la gráfica es conexa.
     * @return <code>true</code> si la gráfica es conexa, <code>false</code> en
     *         otro caso.
     */
    public boolean esConexa() {
        int n = getElementos();
        int m = aristas;
        if (n <= 1)
            return true;
        if (m < n - 1)
            return false;
        if (m > ((n-1)*(n-2))/2)
            return true;

        limpiaColor();
        Iterator<Vertice> it = vertices.iterator();
        recorre(new Cola<Vertice>(), it.next(), v -> {}); 

        while (it.hasNext()) {
            if (!tieneColor(it.next())) {
                limpiaColor();
                return false;
            }
        }
        limpiaColor();
        return true;
    }

    /* Quita el color de todo vértice */
    private void limpiaColor() {
        for (Vertice v : vertices)
            v.color = Color.NINGUNO;
    }

    /* Regresa true si tiene color el vértice */
    private boolean tieneColor(Vertice v) {
        return v.color != Color.NINGUNO;
    }

    /* Pinta al vértice de negro */
    private void negro(Vertice v) {
        v.color = Color.NEGRO;
    }

    /**
     * Realiza la acción recibida en cada uno de los vértices de la gráfica, en
     * el orden en que fueron agregados.
     * @param accion la acción a realizar.
     */
    public void paraCadaVertice(AccionVerticeGrafica<T> accion) {
        for (Vertice v : vertices)
            accion.actua(v);
    }

    /**
     * Realiza la acción recibida en todos los vértices de la gráfica, en el
     * orden determinado por BFS, comenzando por el vértice correspondiente al
     * elemento recibido. Al terminar el método, todos los vértices tendrán
     * color {@link Color#NINGUNO}.
     * @param elemento el elemento sobre cuyo vértice queremos comenzar el
     *        recorrido.
     * @param accion la acción a realizar.
     * @throws NoSuchElementException si el elemento no está en la gráfica.
     */
    public void bfs(T elemento, AccionVerticeGrafica<T> accion) {
        limpiaColor();
        recorre(new Cola<Vertice>(), v(elemento), accion);
        limpiaColor();
    }

    /* Método para dfs y bfs como sólo difieren en la estructura auxiliar */
    private void 
    recorre(MeteSaca<Vertice> ms, Vertice v, AccionVerticeGrafica<T> accion) {
        negro(v);
        ms.mete(v);
        while (!ms.esVacia()) {
            Vertice u = ms.saca();
            accion.actua(u);
            for (Vecino vecino : u.vecinos) {
                if (!tieneColor(vecino.vecino)) {
                    ms.mete(vecino.vecino);
                    negro(vecino.vecino);
                }
            }
        }
    }

    /**
     * Realiza la acción recibida en todos los vértices de la gráfica, en el
     * orden determinado por DFS, comenzando por el vértice correspondiente al
     * elemento recibido. Al terminar el método, todos los vértices tendrán
     * color {@link Color#NINGUNO}.
     * @param elemento el elemento sobre cuyo vértice queremos comenzar el
     *        recorrido.
     * @param accion la acción a realizar.
     * @throws NoSuchElementException si el elemento no está en la gráfica.
     */
    public void dfs(T elemento, AccionVerticeGrafica<T> accion) {
        limpiaColor();
        recorre(new Pila<Vertice>(), v(elemento), accion);
        limpiaColor();
    }

    /**
     * Nos dice si la gráfica es vacía.
     * @return <code>true</code> si la gráfica es vacía, <code>false</code> en
     *         otro caso.
     */
    @Override public boolean esVacia() {
        return vertices.esVacia();
    }

    /**
     * Limpia la gráfica de vértices y aristas, dejándola vacía.
     */
    @Override public void limpia() {
        aristas = 0;
        vertices.limpia();
    }

    /**
     * Regresa una representación en cadena de la gráfica.
     * @return una representación en cadena de la gráfica.
     */
    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (Vertice v : vertices) {
            sb.append(v.elemento.toString());
            sb.append(", ");
        }
        sb.append("}, {");
        limpiaColor();
        for (Vertice u : vertices) {
            negro(u);
            for (Vecino v : u.vecinos)
                if (!tieneColor(v.vecino))
                    appendArista(sb, u, v.vecino);
        }
        sb.append("}");
        limpiaColor();
        return sb.toString();
    }

    /* Añade una arista a la cadena del StringBuilder */
    private void appendArista(StringBuilder sb, Vertice u, Vertice v) {
        sb.append("(");
        sb.append(u.elemento.toString());
        sb.append(", ");
        sb.append(v.elemento.toString());
        sb.append("), ");
    }

    /**
     * Nos dice si la gráfica es igual al objeto recibido.
     * @param objeto el objeto con el que hay que comparar.
     * @return <code>true</code> si la gráfica es igual al objeto recibido;
     *         <code>false</code> en otro caso.
     */
    @Override public boolean equals(Object objeto) {
        if (objeto == null || getClass() != objeto.getClass())
            return false;
        @SuppressWarnings("unchecked") Grafica<T> grafica = (Grafica<T>)objeto;
        if (getElementos() != grafica.getElementos())
            return false;
        if (aristas != grafica.aristas)
            return false;
        for (Vertice v : vertices)
            if (!grafica.contiene(v.elemento))
                return false;
        for (Vertice u : vertices) {
            for (Vertice v : grafica.vertices) {
                T a = u.elemento;
                T b = v.elemento;

                if (u.vecinos.contiene(b) && !v.vecinos.contiene(a))
                    return false;
            }
        }
        return true;
    }

    /**
     * Regresa un iterador para iterar la gráfica. La gráfica se itera en el
     * orden en que fueron agregados sus elementos.
     * @return un iterador para iterar la gráfica.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }

    /**
     * Calcula una trayectoria de distancia mínima entre dos vértices.
     * @param origen el vértice de origen.
     * @param destino el vértice de destino.
     * @return Una lista con vértices de la gráfica, tal que forman una
     *         trayectoria de distancia mínima entre los vértices <code>a</code> y
     *         <code>b</code>. Si los elementos se encuentran en componentes conexos
     *         distintos, el algoritmo regresa una lista vacía.
     * @throws NoSuchElementException si alguno de los dos elementos no está en
     *         la gráfica.
     */
    public Lista<VerticeGrafica<T>> trayectoriaMinima(T origen, T destino) {
        return trayectoria(origen, destino, (v, vu) -> {
            if (vu.vecino.distancia == Double.POSITIVE_INFINITY) {
                vu.vecino.distancia = v.distancia + 1.0;
                return true;
            }
            return false;
        }, (v, vu) -> doubleIguales(v.distancia-1.0, vu.vecino.distancia));
    }

    /**
     * Calcula la ruta de peso mínimo entre el elemento de origen y el elemento
     * de destino.
     * @param origen el vértice origen.
     * @param destino el vértice destino.
     * @return una trayectoria de peso mínimo entre el vértice <code>origen</code> y
     *         el vértice <code>destino</code>. Si los vértices están en componentes
     *         conexas distintas, regresa una lista vacía.
     * @throws NoSuchElementException si alguno de los dos elementos no está en
     *         la gráfica.
     */
    public Lista<VerticeGrafica<T>> dijkstra(T origen, T destino) {
        return trayectoria(origen, destino, (v, vu) -> {
            if (vu.vecino.distancia > v.distancia + vu.peso) {
                vu.vecino.distancia = v.distancia + vu.peso;
                return true;
            }
            return false;
        }, (v, vu) -> doubleIguales(v.distancia-vu.peso, vu.vecino.distancia));
    }

    /* Encuentra una trayectoria que minimiza alguna distancia. 
     * d: BuscadorCamino con quien se construyen las distancias
     * t: BuscadorCamino con quien se construye la trayectoria
     * */
    private Lista<VerticeGrafica<T>>
    trayectoria(T origen, T destino, BuscadorCamino<T> d, BuscadorCamino<T> t) {
        Vertice ini = v(origen);
        Vertice fin = v(destino);
        reiniciaDistancias(ini);

        MonticuloDijkstra<Vertice> m = monticulo();

        while (!m.esVacia()){
            Vertice v = m.elimina();
            if (v.distancia == Double.POSITIVE_INFINITY)
                break;
            for (Vecino vu : v.vecinos)
                if (d.seSiguen(v, vu))
                    m.reordena(vu.vecino);
        }

        return reconstruye(ini, fin, t);
    }

    /* Dados vértices con distancias, construye una (ini,fin)-trayectoria de
     * distancia mínima. */
    private Lista<VerticeGrafica<T>> 
    reconstruye(Vertice ini, Vertice fin, BuscadorCamino<T> buscador) {
        Lista<VerticeGrafica<T>> trayectoria = new Lista<VerticeGrafica<T>>();

        if (fin.distancia == Double.POSITIVE_INFINITY)
            return trayectoria;

        Vertice siguiente = fin;
        while (siguiente != ini) {
            trayectoria.agregaInicio(siguiente);
            for (Vecino vu : siguiente.vecinos) {
                if (!buscador.seSiguen(siguiente, vu))
                    continue;
                siguiente = vu.vecino;
                break;
            }
        }
        trayectoria.agregaInicio(ini);
        return trayectoria;
    }

    /* Reinicia las distancias para correr un algoritmo que genera trayectorias
     * que minimizan alguna distancia. */
    private void reiniciaDistancias(Vertice inicial) {
        for (Vertice v : vertices)
            v.distancia = Double.POSITIVE_INFINITY;
        inicial.distancia = 0;
    }

    /* Decide qué montículo utilizar para los algoritmos que generan 
     * trayectorias que minimizan alguna distancia. */
    private MonticuloDijkstra<Vertice> monticulo() {
        long n = getElementos();
        if (aristas <= (n*(n-1))/2 - n)
            return new MonticuloMinimo<Vertice>(vertices, getElementos());
        return new MonticuloArreglo<Vertice>(vertices, getElementos());
    }

    /* Compara doubles, pues una comparación directa no es muy buena idea. */
    private boolean doubleIguales(double a, double b) {
        if (a == b) return true;
        double epsilon = 128.0 * Math.ulp(1.0);
        double delta = Math.abs(a-b);
        double tamaño = Math.min(Math.abs(a+b), Double.MAX_VALUE);
        return delta < Math.max(Double.MIN_VALUE, epsilon * tamaño);
    }
}
