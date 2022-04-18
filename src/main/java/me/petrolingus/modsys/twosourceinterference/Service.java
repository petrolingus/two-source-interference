package me.petrolingus.modsys.twosourceinterference;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Service extends javafx.concurrent.Service<Void> {

    private final Canvas canvas;

    private double amplitudeA;
    private double cyclicFrequencyA;
    private double wavelengthA;
    private double initialPhaseA;

    private double amplitudeB;
    private double cyclicFrequencyB;
    private double wavelengthB;
    private double initialPhaseB;

    private double distance;

    private boolean isDistanceChanged = true;

    public Service(Canvas canvas) {
        this.canvas = canvas;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                double[][] values = new double[800][800];
                int width = (int) canvas.getWidth();
                int height = (int) canvas.getHeight();

                int[] pixels = new int[800 * 800];

                double[][] distances1 = new double[800][800];
                double[][] distances2 = new double[800][800];

                WritableImage img = new WritableImage(width, height);
                PixelWriter pw = img.getPixelWriter();

                ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                executor.scheduleAtFixedRate(() -> {
                    Platform.runLater(() -> {
                        GraphicsContext g = canvas.getGraphicsContext2D();
                        g.fillRect(0, 0, canvas.getWidth(), canvas.getWidth());
                        g.drawImage(img, 0, 0);
                    });
                }, 1000, 16, TimeUnit.MILLISECONDS);

                while (!isCancelled()) {

                    if (isDistanceChanged) {
                        double halfDistance = distance / 2.0;
                        double cx1 = 400 - halfDistance;
                        double cx2 = 400 + halfDistance;
                        for (int i = 0; i < 800; i++) {
                            for (int j = 0; j < 800; j++) {
                                distances1[i][j] = Math.sqrt(Math.pow((j - cx1), 2) + Math.pow((i - 400), 2));
                                distances2[i][j] = Math.sqrt(Math.pow((j - cx2), 2) + Math.pow((i - 400), 2));
                            }
                        }
                        isDistanceChanged = false;
                    }

                    double PI2 = 2 * Math.PI;
                    double k1 = PI2 / wavelengthA;
                    double k2 = PI2 / wavelengthB;
                    double t = (double) System.nanoTime() / 1_000_000_000;

                    for (int i = 0; i < height; i++) {
                        for (int j = 0; j < width; j++) {

                            double r1 = distances1[i][j];
                            double phi1 = cyclicFrequencyA * t - k1 * r1 + initialPhaseA;
                            double amplA = amplitudeA / r1;
                            double s1 = amplA * Math.sin(phi1);

                            double r2 = distances2[i][j];
                            double phi2 = cyclicFrequencyB * t - k2 * r2 + initialPhaseB;
                            double amplB = amplitudeB / r2;
                            double s2 = amplB * Math.sin(phi2);

                            double amplSum = amplA + amplB;
                            double value = valueMapper(s1 + s2, -amplSum, amplSum);
                            Color c = Color.hsb(240 * value, 1.0, 1.0);
                            int r = (int) Math.round(c.getRed() * 255.0);
                            int g = (int) Math.round(c.getGreen() * 255.0);
                            int b = (int) Math.round(c.getBlue() * 255.0);
                            int color = 0xFF << 24 | r << 16 | g << 8 | b;

                            pixels[i * 800 + j] = color;
                        }
                    }

                    pw.setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixels, 0, width);

                }

                executor.shutdown();

                return null;
            }
        };
    }

    private double valueMapper(double value, double min, double max) {
        return ((value - min) / (max - min));
    }

    public Void setDistance(double distance) {
        this.distance = distance;
        isDistanceChanged = true;
        return null;
    }

    public Void setAmplitudeA(double amplitudeA) {
        this.amplitudeA = amplitudeA;
        return null;
    }

    public Void setCyclicFrequencyA(double cyclicFrequencyA) {
        this.cyclicFrequencyA = cyclicFrequencyA;
        return null;
    }

    public Void setWavelengthA(double wavelengthA) {
        this.wavelengthA = wavelengthA;
        return null;
    }

    public Void setInitialPhaseA(double initialPhaseA) {
        this.initialPhaseA = initialPhaseA;
        return null;
    }

    public Void setAmplitudeB(double amplitudeB) {
        this.amplitudeB = amplitudeB;
        return null;
    }

    public Void setCyclicFrequencyB(double cyclicFrequencyB) {
        this.cyclicFrequencyB = cyclicFrequencyB;
        return null;
    }

    public Void setWavelengthB(double wavelengthB) {
        this.wavelengthB = wavelengthB;
        return null;
    }

    public Void setInitialPhaseB(double initialPhaseB) {
        this.initialPhaseB = initialPhaseB;
        return null;
    }
}
