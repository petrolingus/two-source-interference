package me.petrolingus.modsys.twosourceinterference.core;

import me.petrolingus.modsys.twosourceinterference.utils.Utils;

import java.util.Arrays;

public class Algorithm {

    int nWidth;
    int nHeight;

    SourceType sourceType = SourceType.TWO;

    private final Cell[][] cells;
    double[][] ihx;
    double[][] ihy;
    double[] gi2;
    double[] gi3;
    double[] fi1;
    double[] fi2;
    double[] fi3;

    private double time;
    double pulse;

    public Algorithm(int nWidth, int nHeight, int pmlLayers) {

        this.nWidth = nWidth;
        this.nHeight = nHeight;

        cells = new Cell[nWidth][nHeight];

        ihx = new double[nWidth][nHeight];
        ihy = new double[nWidth][nHeight];

        gi2 = new double[nWidth];
        gi3 = new double[nWidth];

        fi1 = new double[nWidth];
        fi2 = new double[nWidth];
        fi3 = new double[nHeight];

        for (int j = 0; j < nHeight; j++) {
            for (int i = 0; i < nWidth; i++) {
                cells[i][j] = new Cell(1.0);
            }
        }

        //Calculate the PML parameters
        Arrays.fill(gi2, 1.0);
        Arrays.fill(gi3, 1.0);
        Arrays.fill(fi2, 1.0);
        Arrays.fill(fi3, 1.0);

        for (int i = 0; i < pmlLayers; i++) {
            double xnum = pmlLayers - i;
            double xxn = xnum / pmlLayers;
            double xn = Constants.ALPHA * Math.pow(xxn, Constants.N);

            gi2[i] = 1.0 / (1.0 + xn);
            gi2[nWidth - 1 - i] = 1.0 / (1.0 + xn);

            gi3[i] = (1.0 - xn) / (1.0 + xn);
            gi3[nWidth - 1 - i] = (1.0 - xn) / (1.0 + xn);

            xxn = (xnum - 0.5) / pmlLayers;
            xn = Constants.ALPHA * Math.pow(xxn, Constants.N);

            fi1[i] = xn;
            fi1[nWidth - 2 - i] = xn;

            fi2[i] = 1.0 / (1.0 + xn);
            fi2[nWidth - 2 - i] = 1.0 / (1.0 + xn);

            fi3[i] = (1.0 - xn) / (1.0 + xn);
            fi3[nWidth - 2 - i] = (1.0 - xn) / (1.0 + xn);
        }

        System.out.println("ALGORITHM2 WAS CREATED");
    }

    public void next(double ddt) {

        // Dz
        for (int j = 1; j < nHeight - 1; j++) {
            for (int i = 1; i < nWidth - 1; i++) {
                double a = gi3[i] * gi3[j] * cells[i][j].dz;
                cells[i][j].dz = a + gi2[i] * gi2[j] * ddt * (cells[i][j].hy - cells[i - 1][j].hy - cells[i][j].hx + cells[i][j - 1].hx);
            }
        }

        time = time + ddt;
        pulse = (1.0 / (1.0 + Math.exp(-0.1 * (time - 50.0)))) * Math.sin(2 * Math.PI * 0.1 * time);

        switch (sourceType) {
            case ONE -> cells[(int) (0.5 * nWidth)][(int) (0.5 * nHeight)].dz = pulse;
            case TWO -> {
                cells[(int) (0.33 * nWidth)][(int) (0.33 * nHeight)].dz = pulse;
                cells[(int) (0.66 * nWidth)][(int) (0.66 * nHeight)].dz = pulse;
            }
            case FOUR -> {
                cells[(int) (0.45 * nWidth)][(int) (0.45 * nHeight)].dz = pulse;
                cells[(int) (0.45 * nWidth)][(int) (0.55 * nHeight)].dz = pulse;
                cells[(int) (0.55 * nWidth)][(int) (0.55 * nHeight)].dz = pulse;
                cells[(int) (0.55 * nWidth)][(int) (0.45 * nHeight)].dz = pulse;
            }
            case PLATE -> {
                cells[(int) (0.5 * nWidth)][(int) (0.33 * nHeight)].dz = pulse;
                for (int i = (int) (0.33 * nWidth); i < (int) (0.66 * nWidth); i++) {
                    cells[i][(int) (0.66 * nHeight)].makePlate();
                    cells[i][(int) (0.66 * nHeight)].dz = 0;
                }
            }
            case PULSE -> {
                pulse = Math.exp(-(time - 50) * (time - 50) / 50);
                cells[(int) (0.5 * nWidth)][(int) (0.5 * nHeight)].dz = pulse;
            }
        }

        // Ez
        for (int j = 1; j < nHeight - 1; j++) {
            for (int i = 1; i < nWidth - 1; i++) {
                cells[i][j].ez = cells[i][j].ga * cells[i][j].dz;
            }
        }

        // Hx
        for (int j = 0; j < nHeight - 1; j++) {
            for (int i = 0; i < nWidth; i++) {
                double curl_e = cells[i][j].ez - cells[i][j + 1].ez;
                ihx[i][j] = ihx[i][j] + fi1[i] * curl_e;
                cells[i][j].hx = fi3[j] * cells[i][j].hx + fi2[j] * ddt * (curl_e + ihx[i][j]);
            }
        }

        // Hy
        for (int j = 0; j <= nHeight - 1; j++) {
            for (int i = 0; i < nWidth - 1; i++) {
                double curl_e = cells[i + 1][j].ez - cells[i][j].ez;
                ihy[i][j] = ihy[i][j] + fi1[j] * curl_e;
                cells[i][j].hy = fi3[i] * cells[i][j].hy + fi2[i] * ddt * (curl_e + ihy[i][j]);
            }
        }
    }

    public double[][] getValues() {
        double[][] data = new double[Constants.SIZE][Constants.SIZE];
        for (int i = 0; i < Constants.SIZE; i++) {
            for (int j = 0; j < Constants.SIZE; j++) {
                if (cells[i][j].isPlate) {
                    data[i][j] = -1;
                } else {
                    double value =  cells[i][j].getValue() * 1000;
                    data[i][j] = Utils.clamp(value, 0, 270);
                }
            }
        }
        return data;
    }
}

