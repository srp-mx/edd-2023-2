package mx.unam.ciencias.edd.test;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import mx.unam.ciencias.edd.CacheLRU;
import mx.unam.ciencias.edd.Cola;
import mx.unam.ciencias.edd.IteradorLista;
import mx.unam.ciencias.edd.Lista;
import mx.unam.ciencias.edd.MeteSaca;
import mx.unam.ciencias.edd.Pila;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

/**
 * Clase para pruebas unitarias de la clase {@link CacheLRU}
 */
public class TestCacheLRU {
    
    /** Expiración para que ninguna prueba tarde más de 5 segundos. */
    @Rule public Timeout expiracion = Timeout.seconds(5);

    /* Generador de números aleatorios. */
    private Random random;
    /* Número total de elementos. */
    private int total;
    /* Entradas. */
    private Entrada[] entradas;
    /* La caché. */
    private CacheLRU<Integer, Entrada> cache;

    /* Clase para probar el orden de los accesos. */
    private class Entrada {
        /* Último tiempo en el que se accedió a la entrada. */
        private long ultimoAcceso;
        /* Indice en el arreglo. */
        private int indice;

        /* Constructor */
        public Entrada(int indice) {
            this.indice = indice;
        }

        /* Actualiza el último acceso. */
        public Entrada toca() {
            ultimoAcceso = System.nanoTime();
            return this;
        }

        /* Para probar el toString. */
        @Override public String toString() {
            return ""+indice;
        }
    }

    /**
     * Crea una caché para cada prueba.
     */
    public TestCacheLRU() {
        int N = 64;
        random = new Random();
        total = N + random.nextInt(N);
        cache = new CacheLRU<>(total);
        entradas = new Entrada[total*2 - 1];
        for (int i = 0; i < entradas.length; i++)
            entradas[i] = new Entrada(i);
    }

    /* Valida la caché. */
    private void validaCache() {
        long a = Long.MAX_VALUE;
        int n = 0;
        for (Entrada e : cache) {
            Assert.assertTrue(a > e.ultimoAcceso);
            a = e.ultimoAcceso;
            n++;
        }
        Assert.assertTrue(n == cache.getElementos());
        Assert.assertTrue(n <= total);
    }

    /**
     * Prueba unitaria para {@link CacheLRU#CacheLRU}.
     */
    @Test public void testConstructor() {
        Assert.assertTrue(cache.esVacia());
        Assert.assertTrue(cache.getElementos() == 0);
        validaCache();
        try {
            cache = new CacheLRU<>(0);
            cache = new CacheLRU<>(1);
            cache = new CacheLRU<>(-2);
            cache = new CacheLRU<>(-total);
            cache = new CacheLRU<>(-random.nextInt(100));
            Assert.fail();
        } catch (IllegalArgumentException iae) { }
        cache = new CacheLRU<>(2);
        validaCache();
        cache = new CacheLRU<>(total);
        validaCache();
    }

    /**
     * Prueba unitaria para {@link CacheLRU#getCapacidad}.
     */
    @Test public void testCapacidad() {
        Assert.assertTrue(total == cache.getCapacidad());
    }

    /**
     * Prueba unitaria para {@link CacheLRU#esVacia}
     */
    @Test public void testEsVacia() {
        Assert.assertTrue(cache.esVacia());
        int entrada = random.nextInt(entradas.length);
        int ultimaCantidad = 0;
        for (int i = 0; i < total*2; i++) {
            cache.agrega(entrada, entradas[entrada].toca());
            entrada = random.nextInt(entradas.length);
            Assert.assertFalse(cache.esVacia());
            Assert.assertTrue(cache.getElementos() >= ultimaCantidad);
            ultimaCantidad = cache.getElementos();
        }
        Assert.assertTrue(total == cache.getElementos());
        for (int i = 0; i < total; i++) {
            Assert.assertFalse(cache.esVacia());
            cache.eliminaMRU();
        }
        Assert.assertTrue(cache.esVacia());
    }

    /**
     * Prueba unitaria para {@link CacheLRU#contiene}
     */
    @Test public void testContiene() {
        Assert.assertFalse(cache.contiene(0));
        cache.agrega(0, entradas[0].toca());       
        Assert.assertTrue(cache.contiene(0));
        for (int i = 1; i < total; i++) {
            cache.agrega(i, entradas[i].toca());
            Assert.assertTrue(cache.contiene(0));
            validaCache();
        }
        cache.agrega(total, entradas[total].toca());
        Assert.assertFalse(cache.contiene(0));
        validaCache();
    }

