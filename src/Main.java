import algorithm.Genetic;
import data.DataPrinter;
import data.FromFileReader;
import data.Matrix;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
//        long timeLimit = -1;
//        int populationSize = -1;
//        double crossRate = -1;
//        double mutationRate = -1;
//        int crossoverMethod = -1;
//        int mutationMethod = -1;
        long timeLimit = 120000;
        int populationSize = 10;
        double crossRate = 0.8;
        double mutationRate = 0.05;
        int crossoverMethod = 1;
        int mutationMethod = 1;
        FromFileReader fromFileReaderr = new FromFileReader();
        Matrix matrix = fromFileReaderr.loadFromFile("ftv170.atsp");
        Genetic genetic;
        Scanner in = new Scanner(System.in);
        int menu = -1;
        while (menu != 0) {
            System.out.println("Wybierz opcję programu: ");
            System.out.println("1. Wczytaj graf z pliku");
            System.out.println("2. Wyświetl aktualny graf");
            System.out.println("3. Kryterium stopu");
            System.out.println("4. Rozmiar populacji");
            System.out.println("5. Współczynnik krzyżowania");
            System.out.println("6. Współczynnik mutacji");
            System.out.println("7. Wybierz metodę krzyżowania");
            System.out.println("8. Wybierz metodę mutacji");
            System.out.println("9. Rozwiąż");
            System.out.println("10. Wyjdź");
            menu = in.nextInt();
            switch (menu) {
                case 1 -> {
                    System.out.println("Podaj nazwę pliku: ");
                    String filename = in.next();
                    FromFileReader fromFileReader = new FromFileReader();
                    matrix = fromFileReader.loadFromFile(filename);
                }
                case 2 -> {
                    if (matrix != null) {
                        new DataPrinter().displayData(matrix);
                    } else {
                        System.out.println("Najpierw wczytaj lub wygeneruj graf");
                    }
                }
                case 3 -> {
                    System.out.println("Podaj limit czasowy w ms: ");
                    timeLimit = in.nextInt();
                }
                case 4 -> {
                    System.out.println("Wybierz rozmiar populacji: ");
                    populationSize = in.nextInt();
                }
                case 5 -> {
                    System.out.println("Wybierz współczynnik krzyżowania (w przedziale 0.0 - 1.0)");
                    crossRate = in.nextDouble();
                }
                case 6 -> {
                    System.out.println("Wybierz współczynnik mutacji (w przedziale 0.0 - 1.0)");
                    mutationRate = in.nextDouble();
                }
                case 7 -> {
                    System.out.println("Wybierz metodę krzyżowania: ");
                    System.out.println("1. Order Crossover");
                    System.out.println("2. Cycle Crossover");
                    crossoverMethod = in.nextInt();
                }
                case 8 -> {
                    System.out.println("Wybierz metodę mutacji: ");
                    System.out.println("1. Swap Mutation");
                    System.out.println("2. Inverse Mutation");
                    mutationMethod = in.nextInt();
                }
                case 9 -> {
                    if (matrix == null || timeLimit == -1 || populationSize == -1 || crossRate == -1 || mutationRate == -1) {
                        System.out.println("Uzupełnij brakujące dane zanim rozpoczniesz");
                    } else {
                        genetic = new Genetic(matrix, timeLimit, populationSize, crossRate, mutationRate, mutationMethod, crossoverMethod);
                        genetic.solve();

                    }
                }
                case 10 -> System.exit(0);
                default -> System.out.println("Nieprawidłowy wybór. Wybierz ponownie.");
            }
        }

    }
}
