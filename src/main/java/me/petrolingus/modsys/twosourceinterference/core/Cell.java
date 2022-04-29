package me.petrolingus.modsys.twosourceinterference.core;

import java.util.concurrent.ThreadLocalRandom;

public class Cell {

    public double Ex1;
    public double Ex2;
    public double Ey1;
    public double Ey2;
    public double Hz;
    public double Jx;
    public double Jy;

    public Cell(double ex1, double ex2, double ey1, double ey2, double hz, double jx, double jy) {
        Ex1 = ex1;
        Ex2 = ex2;
        Ey1 = ey1;
        Ey2 = ey2;
        Hz = hz;
        Jx = jx;
        Jy = jy;
    }

    public double getValue() {
        return Ex1 * Ex1 + Ex2 * Ex2 + Ey1 * Ey1 + Ey2 * Ey2;
    }

}
