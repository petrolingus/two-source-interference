package me.petrolingus.modsys.twosourceinterference.core;

public class Constants {

    // Vacuum permittivity (electric)
    public static final double EPSILON = 8.8541878128; //e-12

    // Vacuum permeability (magnetic)
    public static final double MU = 1.25663706212; //e-6

    // Electrical resistance and conductance
    public static final double SIGMA = 1;


    // Reflection coefficient
    public static final double R = 1;

    // Absorbent layer thickness
    public static final int D = 4;

    // Degree of absorbent layer
    public static final int N = 1;

    // Maximum electrical conductivity of absorbent layer
    public static final double SIGMA_MAX = -Math.log(R) * Math.sqrt(EPSILON / MU) * ((N + 1.0) / (2.0 * D));


    // Simulation area side size
    public static final int SIDE = 80;

    public static final int SIZE = SIDE + 2 * D;

    // Spatial step
    public static final double STEP = 0.0001;

    // Time step
    public static final double TAU = 0.00005;


    // Other
    public static final double C0 = SIGMA * TAU / (2.0 * EPSILON);
    public static final double C1 = 1.0 - C0;
    public static final double C2 = 1.0 + C0;
    public static final double C3 = C1 / C2;
    public static final double C4 = (TAU / EPSILON) / C2;
    public static final double C5 = (2.0 * TAU / EPSILON) / C2;
    public static final double C6 = TAU / MU;
    public static final double C7 = SIGMA * TAU / 2.0;
    public static final double C8 = EPSILON - C7;
    public static final double C9 = EPSILON + C7;
    public static final double C10 = C8 / C9;
    public static final double C11 = 2.0 * TAU / C9;


}
