package me.petrolingus.modsys.twosourceinterference.core.cell;

public class BorderCell extends Cell {

    double exy;
    double exz;
    double eyx;
    double eyz;
    double hzx;
    double hzy;
    double sigma;
    double astra;

    public BorderCell(double sigma, double astra) {
        super(CellType.BORDER);
        this.exy = 0;
        this.exz = 0;
        this.eyx = 0;
        this.eyz = 0;
        this.hzx = 0;
        this.hzy = 0;
        this.sigma = sigma;
        this.astra = astra;
    }

    @Override
    public double getValue() {
        double ex = getEx();
        double ey = getEy();
        return Math.sqrt(ex * ex + ey * ey);
    }

    @Override
    public double getEx() {
        return exy + exz;
    }

    @Override
    public double getEy() {
        return eyx + eyz;
    }

    @Override
    public double getHz() {
        return hzy + hzx;
    }

    @Override
    public void setEx(double ex) {
        this.exy = this.exz = ex / 2.0;
    }

    @Override
    public void setEy(double ey) {
        this.eyx = this.eyz = ey / 2.0;
    }

    @Override
    public void setHz(double hz) {
        this.hzy = this.hzx = hz / 2.0;
    }

    public void setExy(double exy) {
        this.exy = exy;
    }

    public void setExz(double exz) {
        this.exz = exz;
    }

    public void setEyx(double eyx) {
        this.eyx = eyx;
    }

    public void setEyz(double eyz) {
        this.eyz = eyz;
    }

    public void setHzx(double hzx) {
        this.hzx = hzx;
    }

    public void setHzy(double hzy) {
        this.hzy = hzy;
    }

    public double getExy() {
        return exy;
    }

    public double getExz() {
        return exz;
    }

    public double getEyx() {
        return eyx;
    }

    public double getEyz() {
        return eyz;
    }

    public double getHzx() {
        return hzx;
    }

    public double getHzy() {
        return hzy;
    }

    public double getSigma() {
        return sigma;
    }

    public double getAstra() {
        return astra;
    }
}
