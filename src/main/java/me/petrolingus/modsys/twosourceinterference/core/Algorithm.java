package me.petrolingus.modsys.twosourceinterference.core;

import me.petrolingus.modsys.twosourceinterference.core.cell.*;

public class Algorithm {

    private double time;

    private final Cell[][][] system;

    public Algorithm() {

        // Create system array
        system = new Cell[2][Constants.SIZE][Constants.SIZE];

        // Generate main cells
        for (int i = Constants.D; i < Constants.SIZE - Constants.D; i++) {
            for (int j = Constants.D; j < Constants.SIZE - Constants.D; j++) {
                system[0][i][j] = new MainCell();
                system[1][i][j] = new MainCell();
            }
        }

        // Generate border cells
        for (int i = 1; i <= Constants.D; i++) {
            for (int j = Constants.D - i; j < Constants.SIZE - Constants.D + i; j++) {
                for (int k = Constants.D - i; k < Constants.SIZE - Constants.D + i; k++) {
                    if (system[0][j][k] != null) continue;
                    double sigma = Constants.SIGMA_MAX * Math.pow((double) i / Constants.D, Constants.N);
                    double astra = sigma * Constants.MU / Constants.EPSILON;
                    system[0][j][k] = new BorderCell(sigma, astra);
                    system[1][j][k] = new BorderCell(sigma, astra);
                }
            }
        }

        // Generate source cell
        system[0][Constants.SIZE / 2][Constants.SIZE / 2] = new SourceCell();
        system[1][Constants.SIZE / 2][Constants.SIZE / 2] = new SourceCell();
    }

    public void calculate() {

        time += Constants.TAU;

        double source = 10000 * Math.sin(1000 * time) / (1 + Math.exp(-time + Math.PI));

        // Hz
        for (int i = 0; i < Constants.SIZE; i++) {
            for (int j = 0; j < Constants.SIZE; j++) {
                if (system[0][i][j].cellType.equals(CellType.MAIN)) {
                    double value1 = (system[0][i][j + 1].getEy() - system[0][i][j].getEy()) / Constants.STEP;
                    double value2 = (system[0][i + 1][j].getEx() - system[0][i][j].getEx()) / Constants.STEP;
                    double value3 = system[0][i][j].getHz() - Constants.C6 * (value1 - value2);
                    system[1][i][j].setHz(value3);
                } else if (system[0][i][j].cellType.equals(CellType.SOURCE)) {
                    system[1][i][j].setHz(source);
                } else {
                    system[1][i][j].setHz(0);
                }
            }
        }

        // Ex
        for (int i = 0; i < Constants.SIZE; i++) {
            for (int j = 0; j < Constants.SIZE; j++) {
                if (system[0][i][j].cellType.equals(CellType.MAIN)) {
                    double value1 = Constants.C3 * system[0][i][j].getEx();
                    double value2 = Constants.C4 * Constants.SIGMA * system[0][i][j].getEx();
                    double value3 = Constants.C5 * (system[1][i][j].getHz() - system[1][i - 1][j].getHz()) / (2.0 * Constants.STEP);
                    double value4 = value1 - value2 + value3;
                    system[1][i][j].setEx(value4);
                } else if (system[0][i][j].cellType.equals(CellType.SOURCE)) {
                    double value1 = Constants.C3 * system[0][i][j].getEx();
                    double value2 = Constants.C4 * Constants.SIGMA * system[0][i][j].getEx();
                    double value3 = Constants.C5 * (system[1][i][j].getHz() - system[1][i - 1][j].getHz()) / (2.0 * Constants.STEP);
                    double value4 = value1 - value2 + value3;
                    system[1][i][j].setEx(value4);
//                    double value1 = Constants.C10 * system[0][i][j].getEx();
//                    double value2 = Constants.C11 * (system[1][i][j].getHz() - system[1][i - 1][j].getHz()) / (2.0 * Constants.STEP);
//                    double value4 = value1 + value2 + sourceEx;
//                    system[1][i][j].setEx(value4);
                } else {
                    system[1][i][j].setEx(0);
                }
            }
        }

        // Ey
        for (int i = 0; i < Constants.SIZE; i++) {
            for (int j = 0; j < Constants.SIZE; j++) {
                if (system[0][i][j].cellType.equals(CellType.MAIN)) {
                    double value1 = Constants.C3 * system[0][i][j].getEy();
                    double value2 = Constants.C4 * Constants.SIGMA * system[0][i][j].getEy();
                    double value3 = Constants.C5 * (system[1][i][j].getHz() - system[1][i][j - 1].getHz()) / (2.0 * Constants.STEP);
                    double value4 = value1 - value2 - value3;
                    system[1][i][j].setEy(value4);
                } else if (system[0][i][j].cellType.equals(CellType.SOURCE)) {
                    double value1 = Constants.C3 * system[0][i][j].getEy();
                    double value2 = Constants.C4 * Constants.SIGMA * system[0][i][j].getEy();
                    double value3 = Constants.C5 * (system[1][i][j].getHz() - system[1][i][j - 1].getHz()) / (2.0 * Constants.STEP);
                    double value4 = value1 - value2 - value3;
                    system[1][i][j].setEy(value4);
//                    double value1 = Constants.C10 * system[0][i][j].getEy();
//                    double value2 = Constants.C11 * (system[1][i][j].getHz() - system[1][i][j - 1].getHz()) / (2.0 * Constants.STEP);
//                    double value4 = value1 - value2 + sourceEy;
//                    system[1][i][j].setEy(value4);
                } else {
                    system[1][i][j].setEy(0);
                }
            }
        }

        for (int i = 0; i < Constants.SIZE; i++) {
            System.arraycopy(system[1][i], 0, system[0][i], 0, Constants.SIZE);
        }
    }

    public double[][] getFieldValues() {
        double[][] data = new double[Constants.SIZE][Constants.SIZE];
        for (int i = 0; i < Constants.SIZE; i++) {
            for (int j = 0; j < Constants.SIZE; j++) {
                data[i][j] = system[0][i][j].getValue();
            }
        }
        return data;
    }

}
