package algorithm;

import data.Matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Genetic {
    private final Random rand = new Random();
    private final Matrix graph;
    private final int populationSize;
    private final double crossRate;
    private final double mutationRate;
    private int bestSolution = Integer.MAX_VALUE;
    private int[] bestPath;
    private final int mutationMethod;
    private final int crossoverMethod;
    private long millisActualTime;
    private long bestSolutionTime = Long.MAX_VALUE;
    private final long timeLimit;
    List<TimeResultPair> solutions = new ArrayList<>();

    public Genetic(Matrix graph, long timeLimit, int populationSize, double crossRate, double mutationRate, int mutationMethod, int crossoverMethod) {
        this.graph = graph;
        this.timeLimit = timeLimit;
        this.populationSize = populationSize;
        this.crossRate = crossRate;
        this.mutationRate = mutationRate;
        this.mutationMethod = mutationMethod;
        this.crossoverMethod = crossoverMethod;
    }

    public void solve() {
        int[][] population = createInitialPopulation();
        int[] ratedPopulation;

        millisActualTime = System.currentTimeMillis();

        while (true) {
            ratedPopulation = evaluatePopulation(population);
            updateBestSolution(ratedPopulation, population);
            int[][] nextPopulation = createNextPopulation(population, ratedPopulation);

            if(crossoverMethod == 1){
                orderCrossover(nextPopulation);
            } else cycleCrossover(nextPopulation);

            if(mutationMethod == 1){
                swapMutate(nextPopulation);
            } else inversionMutate(nextPopulation);

            population = nextPopulation;

            long executionTime = System.currentTimeMillis() - millisActualTime;
            if (executionTime > timeLimit) {
                printBestSolution();
                return;
            }
        }
    }

    private int[][] createInitialPopulation() {
        int[][] population = new int[populationSize][graph.size - 1];

        for (int i = 0; i < populationSize; i++) {
            population[i] = generateRandomPath();
        }

        return population;
    }

    private int[] generateRandomPath() {
        int[] randomPath = new int[graph.size - 1];
        for (int i = 0; i < randomPath.length; i++) {
            randomPath[i] = i + 1;
        }

        for (int i = 0; i < randomPath.length; i++) {
            int randomIndexToSwap = rand.nextInt(randomPath.length);
            int temp = randomPath[randomIndexToSwap];
            randomPath[randomIndexToSwap] = randomPath[i];
            randomPath[i] = temp;
        }

        return randomPath;
    }

    private int[] evaluatePopulation(int[][] population) {
        int[] ratedPopulation = new int[populationSize];

        for (int i = 0; i < population.length; i++) {
            ratedPopulation[i] = calculatePathCost(population[i]);
        }

        return ratedPopulation;
    }

    private int calculatePathCost(int[] path) {
        int cost = 0;
        for (int i = 0; i < path.length - 1; i++) {
            cost += graph.getDistanceMatrix()[path[i]][path[i + 1]];
        }
        cost += graph.getDistanceMatrix()[0][path[0]];
        cost += graph.getDistanceMatrix()[path[path.length - 1]][0];
        return cost;
    }

    private void updateBestSolution(int[] ratedPopulation, int[][] population) {
        int minIndex = findIndexOfMinElementFromTab(ratedPopulation);
        if (ratedPopulation[minIndex] < bestSolution) {
            bestSolutionTime = System.currentTimeMillis() - millisActualTime;
            bestSolution = ratedPopulation[minIndex];
            System.out.println("Najlepszy teraz:" + bestSolution);
            solutions.add(new TimeResultPair(bestSolutionTime, bestSolution));
            bestPath = population[minIndex].clone();
        }
    }

    private int[][] createNextPopulation(int[][] population, int[] ratedPopulation) {
        int[][] nextPopulation = new int[populationSize][graph.size - 1];

        for (int j = 0; j < populationSize; j++) {
            int[] permutation = tournamentSelection(population, ratedPopulation);
            nextPopulation[j] = permutation.clone();
        }

        return nextPopulation;
    }

    private int[] tournamentSelection(int[][] population, int[] ratedPopulation) {
        int tournamentSize = (int) (populationSize * 0.30);
        int result = Integer.MAX_VALUE;
        int[] permutation = new int[graph.size - 1];

        for (int k = 0; k < tournamentSize; k++) {
            int index = rand.nextInt(populationSize);
            if (ratedPopulation[index] < result) {
                result = ratedPopulation[index];
                permutation = population[index].clone();
            }
        }

        return permutation;
    }

    private void orderCrossover(int[][] population) {
        int rotate = populationSize - (int) (crossRate * (float) populationSize);
        rotate = rand.nextInt(rotate);

        for (int j = rotate; j < (int) (crossRate * (float) populationSize) + rotate; j += 2) {
            population[j] = myOrderCrossover(population[j], population[j + 1]);
            population[j + 1] = myOrderCrossover(population[j + 1], population[j]);
        }
    }

    private int[] myOrderCrossover(int[] tab1, int[] tab2) {
        int startIndex = rand.nextInt(graph.size - 2);
        int endIndex = rand.nextInt(graph.size - 1);
        int actualChildIndex;
        if (startIndex > endIndex) {
            int helper = endIndex;
            endIndex = startIndex;
            startIndex = helper;
        }
        int[] child = new int[graph.size - 1];
        Arrays.fill(child, -1);

        if (endIndex + 1 - startIndex >= 0)
            System.arraycopy(tab1, startIndex, child, startIndex, endIndex + 1 - startIndex);

        actualChildIndex = endIndex + 1;
        if (actualChildIndex > tab1.length - 1) {
            actualChildIndex = 0;
        }

        for (int i = endIndex + 1; i < tab2.length; i++) {
            if (!isElementInTab(child, tab2[i])) {
                child[actualChildIndex] = tab2[i];
                actualChildIndex++;
            }
            if (actualChildIndex > tab1.length - 1) {
                actualChildIndex = 0;
            }
        }
        for (int i = 0; isElementInTab(child, -1); i++) {
            if (!isElementInTab(child, tab2[i])) {
                child[actualChildIndex] = tab2[i];
                actualChildIndex++;
                if (actualChildIndex > tab1.length - 1) {
                    actualChildIndex = 0;
                }
            }
        }
        return child;
    }

    private void cycleCrossover(int[][] population) {
        int rotate = populationSize - (int) (crossRate * (float) populationSize);
        rotate = rand.nextInt(rotate);

        for (int j = rotate; j < (int) (crossRate * (float) populationSize) + rotate; j += 2) {
            population[j] = cycleCrossover(population[j], population[j + 1]);
            population[j + 1] = cycleCrossover(population[j + 1], population[j]);
        }
    }

    private int[] cycleCrossover(int[] parent1, int[] parent2) {
        int[] child = new int[graph.size - 1];
        Arrays.fill(child, -1);

        int index = rand.nextInt(graph.size - 1);

        do {
            int temp = parent1[index];
            child[index] = temp;
            index = findIndex(parent2, temp);
        } while (child[index] == -1);

        for (int i = 0; i < parent2.length; i++) {
            if (child[i] == -1) {
                child[i] = parent2[i];
            }
        }
        return child;
    }

    private int findIndex(int[] array, int value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }


    private void swapMutate(int[][] population) {
        for (int j = 0; j < (int) (mutationRate * (float) populationSize) + 1; j++) {
            int p1, p2, p3;
            do {
                p1 = rand.nextInt(graph.size - 1);
                p2 = rand.nextInt(graph.size - 1);
                p3 = rand.nextInt(populationSize);
            } while (p1 == p2);
            swap(p1, p2, population[p3]);
        }
    }

    private void swap(int i, int j, int[] path) {
        int temp = path[i];
        path[i] = path[j];
        path[j] = temp;
    }

    private void inversionMutate(int[][] population) {
        for (int j = 0; j < (int) (mutationRate * (float) populationSize) + 1; j++) {
            int p1, p2, p3;
            do {
                p1 = rand.nextInt(graph.size - 1);
                p2 = rand.nextInt(graph.size - 1);
                p3 = rand.nextInt(populationSize);
            } while (p1 == p2);

            if (p1 > p2) {
                int temp = p1;
                p1 = p2;
                p2 = temp;
            }

            invert(p1, p2, population[p3]);
        }
    }

    private void invert(int start, int end, int[] chromosome) {
        while (start < end) {
            int temp = chromosome[start];
            chromosome[start] = chromosome[end];
            chromosome[end] = temp;
            start++;
            end--;
        }
    }


    private boolean isElementInTab(int[] tab, int element) {
        for (int i : tab) {
            if (i == element) {
                return true;
            }
        }
        return false;
    }

    private int findIndexOfMinElementFromTab(int[] tab) {
        int smallest = Integer.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < tab.length; i++) {
            if (tab[i] < smallest) {
                smallest = tab[i];
                index = i;
            }
        }
        return index;
    }

    private void printBestSolution() {
        System.out.print("Najlepsza znaleziona ścieżka: ");
        System.out.print("[0 ");
        for (int j : bestPath) {
            System.out.print(j + " ");
        }
        System.out.println("0]");
        System.out.println("Koszt ścieżki: " + bestSolution);
        System.out.println("Najlepsze rozwiązanie znaleziono w: " + bestSolutionTime + " ms");
        System.out.println("Lista wyników:");
        for (TimeResultPair solution : solutions) {
            System.out.println("Czas: " + solution.getTime() + " ms, Wynik: " + solution.getResult());
        }
    }
}