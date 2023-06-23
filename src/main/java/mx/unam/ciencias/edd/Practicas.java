package mx.unam.ciencias.edd;

import java.text.NumberFormat;
import java.util.Random;

/**
 * Prácticas: Estructuras de datos
 */
public class Practicas {

    private static String cadena;

    /* Imprime el uso del programa y lo termina. */
    private static void uso() {
        System.err.println("Uso: java -jar practicas.jar N");
        System.exit(1);
    }

    public static void main(String[] args) {
        if (args.length != 1)
            uso();

        int N = -1;
        try {
            N = Integer.parseInt(args[0]);
        } catch (NumberFormatException nfe) {
            uso();
        }

        if (N < 0)
            uso();

        Random random = new Random();
        long tiempoInicial, tiempoTotal;
        NumberFormat nf = NumberFormat.getIntegerInstance();

        Integer[] arreglo = new Integer[N];
        for (int i = 0; i < N; i++)
            arreglo[i] = random.nextInt(N);

        int b = arreglo[N/2];

        ArbolBinarioOrdenado<Integer> abo = new ArbolBinarioOrdenado<Integer>();
        tiempoInicial = System.nanoTime();
        for (int i = 0; i < N; i++)
            abo.agrega(arreglo[i]);
        tiempoTotal = System.nanoTime() - tiempoInicial;
        System.out.printf("%2.9f segundos en llenar un árbol " +
                          "binario ordenado con %s elementos.\n",
                          (tiempoTotal/1000000000.0), nf.format(N));

        tiempoInicial = System.nanoTime();
        ArbolRojinegro<Integer> arn = new ArbolRojinegro<Integer>();
        for (int i = 0; i < N; i++)
            arn.agrega(arreglo[i]);
        tiempoTotal = System.nanoTime() - tiempoInicial;
        System.out.printf("%2.9f segundos en llenar un árbol " +
                          "rojinegro con %s elementos.\n",
                          (tiempoTotal/1000000000.0), nf.format(N));

        tiempoInicial = System.nanoTime();
        ArbolAVL<Integer> avl = new ArbolAVL<Integer>();
        for (int i = 0; i < N; i++)
            avl.agrega(arreglo[i]);
        tiempoTotal = System.nanoTime() - tiempoInicial;
        System.out.printf("%2.9f segundos en llenar un árbol " +
                          "AVL con %s elementos.\n",
                          (tiempoTotal/1000000000.0), nf.format(N));

        Diccionario<Integer, Integer> diccionario =
            new Diccionario<Integer, Integer>(N);
        tiempoInicial = System.nanoTime();
        for (int i = 0; i < N; i++)
            diccionario.agrega(arreglo[i], arreglo[i]);
        tiempoTotal = System.nanoTime() - tiempoInicial;
        System.out.printf("%2.9f segundos en llenar un diccionario " +
                          "con %s elementos.\n",
                          (tiempoTotal/1000000000.0), nf.format(N));

        Conjunto<Integer> conjunto = new Conjunto<Integer>(N);
        tiempoInicial = System.nanoTime();
        for (int i = 0; i < N; i++)
            conjunto.agrega(arreglo[i]);
        tiempoTotal = System.nanoTime() - tiempoInicial;
        System.out.printf("%2.9f segundos en llenar un conjunto " +
                          "con %s elementos.\n",
                          (tiempoTotal/1000000000.0), nf.format(N));

        tiempoInicial = System.nanoTime();
        abo.contiene(b);
        tiempoTotal = System.nanoTime() - tiempoInicial;
        System.out.printf("%2.9f segundos en buscar en un árbol " +
                          "binario ordenado con %s elementos.\n",
                          (tiempoTotal/1000000000.0), nf.format(N));

        tiempoInicial = System.nanoTime();
        arn.contiene(b);
        tiempoTotal = System.nanoTime() - tiempoInicial;
        System.out.printf("%2.9f segundos en buscar en un árbol " +
                          "rojinegro con %s elementos.\n",
                          (tiempoTotal/1000000000.0), nf.format(N));
        tiempoInicial = System.nanoTime();
        avl.contiene(b);
        tiempoTotal = System.nanoTime() - tiempoInicial;
        System.out.printf("%2.9f segundos en buscar en un árbol " +
                          "AVL con %s elementos.\n",
                          (tiempoTotal/1000000000.0), nf.format(N));

        tiempoInicial = System.nanoTime();
        diccionario.contiene(b);
        tiempoTotal = System.nanoTime() - tiempoInicial;
        System.out.printf("%2.9f segundos en buscar en un " +
                          "diccionario con %s elementos.\n",
                          (tiempoTotal/1000000000.0), nf.format(N));

        tiempoInicial = System.nanoTime();
        conjunto.contiene(b);
        tiempoTotal = System.nanoTime() - tiempoInicial;
        System.out.printf("%2.9f segundos en buscar en un " +
                          "conjunto con %s elementos.\n",
                          (tiempoTotal/1000000000.0), nf.format(N));

        /*       3
         *    b─────d
         *  1╱│╲    │╲1
         *  ╱ │ ╲1  │ ╲
         * a 2│  ╲  │2 f
         *  ╲ │   ╲ │ ╱
         *  2╲│    ╲│╱1
         *    c─────e
         *       1           */
        Grafica<String> grafica = new Grafica<String>();
        grafica.agrega("a");
        grafica.agrega("b");
        grafica.agrega("c");
        grafica.agrega("d");
        grafica.agrega("e");
        grafica.agrega("f");

        grafica.conecta("a", "b", 1);
        grafica.conecta("a", "c", 2);
        grafica.conecta("b", "c", 2);
        grafica.conecta("b", "d", 3);
        grafica.conecta("b", "e", 1);
        grafica.conecta("c", "e", 1);
        grafica.conecta("d", "e", 2);
        grafica.conecta("d", "f", 1);
        grafica.conecta("e", "f", 1);

        System.out.println(grafica);

        /* BFS */
        grafica.paraCadaVertice(v -> grafica.setColor(v, Color.ROJO));
        Cola<VerticeGrafica<String>> c = new Cola<VerticeGrafica<String>>();
        VerticeGrafica<String> vertice = grafica.vertice("a");
        grafica.setColor(vertice, Color.NEGRO);
        c.mete(vertice);
        cadena = "BFS 1: ";
        while (!c.esVacia()) {
            vertice = c.saca();
            cadena += vertice.get() + ", ";
            for (VerticeGrafica<String> vecino : vertice.vecinos()) {
                if (vecino.getColor() == Color.ROJO) {
                    grafica.setColor(vecino, Color.NEGRO);
                    c.mete(vecino);
                }
            }
        }
        System.out.println(cadena);

        /* BFS de la clase */
        cadena = "BFS 2: ";
        grafica.bfs("a", v -> cadena += v.get() + ", ");
        System.out.println(cadena);

        /* DFS */
        grafica.paraCadaVertice(v -> grafica.setColor(v, Color.ROJO));
        Pila<VerticeGrafica<String>> p = new Pila<VerticeGrafica<String>>();
        vertice = grafica.vertice("a");
        grafica.setColor(vertice, Color.NEGRO);
        p.mete(vertice);
        cadena = "DFS 1: ";
        while (!p.esVacia()) {
            vertice = p.saca();
            cadena += vertice.get() + ", ";
            for (VerticeGrafica<String> vecino : vertice.vecinos()) {
                if (vecino.getColor() == Color.ROJO) {
                    grafica.setColor(vecino, Color.NEGRO);
                    p.mete(vecino);
                }
            }
        }
        System.out.println(cadena);

        /* DFS de la clase */
        cadena = "DFS 2: ";
        grafica.dfs("a", v -> cadena += v.get() + ", ");
        System.out.println(cadena);

        /* Trayectoria mínima */
        Lista<VerticeGrafica<String>> trayectoria =
            grafica.trayectoriaMinima("a", "f");
        String s = "Trayectoría mínima: ";
        for (VerticeGrafica<String> v : trayectoria)
            s += v.get() + ", ";
        System.out.println(s);

        /* Dijkstra */
        Lista<VerticeGrafica<String>> dijkstra =
            grafica.dijkstra("a", "f");
        s = "Dijkstra: ";
        for (VerticeGrafica<String> v : dijkstra)
            s += v.get() + ", ";
        System.out.println(s);
    }
}
