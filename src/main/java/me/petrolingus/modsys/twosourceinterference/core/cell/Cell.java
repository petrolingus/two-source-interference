package me.petrolingus.modsys.twosourceinterference.core.cell;

public abstract class Cell {

    public CellType cellType;

    public Cell(CellType cellType) {
        this.cellType = cellType;
    }

    public double getType() {
        return switch (cellType) {
            case BORDER -> 0;
            case MAIN -> 100;
            case SOURCE -> 270;
        };
    }

    public abstract double getValue();
    public abstract double getEx();
    public abstract double getEy();
    public abstract double getHz();
    public abstract void setEx(double ex);
    public abstract void setEy(double ey);
    public abstract void setHz(double hz);
}
