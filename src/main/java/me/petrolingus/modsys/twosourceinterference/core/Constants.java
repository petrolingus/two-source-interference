package me.petrolingus.modsys.twosourceinterference.core;

public class Constants {

    // Reflection coefficient
    public static final double ALPHA = 0.1;
    // Absorbent layer thickness
    public static final int PML_LAYERS = 16;
    // Degree of absorbent layer
    public static final int N = 2;

    // Simulation area side size
    public static final int SIDE = 256;
    public static final int SIZE = SIDE + 2 * PML_LAYERS;

    public static final double TIME_START = 50.0;
    public static double AMPLITUDE = 2;
    public static double OMEGA = 0.05;

    public static boolean clearRequest = false;

    // Time step
    public static double TAU = 0.5;

    public static SourceType sourceType;

    public static void setSourceType(SourceType sourceType) {
        Constants.sourceType = sourceType;
    }

    public static void setTimeMul(double timeMul) {
        TAU = (float) timeMul;
    }

    public static void setAmplitude(double amplitude) {
        AMPLITUDE = amplitude;
    }

    public static void setOmega(double omega) {
        OMEGA = omega;
    }
}