    /**
     * Prueba unitaria para {@link CacheLRU#get}
     */
    @Test public void testGet() {
        Lista<Entrada> elems = new Lista<>();
        for (int i = 0; i < total; i++) {
            Entrada e = entradas[random.nextInt(entradas.length)];
            if (elems.contiene(e)) {
                i--;
                continue;
            }
            elems.agrega(e);
        }
        for (int i = 0; i < total*2; i++) {
            Entrada e = entradas[random.nextInt(entradas.length)];
            cache.agrega(e.indice, e.toca());
            validaCache();
        }

        IteradorLista<Entrada> it = elems.iteradorLista();
        it.end();
        while (it.hasPrevious()) {
            Entrada e = it.previous();
            cache.agrega(e.indice, e.toca());
            validaCache();
        }

        while (!elems.esVacia()) {
            Entrada e = elems.get(random.nextInt(elems.getLongitud()));
            elems.elimina(e);
            Assert.assertTrue(cache.get(e.toca().indice) == e);
            validaCache();
        }
    }

    /**
     * Prueba unitaria para {@link CacheLRU#agrega}
     */
    @Test public void testAgrega() {
        validaCache();
        int entrada = random.nextInt(entradas.length);
        for (int i = 0; i < total*2; i++) {
            cache.agrega(entrada, entradas[entrada].toca());
            entrada = random.nextInt(entradas.length);
            validaCache();
        }
        validaCache();
        Entrada noContenida = null;
        Entrada lru = cache.getLRU();
        for (int i = 0; i < entradas.length; i++) {
            if (!cache.contiene(i)) {
                noContenida = entradas[i];
                break;
            }
        }
        Assert.assertTrue(cache.contiene(lru.indice));
        Assert.assertNotNull(noContenida);
        Assert.assertFalse(cache.contiene(noContenida.indice));
        cache.agrega(noContenida.indice, noContenida.toca());
        Assert.assertFalse(cache.contiene(lru.indice));
        Assert.assertTrue(cache.contiene(noContenida.indice));
        Assert.assertTrue(cache.getMRU() == noContenida);
        validaCache();
    }

    /**
     * Prueba unitaria para {@link CacheLRU#elimina}
     */
    @Test public void testElimina() {
        validaCache();
        int entrada = random.nextInt(entradas.length);
        for (int i = 0; i < total*2; i++) {
            cache.agrega(entrada, entradas[entrada].toca());
            entrada = random.nextInt(entradas.length);
            validaCache();
        }
        validaCache();
    }

    /**
     * Prueba unitaria para {@link CacheLRU#eliminaMRU}
     */
    @Test public void testEliminaMRU() {
        eliminaMruLru(new Pila<Entrada>());
    }

    private void eliminaMruLru(MeteSaca<Entrada> meteSaca) {
        for (int i = 0; i < total; i++) {
            int idx = random.nextInt(entradas.length);
            if (cache.contiene(idx)) {
                i--;
                continue;
            }
            meteSaca.mete(entradas[idx]);
            cache.agrega(idx, entradas[idx].toca());
            validaCache();
        }

        while (!meteSaca.esVacia()) {
            if (meteSaca instanceof Pila<?>)
                Assert.assertTrue(meteSaca.saca() == cache.eliminaMRU());
            else
                Assert.assertTrue(meteSaca.saca() == cache.eliminaLRU());
            validaCache();
        }
    }

    /**
     * Prueba unitaria para {@link CacheLRU#eliminaLRU}
     */
    @Test public void testEliminaLRU() {
        eliminaMruLru(new Cola<Entrada>());
    }

    /**
     * Prueba unitaria para {@link CacheLRU#limpia}
     */
    @Test public void testLimpia() {
        cache.limpia();
        Assert.assertTrue(cache.esVacia());
        for (int i = 0; i < total*2; i++) {
            int idx = random.nextInt(entradas.length);
            cache.agrega(idx, entradas[idx].toca());
            Assert.assertFalse(cache.esVacia());
        }
        cache.limpia();
        Assert.assertTrue(cache.esVacia());
    }

    /**
     * Prueba unitaria para {@link CacheLRU#copia}
     */
    @Test public void testCopia() {
        for (int i = 0; i < total*2; i++) {
            int idx = random.nextInt(entradas.length);
            cache.agrega(idx, entradas[idx].toca());
            Assert.assertFalse(cache.esVacia());
        }
        CacheLRU<Integer, Entrada> cache2 = cache.copia();
        validaCache();
        Assert.assertFalse(cache == cache2);
        Assert.assertTrue(cache.equals(cache2));
        while (!cache.esVacia()) {
            Assert.assertTrue(cache.eliminaMRU() == cache2.eliminaMRU());
        }
    }

