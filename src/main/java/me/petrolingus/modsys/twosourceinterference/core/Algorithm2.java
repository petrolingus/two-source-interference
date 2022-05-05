package me.petrolingus.modsys.twosourceinterference.core;

public class Algorithm2 {

    int nWidth;
    int nHeight;

    SourceType sourceType = SourceType.ONE;

    Cell[][] yee;
    double[][] ihx;
    double[][] ihy;
    double[] gi2;
    double[] gi3;
    double[] gj2;
    double[] gj3;
    double[] fi1;
    double[] fi2;
    double[] fi3;
    double[] fj1;
    double[] fj2;
    double[] fj3;

    private double time;
    double xn, xxn, xnum, xd, curl_e;
    double pulse;

    public enum SourceType {
        ONE,
        TWO,
        FOUR,
        STRANGE,
        PULSE
    }

    public static class Cell {
        public double dz;
        public double ez;
        public double ga;
        public double hx;
        public double hy;
        public double getValue() {
            return dz * ez / 2.0;
        }
        public Cell() {
            this.dz = 0;
            this.ez = 0;
            this.ga = 0;
            this.hx = 0;
            this.hy = 0;
        }
    }

    public Algorithm2(double width, int nWidth, double height, int nHeight, int npml) {

        this.nWidth = nWidth;
        this.nHeight = nHeight;

        yee = new Cell[nWidth][nHeight];
        ihx = new double[nWidth][nHeight];
        ihy = new double[nWidth][nHeight];

        gi2 = new double[nWidth];
        gi3 = new double[nWidth];

        gj2 = new double[nHeight];
        gj3 = new double[nWidth];

        fi1 = new double[nWidth];
        fi2 = new double[nWidth];
        fi3 = new double[nHeight];

        fj1 = new double[nHeight];
        fj2 = new double[nHeight];
        fj3 = new double[nHeight];

        for (int j = 0; j < nHeight; j++) {
            for (int i = 0; i < nWidth; i++) {
                yee[i][j] = new Cell();
                yee[i][j].dz = 0.0;
                yee[i][j].hx = 0.0;
                yee[i][j].hy = 0.0;
                yee[i][j].ga = 1.0;
                ihx[i][j] = 0.0;
                ihy[i][j] = 0.0;
            }
        }

        //Calculate the PML parameters
        for (int i = 0; i < nWidth; i++) {
            gi2[i] = 1.0;
            gi3[i] = 1.0;
            fi1[i] = 0.0;
            fi2[i] = 1.0;
            fi3[i] = 1.0;
        }
        for (int j = 0; j < nHeight; j++) {
            gj2[j] = 1.0;
            gj3[j] = 1.0;
            fj1[j] = 0.0;
            fj2[j] = 1.0;
            fj3[j] = 1.0;
        }

        for (int i = 0; i <= npml; i++) {
            xnum = npml - i;
            xd = npml;
            xxn = xnum / xd;
            xn = 0.25 * Math.pow(xxn, 3.0);
            gi2[i] = 1.0 / (1.0 + xn);
            gi2[nWidth - 1 - i] = 1.0 / (1.0 + xn);
            gi3[i] = (1.0 - xn) / (1.0 + xn);
            gi3[nWidth - i - 1] = (1.0 - xn) / (1.0 + xn);
            xxn = (xnum - .5) / xd;
            xn = 0.25 * Math.pow(xxn, 3.0);
            fi1[i] = xn;
            fi1[nWidth - 2 - i] = xn;
            fi2[i] = 1.0 / (1.0 + xn);
            fi2[nWidth - 2 - i] = 1.0 / (1.0 + xn);
            fi3[i] = (1.0 - xn) / (1.0 + xn);
            fi3[nWidth - 2 - i] = (1.0 - xn) / (1.0 + xn);
        }
        for (int j = 0; j <= npml; j++) {
            xnum = npml - j;
            xd = npml;
            xxn = xnum / xd;
            xn = 0.25 * Math.pow(xxn, 3.0);

            gj2[j] = 1.0 / (1.0 + xn);
            gj2[nHeight - 1 - j] = 1.0 / (1.0 + xn);
            gj3[j] = (1.0 - xn) / (1.0 + xn);
            gj3[nHeight - j - 1] = (1.0 - xn) / (1.0 + xn);
            xxn = (xnum - .5) / xd;
            xn = 0.25 * Math.pow(xxn, 3.0);
            fj1[j] = xn;
            fj1[nHeight - 2 - j] = xn;
            fj2[j] = 1.0 / (1.0 + xn);
            fj2[nHeight - 2 - j] = 1.0 / (1.0 + xn);
            fj3[j] = (1.0 - xn) / (1.0 + xn);
            fj3[nHeight - 2 - j] = (1.0 - xn) / (1.0 + xn);
        }

        System.out.println("ALGORITHM2 WAS CREATED");
    }

