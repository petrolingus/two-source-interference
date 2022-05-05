package me.petrolingus.modsys.twosourceinterference.core;

import me.petrolingus.modsys.twosourceinterference.core.cell.*;

@SuppressWarnings({"DuplicatedCode", "CommentedOutCode", "StatementWithEmptyBody", "ConstantConditions"})
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
                    BorderCell currentCell = ((BorderCell) system[0][i][j]);

                    boolean isRight = j > Constants.SIZE / 2;
                    boolean isBottom = i > Constants.SIZE / 2;
                    int shiftY = (isBottom) ? -1 : 1;
                    int shiftX = (isRight) ? -1 : 1;

                    double value1 = 0;
                    double eyz_ezx_right;
                    double eyz_ezx_center = currentCell.getEyz() + currentCell.getEyx();
                    if (system[0][i][j + shiftX] instanceof BorderCell borderCell) {
                        eyz_ezx_right = borderCell.getEyz() + borderCell.getEyx();
                    } else {
                        Cell cell = system[0][i][j + shiftX];
                        eyz_ezx_right = cell.getEy() / 2.0;
                    }
                    if (isRight) {
                        value1 = (eyz_ezx_center - eyz_ezx_right) / Constants.STEP;
                    } else {
                        value1 = (eyz_ezx_right - eyz_ezx_center) / Constants.STEP;
                    }

                    double value2 = 0;
                    double exz_exy_right;
                    double exz_exy_center = currentCell.getExz() + currentCell.getExy();
                    if (system[0][i + shiftY][j] instanceof BorderCell borderCell) {
                        exz_exy_right = borderCell.getExz() + borderCell.getExy();
                    } else {
                        Cell cell = system[0][i + shiftY][j];
                        exz_exy_right = cell.getEx() / 2.0;
                    }
                    if (isBottom) {
                        value2 = (exz_exy_center - exz_exy_right) / Constants.STEP;
                    } else {
                        value2 = (exz_exy_right - exz_exy_center) / Constants.STEP;
                    }

                    double valueHzx = currentCell.getHzx() + (-value1 - currentCell.getAstra() * currentCell.getHzx()) * (Constants.TAU / Constants.MU);
                    double valueHzy = currentCell.getHzy() + (-value2 + currentCell.getAstra() * currentCell.getHzy()) * (Constants.TAU / Constants.MU);

                    ((BorderCell) system[1][i][j]).setHzx(valueHzx);
                    ((BorderCell) system[1][i][j]).setHzy(valueHzy);
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
                    BorderCell currentCell = ((BorderCell) system[0][i][j]);

                    boolean isRight = j > Constants.SIZE / 2;
                    boolean isBottom = i > Constants.SIZE / 2;
                    int shiftY = (isBottom) ? -1 : 1;
                    int shiftX = (isRight) ? -1 : 1;

                    double value1;
                    double hzy_hzx_right;
                    double hzy_hzx_center = currentCell.getHzx() + currentCell.getHzy();
                    if (system[0][i + shiftY][j] instanceof BorderCell borderCell) {
                        hzy_hzx_right = borderCell.getHzy() + borderCell.getHzx();

                    } else {
                        Cell cell = system[0][i + shiftY][j];
                        hzy_hzx_right = cell.getHz() / 2.0;
                    }

                    if (isRight) {
                        value1 = (hzy_hzx_center - hzy_hzx_right) / Constants.STEP;
                    } else {
                        value1 = (hzy_hzx_right - hzy_hzx_center) / Constants.STEP;
                    }

                    double valueExy = currentCell.getExy() + (value1 - currentCell.getSigma() * currentCell.getExy()) * (Constants.TAU / Constants.EPSILON);

                    ((BorderCell) system[1][i][j]).setExy(valueExy);
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
                    BorderCell currentCell = ((BorderCell) system[0][i][j]);

                    boolean isRight = j > Constants.SIZE / 2;
                    boolean isBottom = i > Constants.SIZE / 2;
                    int shiftY = (isBottom) ? -1 : 1;
                    int shiftX = (isRight) ? -1 : 1;

                    double value;
                    double hzx_hzy_right;
                    double hzx_hzy_center = currentCell.getHzx() + currentCell.getHzy();

                    if (system[0][i][j + shiftX] instanceof BorderCell borderCell) {
                        hzx_hzy_right = borderCell.getHzy() + borderCell.getHzx();
                    } else {
                        Cell cell = system[0][i][j + shiftX];
                        hzx_hzy_right = cell.getHz() / 2.0;
                    }

                    if (isRight) {
                        value = (hzx_hzy_center - hzx_hzy_right) / Constants.STEP;
                    } else {
                        value = (hzx_hzy_right - hzx_hzy_center) / Constants.STEP;
                    }

                    double valueEyx = currentCell.getEyx() + (-value - currentCell.getSigma() * currentCell.getEyx()) * (Constants.TAU / Constants.EPSILON);

                    ((BorderCell) system[1][i][j]).setEyx(valueEyx);
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
