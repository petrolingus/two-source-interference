package me.petrolingus.modsys.twosourceinterference.core;

public class Cell {
    public double dz;
    public double ez;
    public double ga;
    public double hx;
    public double hy;
    public boolean isPlate = false;

    public Cell(double ga) {
        this.ga = ga;
    }

    public double getValue() {
        return Math.sqrt(hx * hx + hy * hy) / 2.0;
    }

    public void makePlate() {
        this.isPlate = true;
    }
}