    /**
     * Prueba unitaria para {@link CacheLRU#getMRU}
     */
    @Test public void testGetMRU() {
        try {
            cache.getMRU();
            Assert.fail();
        } catch(NoSuchElementException nsee) {}

        Lista<Entrada> l = new Lista<>();
        for (int i = 0; i < total*2; i++) {
            int idx = random.nextInt(entradas.length);
            cache.agrega(idx, entradas[idx].toca());
            Assert.assertTrue(cache.getMRU() == entradas[idx]);
            if (l.contiene(entradas[idx]))
                l.elimina(entradas[idx]);
            l.agregaInicio(entradas[idx]);
            if (l.getLongitud() > total)
                l.eliminaUltimo();
            validaCache();
        }

        while (!l.esVacia()) {
            Entrada e = l.get(random.nextInt(l.getLongitud()));
            l.elimina(e);
            cache.get(e.toca().indice);
            Assert.assertTrue(cache.getMRU() == e);
            validaCache();
        }
    }

    /**
     * Prueba unitaria para {@link CacheLRU#getLRU}
     */
    @Test public void testGetLRU() {
        try {
            cache.getLRU();
            Assert.fail();
        } catch(NoSuchElementException nsee) {}

        Lista<Entrada> l = new Lista<>();
        for (int i = 0; i < total*2; i++) {
            int idx = random.nextInt(entradas.length);
            cache.agrega(idx, entradas[idx].toca());
            if (l.contiene(entradas[idx]))
                l.elimina(entradas[idx]);
            l.agregaInicio(entradas[idx]);
            if (l.getLongitud() > total)
                l.eliminaUltimo();
            validaCache();
            Assert.assertTrue(cache.getLRU() == l.getUltimo());
        }

        while (l.getLongitud() > 1) {
            Entrada e = l.eliminaUltimo();
            cache.get(e.toca().indice);
            Assert.assertTrue(cache.getLRU() == l.getUltimo());
            validaCache();
        }
    }

    /**
     * Prueba unitaria para {@link CacheLRU#toString}
     */
    @Test public void testToString() {
        String str = "[]";
        Assert.assertTrue(str.equals(cache.toString()));
        int ini = random.nextInt(total-2);
        for (int i = ini; i < ini+total; i++) {
            str = "[";
            for (int j = i; j >= ini; j--) {
                str += String.format("'%d': '%d'", j, j);
                if (j != ini)
                    str += ", ";
            }
            str += "]";
            cache.agrega(i, entradas[i]);
            Assert.assertTrue(str.equals(cache.toString()));
        }
    }

    /**
     * Prueba unitaria para {@link CacheLRU#equals}
     */
    @Test public void testEquals() {
        Lista<Entrada> l = new Lista<>();
        CacheLRU<Integer, Entrada> cache2 = new CacheLRU<>(total);
        Assert.assertTrue(cache.equals(cache2));
        for (int i = 0; i < total*2; i++) {
            int idx = random.nextInt(entradas.length);
            cache.agrega(idx, entradas[idx].toca());
            cache2.agrega(idx, entradas[idx].toca());
            Assert.assertTrue(cache.equals(cache2));
            if (l.contiene(entradas[idx]))
                l.elimina(entradas[idx]);
            l.agregaInicio(entradas[idx]);
            if (l.getLongitud() > total)
                l.eliminaUltimo();
            validaCache();
        }

        int e = l.get(random.nextInt(l.getLongitud()-1)+1).indice;
        cache.get(e);
        Assert.assertFalse(cache.equals(cache2));
        cache2.get(e);
        Assert.assertTrue(cache.equals(cache2));
    }

    /**
     * Prueba unitaria para {@link IteradorLista#hasNext}
     * a través del método {@link CacheLRU#iteradorLlaves}
     */
    @Test public void testIteradorLlavesHasNext() {
        Iterator<Integer> iterador = cache.iteradorLlaves();
        Assert.assertFalse(iterador.hasNext());
        cache.agrega(0, entradas[0].toca());
        iterador = cache.iteradorLlaves();
        Assert.assertTrue(iterador.hasNext());
        for (int i = 1; i < total; i++)
            cache.agrega(i, entradas[i].toca());
        iterador = cache.iteradorLlaves();
        for (int i = 0; i < total-1; i++)
            iterador.next();
        Assert.assertTrue(iterador.hasNext());
        iterador.next();
        Assert.assertFalse(iterador.hasNext());
        validaCache();
    }

