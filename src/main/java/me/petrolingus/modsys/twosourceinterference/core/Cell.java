package me.petrolingus.modsys.twosourceinterference.core;

public class Cell {

    public enum Type {
        BORDER,
        MAIN
    }

    public double Ex;
    public double Ey;
    public double Hz;

    public Type type;

    public Cell(double ex, double ey, double hz, Type type) {
        Ex = ex;
        Ey = ey;
        Hz = hz;
        this.type = type;
    }

    public double getValue() {
        if (type.equals(Type.BORDER)) {
            return 0;
        } else {
//            return Math.sqrt(Ex * Ex + Ey * Ey);
            return 100;
        }
    }
}
