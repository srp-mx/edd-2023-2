package mx.unam.ciencias.edd;

import java.util.Comparator;

/**
 * Clase para ordenar y buscar arreglos genéricos.
 */
public class Arreglos {

    /* Constructor privado para evitar instanciación. */
    private Arreglos() {}

    /**
     * Ordena el arreglo recibido usando QuickSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo el arreglo a ordenar.
     * @param comparador el comparador para ordenar el arreglo.
     */
    public static <T> void
    quickSort(T[] arreglo, Comparator<T> comparador) {
        auxiliarQuickSort(arreglo, comparador, 0, arreglo.length - 1);
    }

    /**
     * Ordena el subarreglo recibido usando QuickSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo el arreglo a ordenar.
     * @param comparador el comparador para ordenar el arreglo.
     * @param ini el indice (inclusivo) del inicio del subarreglo.
     * @param fin el indice (exclusivo) del fin del subarreglo.
     */
    private static <T> void
    auxiliarQuickSort(T[] arreglo, Comparator<T> comparador, int ini, int fin) {
        if (fin <= ini)
            return;

        int i = ini + 1;
        int j = fin;

        while (i < j) {
            T pivote = arreglo[ini];
            int compI = comparador.compare(arreglo[i], pivote);
            int compJ = comparador.compare(arreglo[j], pivote);
            if (compI > 0 && compJ < 0)
                intercambia(arreglo, i++, j--);
            else if (compI <= 0)
                i++;
            else
                j--;
        }

        if (comparador.compare(arreglo[i], arreglo[ini]) > 0)
            i--;

        intercambia(arreglo, ini, i);
        auxiliarQuickSort(arreglo, comparador, ini, i - 1);
        auxiliarQuickSort(arreglo, comparador, i+1, fin);
    }

    /**
     * Ordena el arreglo recibido usando QickSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo un arreglo cuyos elementos son comparables.
     */
    public static <T extends Comparable<T>> void
    quickSort(T[] arreglo) {
        quickSort(arreglo, (a, b) -> a.compareTo(b));
    }

    /**
     * Ordena el arreglo recibido usando SelectionSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo el arreglo a ordenar.
     * @param comparador el comparador para ordernar el arreglo.
     */
    public static <T> void
    selectionSort(T[] arreglo, Comparator<T> comparador) {
        for (int i = 0; i < arreglo.length; i++) {
            int indiceMin = i;
            for (int j = i+1; j < arreglo.length; j++) {
                if (comparador.compare(arreglo[j], arreglo[indiceMin]) <= 0)
                    indiceMin = j;
            }
            intercambia(arreglo, i, indiceMin);
        }
    }

    /* Intercambia dos entradas de un arreglo */
    private static <T> void
    intercambia(T[] arreglo, int i, int j) {
        T temp = arreglo[i];
        arreglo[i] = arreglo[j];
        arreglo[j] = temp;
    }

    /**
     * Ordena el arreglo recibido usando SelectionSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo un arreglo cuyos elementos son comparables.
     */
    public static <T extends Comparable<T>> void
    selectionSort(T[] arreglo) {
        selectionSort(arreglo, (a, b) -> a.compareTo(b));
    }

    /**
     * Hace una búsqueda binaria del elemento en el arreglo. Regresa el índice
     * del elemento en el arreglo, o -1 si no se encuentra.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo el arreglo dónde buscar.
     * @param elemento el elemento a buscar.
     * @param comparador el comparador para hacer la búsqueda.
     * @return el índice del elemento en el arreglo, o -1 si no se encuentra.
     */
    public static <T> int
    busquedaBinaria(T[] arreglo, T elemento, Comparator<T> comparador) {
        int ini = 0;
        int fin = arreglo.length - 1;
        
        while (fin - ini >= 0) {
            int pm = ini + (fin-ini)/2;
            int comparacion = comparador.compare(arreglo[pm], elemento);
            if (comparacion == 0)
                return pm;
            else if (comparacion > 0)
                fin = pm - 1;
            else
                ini = pm + 1;
        }
        return -1;
    }

    /**
     * Hace una búsqueda binaria del elemento en el arreglo. Regresa el índice
     * del elemento en el arreglo, o -1 si no se encuentra.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo un arreglo cuyos elementos son comparables.
     * @param elemento el elemento a buscar.
     * @return el índice del elemento en el arreglo, o -1 si no se encuentra.
     */
    public static <T extends Comparable<T>> int
    busquedaBinaria(T[] arreglo, T elemento) {
        return busquedaBinaria(arreglo, elemento, (a, b) -> a.compareTo(b));
    }
}
