Estructuras de Datos 2023-2
===========================

Prácticas de laboratorio
------------------------

### Estructuras y algunos métodos interesantes

* Lista Doblemente Ligada
    * Merge Sort
* Arreglo
    * Quick Sort
    * Selection Sort
    * Búsqueda binaria
* Pila
* Cola
* Árbol Binario Completo
    * Búsqueda por amplitud
* Montículo Mínimo
    * Heap sort
* Árbol Binario Ordenado
    * Búsqueda por profundidad
        * En orden
        * Post orden
        * Pre orden
* Árbol Rojinegro
* Árbol AVL
* Diccionario
* Conjunto
* Gráfica con Diccionario de Adyacencias
    * Búsqueda por profundidad
    * Búsqueda por amplitud
    * Trayectoria mínima
    * Dijkstra

### Uso

Compilar:

```
$ mvn compile
```

Pruebas unitarias:

```
$ mvn test
```

Programa de demostración:

```
$ mvn install
...
$ java -jar target/practicas.jar N
```

Donde `N` es un número entero.

### Repositorio

Pueden clonar la práctica con el siguiente comando:

```
$ git clone https://github.com/srp-mx/edd-2023-2-edd.git
```

### Documentación

La documentación generada por JavaDoc la pueden consultar aquí:

```
mvn javadoc:javadoc
...
xdg-open target/site/apidocs/index.html
```

### Libro

[El
libro](https://tienda.fciencias.unam.mx/es/home/437-estructuras-de-datos-con-java-moderno-9786073009157.html).

### Temario

1. Introducción (semana 1, sesión 1)
2. Genéricos (semana 1, sesión 2)
    1. Tipos genéricos
    2. Borradura de tipos
    3. Acotamiento de tipos
    4. Empacamiento y desempacamiento
3. Iteradores (semana 2, sesión 3)
    1. El operador for-each
4. Colecciones (semana 2, sesión 3)
5. Listas (semanas 2-3, sesiones 4-5)
    1. Definición de listas
    2. Algoritmos para listas
    3. Iteradores para listas
6. Complejidad computacional (semana 3, sesión 6)
    1. La notación de O grandota
    2. Complejidades en tiempo y en espacio
7. Arreglos (semana 4, sesión 7)
    1. El polinomio de redireccionamiento
    2. Arreglos y genéricos
8. Pilas y colas (semana 4, sesión 8)
    1. Algoritmos de la clase abstracta
    2. Algoritmos para pilas
    3. Algoritmos para colas
9. Lambdas (semanas 4-5, sesiones 8-9)
    1. Lambdas y funciones de primera clase
    2. Clases internas anónimas
    3. Lambdas en Java con interfaces funcionales
10. Ordenamientos (semanas 5-6, sesiones 9-10)
    1. Ordenamientos en arreglos
        1. Algoritmo SelectionSort
        2. Algoritmo QuickSort
        3. Manteniendo arreglos ordenados
    2. Ordenamientos en listas
        1. Algoritmo MergeSort
        2. Manteniendo listas ordenadas
    3. La razón para ordenar colecciones
11. Búsquedas (semana 6, sesión 11)
    1. Búsquedas en arreglos
    2. Búsquedas en listas
12. Árboles binarios (semanas 6-7, sesiones 12-13)
    1. Definición de árboles binarios
    2. Propiedades de árboles binarios
    3. Implementación en Java
    4. Algoritmos para árboles binarios
    5. Aprovechando referencias no utilizadas
13. Árboles binarios completos (semana 7, sesión 13-14)
    1. Definición de árboles binarios completos
    2. Recorriendo árboles por amplitud
    3. Acciones para vértices de árboles binarios
    4. Algoritmos para árboles binarios completos
14. Árboles binarios ordenados (semana 8, sesiones 15-16)
    1. Definición de árboles binarios ordenados
    2. Recorriendo árboles por profundidad
        1. DFS pre-order
        2. DFS post-order
        3. DFS in-order
    3. Algoritmos para árboles binarios ordenados
    4. Complejidades en tiempo y en espacio
15. Árboles rojinegros (semanas 9-10, sesiones 17-19)
    1. Definición de árboles rojinegros
    2. Algoritmos de los árboles rojinegros
        1. Algoritmo para agregrar
        2. Algoritmo para eliminar
16. Árboles AVL (semana 10, sesión 20)
    1. Definición de árboles AVL
    2. Algoritmos de los árboles AVL
        1. Algoritmo de rebalanceo
17. Gráficas (semana 11, sesiones 21-22)
    1. Definición de gráficas
    2. Propiedades de gráficas
    3. Implementación en Java
    4. Recorridos en gráficas
        1. BFS en gráficas
        2. DFS en gráficas
    5. Algoritmos para gráficas
    6. Implementaciones alternativas de gráficas
18. Montículos mínimos (semana 12, sesiones 23-24)
    1. Definición de montículos mínimos
    2. Acomodando hacia arriba y hacia abajo
        1. Acomodando hacia arriba
        2. Acomodando hacia abajo
    3. Implementación en Java
    4. Algoritmos para montículos mínimos
    5. Algoritmo HeapSort
    6. Montículos de arreglos
19. Algoritmo de Dijkstra (semana 13, sesiones 25-26)
    1. Definición de trayectoria de peso mínimo
    2. Implementación en Java
    3. Algoritmos para gráficas con pesos en las aristas
    4. Algoritmo de trayectoria mínima
    5. Algoritmo de Dijkstra
    6. Reconstruyendo trayectorias
    7. Pesos con matrices de adyacencias
20. Funciones de dispersión (semana 14, sesiones 27-28)
    1. Colisiones en funciones de dispersión
    2. Implementación en Java
        1. Cascando huevos
    3. Función de dispersión XOR
    4. Función de dispersión Bob Jenkins
    5. Función de dispersión de Daniel J. Bernstein
    6. Funciones de dispersión para diccionarios
21. Diccionarios (semana 15, sesiones 29-30)
    1. El arreglo del diccionario
    2. Implementación en Java
    3. Algoritmos para diccionarios
    4. Complejidades en tiempo y en espacio
    5. Implementaciones alternas de diccionarios
22. Conjuntos (semana 16, sesión 31)
    1. Implementación en Java
    2. Algoritmos para conjuntos
    3. Otros usos de conjuntos
23. Mejorando gráficas (semana 16, sesión 31)
    1. Modificaciones al código
24. Conclusiones (semana 16, sesión 32)
