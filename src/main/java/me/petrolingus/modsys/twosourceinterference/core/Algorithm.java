package me.petrolingus.modsys.twosourceinterference.core;

import java.util.concurrent.ThreadLocalRandom;

public class Algorithm {

    private static final double SIGMA = 1;
    private static final double MAX_SIMULATION_TIME = 1;
    private static final double TAU = 0.1;

    public static final double C = 299792458; // m/s
    public static final double EPSILON = 1.25663706e-6;
    public static final double MU = 8.85418782e-12;
    public static final double CELL_SIZE = 1;

    public static final double C0 = SIGMA * TAU / (2 * EPSILON);
    public static final double C1 = 1.0 - SIGMA * TAU / (2 * EPSILON);
    public static final double C2 = 1.0 + SIGMA * TAU / (2 * EPSILON);

    int n = 0;
    Cell[][][] system;

    public Algorithm(int n) {
        this.n = n;
        system = new Cell[2][n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == n / 2 && j == n / 2) {
                    double Ex1 = 100;
                    system[0][i][j] = new Cell(Ex1, 0, 0, 0, 0, 0, 0);
                } else {
                    system[0][i][j] = new Cell(0, 0, 0, 0, 0, 0, 0);
                }
            }
        }
    }

    private void calculate() {

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double prevHz = system[0][i][j].Hz;
                double prevEx1 = system[0][i][j].Ex1;
                double prevEx2 = system[0][i][j].Ex2;
                double prevEy1 = system[0][i][j].Ey1;
                double prevEy2 = system[0][i][j].Ey2;
                system[1][i][j].Hz = prevHz - (TAU / MU) * ((prevEy2 - prevEy1) / CELL_SIZE - (prevEx2 - prevEx1) / CELL_SIZE);
                system[1][i][j].Ex1 = (C1 / C2) * prevEx1 + ((2 * TAU / EPSILON) / C2) * (0);
            }
        }

    }

    public double[][] getFieldValues() {
        double[][] data = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                data[i][j] = system[0][i][j].getValue();
            }
        }
        return data;
    }

}
