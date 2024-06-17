package org.example;

public class Main {

    public static void main(String[] args) {
        Algorithms algorithms = new Algorithms();
        int size = 10;
        Point[] points = algorithms.generatePoints(size);
        double[][] distanceMatrix = algorithms.calculateDistanceMatrix(points);

        //-----------------------Algorytm Brute-Force----------------------------------------

        algorithms.measureTime(() -> {
            double result = algorithms.tspBruteForce(distanceMatrix);
            System.out.println("Brute-force wynik: " + result);
        }, "Brute-force");

        algorithms.measureMemory(() -> {
            double result = algorithms.tspBruteForce(distanceMatrix);
            System.out.println("Brute-force wynik: " + result);
        }, "Brute-force");

        //----------------------------Algorytm Christofides-------------------------------------

        algorithms.measureTime(() -> {
            double result = algorithms.tspChristofides(distanceMatrix);
            System.out.println("Christofides wynik: " + result);
        }, "Christofides");

        algorithms.measureMemory(() -> {
            double result = algorithms.tspChristofides(distanceMatrix);
            System.out.println("Christofides wynik: " + result);
        }, "Christofides");


        double exactSolution = algorithms.tspBruteForce(distanceMatrix);
        double heuristicSolution = algorithms.tspChristofides(distanceMatrix);


        System.out.println("Dokładne rozwiązanie: " + exactSolution);
        System.out.println("Heurystyczne rozwiązanie: " + heuristicSolution);
        System.out.println("Jakość rozwiązania: " + (heuristicSolution / exactSolution) * 100 + "%");
    }
}
