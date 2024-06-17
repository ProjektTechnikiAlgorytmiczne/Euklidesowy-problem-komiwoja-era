package org.example;

import java.util.*;

public class Algorithms {

    /*
    Metoda do generowania tablicy punktów, które reprezentują pozycje miast w problemie komiwojażera.
     */
    static Point[] generatePoints(int size) {
        Random rand = new Random();
        Point[] points = new Point[size];
        for (int i = 0; i < size; i++) {
            points[i] = new Point(rand.nextDouble() * 100, rand.nextDouble() * 100);
        }
        return points;
    }

    /*
    Metoda oblicza macierz odległości między wszystkimi parami punktów (miast).
    */
    static double[][] calculateDistanceMatrix(Point[] points) {
        int size = points.length;
        double[][] distanceMatrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                distanceMatrix[i][j] = points[i].distance(points[j]);
            }
        }
        return distanceMatrix;
    }

    /*
    Implementacja algorytmu brute-force
     */
    static double tspBruteForce(double[][] distanceMatrix) {
        // Pobiera rozmiar macierzy odległości
        int size = distanceMatrix.length;
        // Lista do przechowywania indeksów miast (bez miasta startowego)
        List<Integer> vertices = new ArrayList<>();
        for (int i = 1; i < size; i++) {
            vertices.add(i);
        }

        // Zmienna do przechowywania długości najkrótszej znalezionej ścieżki
        double minPath = Double.MAX_VALUE;
        do {
            // Zmienna do przechowywania długości bieżącej ścieżki
            double currentPathWeight = 0;
            int k = 0;
            // Iterowanie przez wszystkie maista w bieżącej permutacji
            for (int i = 0; i < vertices.size(); i++) {
                // Dodawanie odległości między bieżącym miastem k, a następnym miastem w permutacji
                currentPathWeight += distanceMatrix[k][vertices.get(i)];
                // Indeks na kolejne miasto
                k = vertices.get(i);
            }
            // Dodanie odległości powrotnej z ostatniego miasta permutacji do miasta startowego
            currentPathWeight += distanceMatrix[k][0];
            // Aktualizacja długości najkrótszej
            minPath = Math.min(minPath, currentPathWeight);
        } while (nextPermutation(vertices));

        return minPath;
    }

    /*
    Metoda do generowania następnej permutacji listy data
     */
    static boolean nextPermutation(List<Integer> data) {
        int k = data.size() - 2;
        while (k >= 0 && data.get(k) >= data.get(k + 1)) {
            k--;
        }
        if (k < 0) return false;

        int l = data.size() - 1;
        while (data.get(k) >= data.get(l)) {
            l--;
        }
        Collections.swap(data, k, l);

        Collections.reverse(data.subList(k + 1, data.size()));
        return true;
    }

    /*
    Metoda z implementacja algorytmu Christofidesa
     */
    static double tspChristofides(double[][] distanceMatrix) {
        int size = distanceMatrix.length;

        // Znajdź MST (Minimalne Drzewo Rozpinające) przy użyciu algorytmu Kruskala
        Set<Edge> mst = kruskalMST(distanceMatrix);

        // Znajdź wierzchołki o nieparzystym stopniu
        List<Integer> oddVertices = findOddDegreeVertices(mst, size);

        // Znajdź perfekcyjne dopasowanie dla wierzchołków o nieparzystym stopniu
        Set<Edge> matching = findPerfectMatching(oddVertices, distanceMatrix);

        // Połącz MST i perfekcyjne dopasowanie
        mst.addAll(matching);

        // Znajdź cykl Eulerowski
        List<Integer> eulerianCycle = findEulerianCycle(mst, size);

        // Konwertuj cykl Eulerowski na cykl Hamiltona (pomijając odwiedzenia wierzchołków)
        List<Integer> hamiltonianCycle = makeHamiltonian(eulerianCycle);

        // Oblicz długość cyklu Hamiltona
        double pathLength = calculatePathLength(hamiltonianCycle, distanceMatrix);

        return pathLength;
    }

    /*
    Metoda implementująca algorytm Kruskala do znajdowania minilanego drzewa rozpinającego dla podanego grafu
    reprezentowaną przez macierz odległości
     */
    static Set<Edge> kruskalMST(double[][] distanceMatrix) {
        int size = distanceMatrix.length;
        PriorityQueue<Edge> edges = new PriorityQueue<>(Comparator.comparingDouble(e -> e.getWeight()));
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                edges.add(new Edge(i, j, distanceMatrix[i][j]));
            }
        }

        int[] parent = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i;
        }

        Set<Edge> mst = new HashSet<>();
        while (!edges.isEmpty() && mst.size() < size - 1) {
            Edge edge = edges.poll();
            int root1 = find(parent, edge.getU());
            int root2 = find(parent, edge.getV());
            if (root1 != root2) {
                mst.add(edge);
                union(parent, root1, root2);
            }
        }
        return mst;
    }

    /*
    Metoda pomocnicza używana w algorytmie Kruskala do znajdowania reprezentanta zbioru, do którego należy dany wierzchołek
     */
    static int find(int[] parent, int vertex) {
        if (parent[vertex] != vertex) {
            parent[vertex] = find(parent, parent[vertex]);
        }
        return parent[vertex];
    }

    /*
    Metoda pomocnicza do łączenia dwóch zbiorów w jeden
     */
    static void union(int[] parent, int root1, int root2) {
        parent[root1] = root2;
    }

    /*
    Metoda do algorytmu Christofidesa, która znajduje wszystkie wierzchołki o nieparzystym stopniu
    w minimalnym drzewie rozpinającym
     */
    static List<Integer> findOddDegreeVertices(Set<Edge> mst, int size) {
        int[] degree = new int[size];
        for (Edge edge : mst) {
            degree[edge.getU()]++;
            degree[edge.getV()]++;
        }

        List<Integer> oddVertices = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (degree[i] % 2 != 0) {
                oddVertices.add(i);
            }
        }
        return oddVertices;
    }

    /*
    Metoda wykorzystywana w algorytmie Christofidesa, znajduje dopasowanie minimalne w grafie, który składa
    się z wierzchołków o nieparzystym stopniu z minimalnego drzewa rozpinającego
     */
    static Set<Edge> findPerfectMatching(List<Integer> oddVertices, double[][] distanceMatrix) {
        Set<Edge> matching = new HashSet<>();
        boolean[] used = new boolean[distanceMatrix.length];
        for (int i = 0; i < oddVertices.size(); i++) {
            if (!used[oddVertices.get(i)]) {
                int bestJ = -1;
                double bestWeight = Double.MAX_VALUE;
                for (int j = i + 1; j < oddVertices.size(); j++) {
                    if (!used[oddVertices.get(j)] && distanceMatrix[oddVertices.get(i)][oddVertices.get(j)] < bestWeight) {
                        bestWeight = distanceMatrix[oddVertices.get(i)][oddVertices.get(j)];
                        bestJ = j;
                    }
                }
                used[oddVertices.get(i)] = true;
                used[oddVertices.get(bestJ)] = true;
                matching.add(new Edge(oddVertices.get(i), oddVertices.get(bestJ), bestWeight));
            }
        }
        return matching;
    }

    /*
    Metoda do znalezienia cyklu Eulera w grafie, który jest reprezentowany przez zbiór krawędzi
     */
    static List<Integer> findEulerianCycle(Set<Edge> edges, int size) {
        List<Integer>[] graph = new ArrayList[size];
        for (int i = 0; i < size; i++) {
            graph[i] = new ArrayList<>();
        }
        for (Edge edge : edges) {
            graph[edge.getU()].add(edge.getV());
            graph[edge.getV()].add(edge.getU());
        }

        List<Integer> eulerianCycle = new ArrayList<>();
        findEulerianCycleUtil(graph, 0, eulerianCycle);
        return eulerianCycle;
    }

    /*
    Metoda rekurencyjna do znalezienia cyklu Eulera w grafie
     */
    static void findEulerianCycleUtil(List<Integer>[] graph, int u, List<Integer> eulerianCycle) {
        for (int i = 0; i < graph[u].size(); i++) {
            int v = graph[u].get(i);
            if (v != -1) {
                graph[u].set(i, -1);
                for (int j = 0; j < graph[v].size(); j++) {
                    if (graph[v].get(j) == u) {
                        graph[v].set(j, -1);
                        break;
                    }
                }
                findEulerianCycleUtil(graph, v, eulerianCycle);
            }
        }
        eulerianCycle.add(u);
    }

    /*
    Metoda konwertuje cykl Eulera na cykl Hamiltona poprzez usunięcie powtórzonych wizyt w wierzchołkach.
    W algorytmie Christofidesa, ta funkcja jest używana do uzyskania przybliżonego rozwiązania
     */
    static List<Integer> makeHamiltonian(List<Integer> eulerianCycle) {
        Set<Integer> visited = new HashSet<>();
        List<Integer> hamiltonianCycle = new ArrayList<>();
        for (int vertex : eulerianCycle) {
            if (!visited.contains(vertex)) {
                visited.add(vertex);
                hamiltonianCycle.add(vertex);
            }
        }
        return hamiltonianCycle;
    }

    /*
    Metoda obliczająca całkowitą długość ścieżki (cyklu) w grafie, który jest reprezentowany przez macierz odległości
     */
    static double calculatePathLength(List<Integer> cycle, double[][] distanceMatrix) {
        double length = 0.0;
        for (int i = 0; i < cycle.size() - 1; i++) {
            length += distanceMatrix[cycle.get(i)][cycle.get(i + 1)];
        }
        length += distanceMatrix[cycle.get(cycle.size() - 1)][cycle.get(0)];
        return length;
    }

    /*
    Metoda służąca do pomiaru czasu wykonania algorytmu
     */
    static void measureTime(Runnable algorithm, String algorithmName) {
        long startTime = System.nanoTime();
        algorithm.run();
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        System.out.println(algorithmName + " czas wykonania: " + duration + " ns");
    }

    /*
    Metoda do pomiaru przybliżonego pomiaru zużycia pamięci przez algorytm
     */
    static void measureMemory(Runnable algorithm, String algorithmName) {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long startMemory = runtime.totalMemory() - runtime.freeMemory();
        algorithm.run();
        long endMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsage = endMemory - startMemory;
        System.out.println(algorithmName + " zużycie pamięci: " + memoryUsage + " bajtów");
    }
}