    public void GenNextStep(double ddt) {

        time = time + ddt;

        // Dz
        for (int j = 1; j < nHeight; j++) {
            for (int i = 1; i < nWidth; i++) {
                yee[i][j].dz = gi3[i] * gj3[j] * yee[i][j].dz + gi2[i] * gj2[j] * ddt * (yee[i][j].hy - yee[i - 1][j].hy - yee[i][j].hx + yee[i][j - 1].hx);
            }
        }

//        pulse = (1.0 / (1.0 + Math.exp(-0.1 * (T - 50.0)))) * Math.sin(2 * Math.PI * 25000.0 * T);
        pulse = (1.0 / (1.0 + Math.exp(-0.1 * (time - 50.0)))) * Math.sin(2 * Math.PI * 0.05 * time);

        switch (sourceType) {
            case ONE -> yee[(int) (0.5 * nWidth)][(int) (0.5 * nHeight)].dz += pulse;
            case TWO -> {
                yee[(int) (0.33 * nWidth)][(int) (0.33 * nHeight)].dz += pulse;
                yee[(int) (0.66 * nWidth)][(int) (0.66 * nHeight)].dz += pulse;
            }
            case FOUR -> {
                yee[(int) (0.45 * nWidth)][(int) (0.45 * nHeight)].dz += pulse;
                yee[(int) (0.45 * nWidth)][(int) (0.55 * nHeight)].dz += pulse;
                yee[(int) (0.55 * nWidth)][(int) (0.55 * nHeight)].dz += pulse;
                yee[(int) (0.55 * nWidth)][(int) (0.45 * nHeight)].dz += pulse;
            }
            case STRANGE -> {
                yee[(int) (0.5 * nWidth)][(int) (0.33 * nHeight)].dz += pulse;
                for (int i = (int) (0.33 * nWidth); i < (int) (0.66 * nWidth); i++) {
                    yee[i][(int) (0.66 * nHeight)].dz = 0;
                }
            }
            case PULSE -> {
                pulse = Math.exp(-(time - 50) * (time - 50) / 50);
                yee[(int) (0.5 * nWidth)][(int) (0.5 * nHeight)].dz = pulse;
            }
        }

        // Ez
        for (int j = 1; j < nHeight - 1; j++) {
            for (int i = 1; i < nWidth - 1; i++) {
                yee[i][j].ez = yee[i][j].ga * yee[i][j].dz;
            }
        }

        // Hx
        for (int j = 0; j < nHeight - 1; j++) {
            for (int i = 0; i < nWidth; i++) {
                curl_e = yee[i][j].ez - yee[i][j + 1].ez;
                ihx[i][j] = ihx[i][j] + fi1[i] * curl_e;
                yee[i][j].hx = fj3[j] * yee[i][j].hx + fj2[j] * ddt * (curl_e + ihx[i][j]);
            }
        }

        // Hy
        for (int j = 0; j <= nHeight - 1; j++) {
            for (int i = 0; i < nWidth - 1; i++) {
                curl_e = yee[i + 1][j].ez - yee[i][j].ez;
                ihy[i][j] = ihy[i][j] + fj1[j] * curl_e;
                yee[i][j].hy = fi3[i] * yee[i][j].hy + fi2[i] * ddt * (curl_e + ihy[i][j]);
            }
        }
    }

    public double[][] getFieldValues() {
        double[][] data = new double[Constants.SIZE][Constants.SIZE];
        for (int i = 0; i < Constants.SIZE; i++) {
            for (int j = 0; j < Constants.SIZE; j++) {
                int startAngle = 180;
                double value =  yee[i][j].getValue() * 10000 + startAngle;
                int max = 270 + startAngle;
                if (value > max) {
                    data[i][j] = max;
                } else {
                    data[i][j] = value;
                }
            }
        }
        return data;
    }
}

