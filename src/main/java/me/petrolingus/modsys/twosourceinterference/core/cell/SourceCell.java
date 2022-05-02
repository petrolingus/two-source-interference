package me.petrolingus.modsys.twosourceinterference.core.cell;

public class SourceCell extends Cell {

    private double ex;
    private double ey;
    private double hz;

    public SourceCell() {
        super(CellType.SOURCE);
        this.ex = 0;
        this.ey = 0;
        this.hz = 0;
    }

    @Override
    public double getValue() {
        return Math.sqrt(ex * ex + ey * ey);
    }

    @Override
    public double getEx() {
        return ex;
    }

    @Override
    public double getEy() {
        return ey;
    }

    @Override
    public double getHz() {
        return hz;
    }

    @Override
    public void setEx(double ex) {
        this.ex = ex;
    }

    @Override
    public void setEy(double ey) {
        this.ey = ey;
    }

    @Override
    public void setHz(double hz) {
        this.hz = hz;
    }

    public void generateNext(double t) {
        hz = 10000 * Math.sin(1000 * t) / (1 + Math.exp(-t + Math.PI));
    }
}
