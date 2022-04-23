package me.petrolingus.modsys.twosourceinterference.core;

import java.util.concurrent.ThreadLocalRandom;

public class Algorithm {

    private static final double SIGMA = 1;
    private static final double EPSILON = 1;
    private static final double MAX_SIMULATION_TIME = 1;
    private static final double TAU = 1;

    int n = 0;
    Cell[][][] system;

    public Algorithm(int n) {
        this.n = n;
        system = new Cell[2][n][n];
    }

    private void calculateEx() {



    }

    public double[][] getFieldValues() {
        double[][] data = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                data[i][j] = 360 * ThreadLocalRandom.current().nextDouble();
            }
        }
        return data;
    }

}