    /**
     * Prueba unitaria para {@link IteradorLista#next}
     * a través del método {@link CacheLRU#iteradorLlaves}
     */
    @Test public void testIteradorLlavesNext() {
        Iterator<Integer> iterador = cache.iteradorLlaves();
        try {
            iterador.next();
            Assert.fail();
        } catch (NoSuchElementException nsee) {}
        for (int i = 0; i < total; i++)
            cache.agrega(i, entradas[i].toca());
        iterador = cache.iteradorLlaves();
        for (int i = 0; i < total; i++)
            Assert.assertTrue(iterador.next() == total - 1 - i);
        try {
            iterador.next();
            Assert.fail();
        } catch (NoSuchElementException nsee) {}
        validaCache();
    }

    /**
     * Prueba unitaria para {@link IteradorLista#previous}
     * a través del método {@link CacheLRU#iteradorListaLlaves}
     */
    @Test public void testIteradorLlavesPrevious() {
        IteradorLista<Integer> iterador = cache.iteradorListaLlaves();
        try {
            iterador.previous();
            Assert.fail();
        } catch (NoSuchElementException nsee) {}
        for (int i = 0; i < total; i++)
            cache.agrega(i, entradas[i].toca());
        iterador = cache.iteradorListaLlaves();
        iterador.end();
        for (int i = 0; i < total; i++)
            Assert.assertTrue(iterador.previous() == i);
        try {
            iterador.previous();
            Assert.fail();
        } catch (NoSuchElementException nsee) {}
        validaCache();
    }

    /**
     * Prueba unitaria para {@link IteradorLista#hasPrevious}
     * a través del método {@link CacheLRU#iteradorListaLlaves}
     */
    @Test public void testIteradorLlavesHasPrevious() {
        IteradorLista<Integer> iterador = cache.iteradorListaLlaves();
        Assert.assertFalse(iterador.hasPrevious());
        cache.agrega(0, entradas[0].toca());
        iterador = cache.iteradorListaLlaves();
        iterador.next();
        Assert.assertTrue(iterador.hasPrevious());
        for (int i = 1; i < total; i++)
            cache.agrega(i, entradas[i].toca());
        iterador = cache.iteradorListaLlaves();
        iterador.next();
        Assert.assertTrue(iterador.hasPrevious());
        iterador.previous();
        Assert.assertFalse(iterador.hasPrevious());
        iterador.end();
        Assert.assertTrue(iterador.hasPrevious());
    }

    /**
     * Prueba unitaria para {@link IteradorLista#start}
     * a través del método {@link CacheLRU#iteradorListaLlaves}
     */
    @Test public void testIteradorLlavesStart() {
        for (int i = 0; i < total; i++)
            cache.agrega(i, entradas[i].toca());
        IteradorLista<Integer> iterador = cache.iteradorListaLlaves();
        while (iterador.hasNext())
            iterador.next();
        Assert.assertTrue(iterador.hasPrevious());
        iterador.start();
        Assert.assertFalse(iterador.hasPrevious());
        Assert.assertTrue(iterador.hasNext());
        Assert.assertTrue(iterador.next() == total-1);
        validaCache();
    }

    /**
     * Prueba unitaria para {@link IteradorLista#end}
     * a través del método {@link CacheLRU#iteradorListaLlaves}
     */
    @Test public void testIteradorLlavesEnd() {
        for (int i = 0; i < total; i++)
            cache.agrega(i, entradas[i].toca());
        IteradorLista<Integer> iterador = cache.iteradorListaLlaves();
        iterador.end();
        Assert.assertFalse(iterador.hasNext());
        Assert.assertTrue(iterador.hasPrevious());
        Assert.assertTrue(iterador.previous() == 0);
        validaCache();
    }

    /**
     * Prueba unitaria para {@link IteradorLista#hasNext}
     * a través del método {@link CacheLRU#iterator}
     */
    @Test public void testIteradorHasNext() {
        Iterator<Entrada> iterador = cache.iterator();
        Assert.assertFalse(iterador.hasNext());
        cache.agrega(0, entradas[0].toca());
        iterador = cache.iterator();
        Assert.assertTrue(iterador.hasNext());
        for (int i = 1; i < total; i++)
            cache.agrega(i, entradas[i].toca());
        iterador = cache.iterator();
        for (int i = 0; i < total-1; i++)
            iterador.next();
        Assert.assertTrue(iterador.hasNext());
        iterador.next();
        Assert.assertFalse(iterador.hasNext());
        validaCache();
    }

