package me.petrolingus.modsys.twosourceinterference.core;

public class Algorithm2 {

    int nwdth;
    int nhght;

    SourceType sourceType = SourceType.TWO;

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
        //        public double bz;
//        public double hz;
        public double ga;
        public double gb;
        public double gc;
        //        public double iz;
//        public double sz;
//        public double ihz;
//        public double shz;
//        public double gha;
//        public double ghb;
//        public double ghc;
//        public double nrj;
        public double hx;
        public double hy;
//        public double ex;
//        public double ey;

        public double getValue() {
            return dz * ez / 2.0 + Math.sqrt(hx * hx + hy * hy) / 2.0;
        }
    }

    public static class Detector {
        int i;
        int j;
    }

    double ddx;
    double ddy;
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
    int l, ic, jc, nsteps, npml, istart, jstart, ifin, jfin;
    double dt, T, epsz, epsilon, sigma, eaf;
    double xn, xxn, xnum, xd, curl_e;
    double t0, spread, pulse;
    double tau, chi1, del_exp;

    public Algorithm2(double width, int nwidth, double height, int nheight, int npml) {

        ddx = width / nwidth;
        ddy = height / nheight;

        ic = nwidth / 2;
        jc = nheight / 2;
        nwdth = nwidth;
        nhght = nheight;
        dt = ddx / 6e10; /* Time steps */
        epsz = 8.8e-12;

        yee = new Cell[nwidth][nheight];
        ihx = new double[nwidth][nheight];
        ihy = new double[nwidth][nheight];

        gi2 = new double[nwidth];
        gi3 = new double[nwidth];

        gj2 = new double[nheight];
        gj3 = new double[nwidth];

        fi1 = new double[nwidth];
        fi2 = new double[nwidth];
        fi3 = new double[nheight];

        fj1 = new double[nheight];
        fj2 = new double[nheight];
        fj3 = new double[nheight];

        for (int j = 0; j < nheight; j++) {
            for (int i = 0; i < nwidth; i++) {
                yee[i][j] = new Cell();
                yee[i][j].dz = 0.0;
                yee[i][j].hx = 0.0;
                yee[i][j].hy = 0.0;
                ihx[i][j] = 0.0;
                ihy[i][j] = 0.0;
                yee[i][j].ga = 1.0;
            }
        }

        //Calculate the PML parameters
        for (int i = 0; i < nwidth; i++) {
            gi2[i] = 1.0;
            gi3[i] = 1.0;
            fi1[i] = 0.0;
            fi2[i] = 1.0;
            fi3[i] = 1.0;
        }
        for (int j = 0; j < nheight; j++) {
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
            gi2[nwidth - 1 - i] = 1.0 / (1.0 + xn);
            gi3[i] = (1.0 - xn) / (1.0 + xn);
            gi3[nwidth - i - 1] = (1.0 - xn) / (1.0 + xn);
            xxn = (xnum - .5) / xd;
            xn = 0.25 * Math.pow(xxn, 3.0);
            fi1[i] = xn;
            fi1[nwidth - 2 - i] = xn;
            fi2[i] = 1.0 / (1.0 + xn);
            fi2[nwidth - 2 - i] = 1.0 / (1.0 + xn);
            fi3[i] = (1.0 - xn) / (1.0 + xn);
            fi3[nwidth - 2 - i] = (1.0 - xn) / (1.0 + xn);
        }
        for (int j = 0; j <= npml; j++) {
            xnum = npml - j;
            xd = npml;
            xxn = xnum / xd;
            xn = 0.25 * Math.pow(xxn, 3.0);

            gj2[j] = 1.0 / (1.0 + xn);
            gj2[nheight - 1 - j] = 1.0 / (1.0 + xn);
            gj3[j] = (1.0 - xn) / (1.0 + xn);
            gj3[nheight - j - 1] = (1.0 - xn) / (1.0 + xn);
            xxn = (xnum - .5) / xd;
            xn = 0.25 * Math.pow(xxn, 3.0);
            fj1[j] = xn;
            fj1[nheight - 2 - j] = xn;
            fj2[j] = 1.0 / (1.0 + xn);
            fj2[nheight - 2 - j] = 1.0 / (1.0 + xn);
            fj3[j] = (1.0 - xn) / (1.0 + xn);
            fj3[nheight - 2 - j] = (1.0 - xn) / (1.0 + xn);
        }

        istart = 0;
        jstart = 0;
        ifin = 0;
        jfin = 0;
        epsilon = 2;
        sigma = 0.01;
        chi1 = 1;

        tau = 0.001;
        del_exp = Math.exp(-dt / tau);
        tau = 1.e-6 * tau;

        for (int j = jstart; j <= jfin; j++) {
            for (int i = istart; i <= ifin; i++) {
                yee[i][j].ga = 1. / (epsilon + sigma * dt / epsz + chi1 * dt / tau);
                yee[i][j].gb = sigma * dt / epsz;
                yee[i][j].gc = chi1 * dt / tau;
            }
        }
        t0 = 0.0;
        spread = 40.0;
        T = 0;

        System.out.println("ALGORITHM2 WAS CREATED");
    }

    public void GenNextStep(double ddt) {

        T = T + ddt;
//        System.out.println(T);

        // Dz
        for (int j = 1; j < nhght; j++) {
            for (int i = 1; i < nwdth; i++) {
                yee[i][j].dz = gi3[i] * gj3[j] * yee[i][j].dz + gi2[i] * gj2[j] * ddt * (yee[i][j].hy - yee[i - 1][j].hy - yee[i][j].hx + yee[i][j - 1].hx);
            }
        }

//        pulse = (1.0 / (1.0 + Math.exp(-0.1 * (T - 50.0)))) * Math.sin(2 * Math.PI * 25000.0 * T);
        pulse = (1.0 / (1.0 + Math.exp(-0.1 * (T - 50.0)))) * Math.sin(2 * Math.PI * 0.05 * T);

        switch (sourceType) {
            case ONE -> yee[(int) (0.5 * nwdth)][(int) (0.5 * nhght)].dz = pulse;
            case TWO -> {
                yee[(int) (0.33 * nwdth)][(int) (0.33 * nhght)].dz += pulse;
                yee[(int) (0.66 * nwdth)][(int) (0.66 * nhght)].dz += pulse;
            }
            case FOUR -> {
                yee[(int) (0.45 * nwdth)][(int) (0.45 * nhght)].dz += pulse;
                yee[(int) (0.45 * nwdth)][(int) (0.55 * nhght)].dz += pulse;
                yee[(int) (0.55 * nwdth)][(int) (0.55 * nhght)].dz += pulse;
                yee[(int) (0.55 * nwdth)][(int) (0.45 * nhght)].dz += pulse;
            }
            case STRANGE -> {
                yee[(int) (0.5 * nwdth)][(int) (0.33 * nhght)].dz += pulse;
                for (int i = (int) (0.33 * nwdth); i < (int) (0.66 * nwdth); i++) {
                    yee[i][(int) (0.66 * nhght)].dz = 0;
                }
            }
            case PULSE -> {
                pulse = Math.exp(-(T - 50) * (T - 50) / 50);
                yee[ic][jc].dz = pulse;
            }
        }

        // Ez
        for (int j = 1; j < nhght - 1; j++) {
            for (int i = 1; i < nwdth - 1; i++) {
                yee[i][j].ez = yee[i][j].ga * yee[i][j].dz;
            }
        }

        // Hx
        for (int j = 0; j < nhght - 1; j++) {
            for (int i = 0; i < nwdth; i++) {
                curl_e = yee[i][j].ez - yee[i][j + 1].ez;
                ihx[i][j] = ihx[i][j] + fi1[i] * curl_e;
                yee[i][j].hx = fj3[j] * yee[i][j].hx + fj2[j] * ddt * (curl_e + ihx[i][j]);
            }
        }

        // Hy
        for (int j = 0; j <= nhght - 1; j++) {
            for (int i = 0; i < nwdth - 1; i++) {
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

