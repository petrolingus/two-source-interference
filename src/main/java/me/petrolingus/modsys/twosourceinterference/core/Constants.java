package me.petrolingus.modsys.twosourceinterference.core;

public class Constants {

    // Reflection coefficient
    public static final double ALPHA = 0.1;
    // Absorbent layer thickness
    public static final int PML_LAYERS = 16;
    // Degree of absorbent layer
    public static final int N = 2;

    // Simulation area side size
    public static final int SIDE = 200;
    public static final int SIZE = SIDE + 2 * PML_LAYERS;

    // Time step
    public static final double TAU = 0.7;
}