    /**
     * Prueba unitaria para {@link IteradorLista#next}
     * a través del método {@link CacheLRU#iterator}
     */
    @Test public void testIteradorNext() {
        Iterator<Entrada> iterador = cache.iterator();
        try {
            iterador.next();
            Assert.fail();
        } catch (NoSuchElementException nsee) {}
        for (int i = 0; i < total; i++)
            cache.agrega(i, entradas[i].toca());
        iterador = cache.iterator();
        for (int i = 0; i < total; i++)
            Assert.assertTrue(iterador.next() == entradas[total - 1 - i]);
        try {
            iterador.next();
            Assert.fail();
        } catch (NoSuchElementException nsee) {}
        validaCache();
    }

    /**
     * Prueba unitaria para {@link IteradorLista#previous}
     * a través del método {@link CacheLRU#iteradorLista}
     */
    @Test public void testIteradorPrevious() {
        IteradorLista<Entrada> iterador = cache.iteradorLista();
        try {
            iterador.previous();
            Assert.fail();
        } catch (NoSuchElementException nsee) {}
        for (int i = 0; i < total; i++)
            cache.agrega(i, entradas[i].toca());
        iterador = cache.iteradorLista();
        iterador.end();
        for (int i = 0; i < total; i++)
            Assert.assertTrue(iterador.previous() == entradas[i]);
        try {
            iterador.previous();
            Assert.fail();
        } catch (NoSuchElementException nsee) {}
        validaCache();
    }

    /**
     * Prueba unitaria para {@link IteradorLista#hasPrevious}
     * a través del método {@link CacheLRU#iteradorLista}
     */
    @Test public void testIteradorHasPrevious() {
        IteradorLista<Entrada> iterador = cache.iteradorLista();
        Assert.assertFalse(iterador.hasPrevious());
        cache.agrega(0, entradas[0].toca());
        iterador = cache.iteradorLista();
        iterador.next();
        Assert.assertTrue(iterador.hasPrevious());
        for (int i = 1; i < total; i++)
            cache.agrega(i, entradas[i].toca());
        iterador = cache.iteradorLista();
        iterador.next();
        Assert.assertTrue(iterador.hasPrevious());
        iterador.previous();
        Assert.assertFalse(iterador.hasPrevious());
        iterador.end();
        Assert.assertTrue(iterador.hasPrevious());
    }

    /**
     * Prueba unitaria para {@link IteradorLista#start}
     * a través del método {@link CacheLRU#iteradorLista}
     */
    @Test public void testIteradorStart() {
        for (int i = 0; i < total; i++)
            cache.agrega(i, entradas[i].toca());
        IteradorLista<Entrada> iterador = cache.iteradorLista();
        while (iterador.hasNext())
            iterador.next();
        Assert.assertTrue(iterador.hasPrevious());
        iterador.start();
        Assert.assertFalse(iterador.hasPrevious());
        Assert.assertTrue(iterador.hasNext());
        Assert.assertTrue(iterador.next() == entradas[total-1]);
        validaCache();
    }

    /**
     * Prueba unitaria para {@link IteradorLista#end}
     * a través del método {@link CacheLRU#iteradorLista}
     */
    @Test public void testIteradorEnd() {
        for (int i = 0; i < total; i++)
            cache.agrega(i, entradas[i].toca());
        IteradorLista<Entrada> iterador = cache.iteradorLista();
        iterador.end();
        Assert.assertFalse(iterador.hasNext());
        Assert.assertTrue(iterador.hasPrevious());
        Assert.assertTrue(iterador.previous() == entradas[0]);
        validaCache();
    }

    /**
     * Prueba unitaria para {@link CacheLRU#tryGet}.
     */
    @Test public void testTryGet() {
        final boolean[] miss = new boolean[total];
        for (int i = 0; i < total; i++) {
            final int j = i;
            miss[j] = false;
            cache.tryGet(j, () -> {
                miss[j] = true;
                return entradas[j].toca();
            });
            Assert.assertTrue(miss[j]);
        }
        for (int i = 0; i < total; i++) {
            final int j = i;
            cache.tryGet(j, () -> {
                Assert.fail();
                return null;
            });
        }
    }
}
