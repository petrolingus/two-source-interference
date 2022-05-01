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
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    if (j > 3 && j < n - 4 && k > 3 && k < n - 4) {
                        system[i][j][k] = new Cell(0, 0, 0, Cell.Type.MAIN);
                    } else {
                        system[i][j][k] = new Cell(0, 0, 0, Cell.Type.BORDER);
                    }
                }
            }
        }
    }

    private void calculate() {

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {

                if (system[0][i][j].type.equals(Cell.Type.MAIN)) {

                    system[1][i][j].Hz = system[0][i][j].Hz - (TAU / MU) * (
                            (system[0][i + 1][j].Ey - system[0][i][j].Ey) / CELL_SIZE -
                                    (system[0][i][j + 1].Ex - system[0][i][j].Ex) / CELL_SIZE);

                    system[1][i][j].Ex = (C1 / C2) * system[0][i][j].Ex - ((TAU / EPSILON) / C2) * SIGMA * system[0][i][j].Ex +
                            (2.0 * TAU * EPSILON / C2) * ((system[1][i][j].Hz -  system[1][i][j].Hz) / CELL_SIZE);

//                system[1][i][j].Ex1 = (C1 / C2) * prevEx1 + ((2 * TAU / EPSILON) / C2) * (0);


                }
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
