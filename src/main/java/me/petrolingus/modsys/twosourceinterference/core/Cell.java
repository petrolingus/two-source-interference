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
        return dz;
    }

    public void makePlate() {
        this.isPlate = true;
    }
}
