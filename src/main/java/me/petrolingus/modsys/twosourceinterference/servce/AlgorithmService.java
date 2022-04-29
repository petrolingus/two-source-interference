package me.petrolingus.modsys.twosourceinterference.servce;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import me.petrolingus.modsys.twosourceinterference.core.Algorithm;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("DuplicatedCode")
public class AlgorithmService extends Service<Void> {

    private final Canvas canvas;

    public AlgorithmService(Canvas canvas) {
        this.canvas = canvas;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() {

                int width = (int) canvas.getWidth();
                int height = (int) canvas.getHeight();

                int[] pixels = new int[width * height];

                WritableImage img = new WritableImage(width, height);
                PixelWriter pw = img.getPixelWriter();

                ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                executor.scheduleAtFixedRate(() -> {
                    Platform.runLater(() -> {
                        GraphicsContext g = canvas.getGraphicsContext2D();
                        g.fillRect(0, 0, canvas.getWidth(), canvas.getWidth());
                        g.setStroke(Color.GRAY);
                        g.drawImage(img, 0, 0);
                        g.strokeRect(0, 0, canvas.getWidth(), canvas.getWidth());
                    });
                }, 1000, 16, TimeUnit.MILLISECONDS);

                int n = 10;
                double[][] data = new double[n][n];
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        data[i][j] = 360 * ThreadLocalRandom.current().nextDouble();
                    }
                }

                Algorithm algorithm = new Algorithm(n);

                while (!isCancelled()) {

                    for (int i = 0; i < height; i++) {
                        for (int j = 0; j < width; j++) {
                            Color c = Color.hsb(data[i/(width/n)][j/(height/n)], 1.0, 1.0);
                            int r = (int) Math.round(c.getRed() * 255.0);
                            int g = (int) Math.round(c.getGreen() * 255.0);
                            int b = (int) Math.round(c.getBlue() * 255.0);
                            int color = 0xFF << 24 | r << 16 | g << 8 | b;
                            pixels[i * width + j] = color;
                        }
                    }

                    pw.setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixels, 0, width);

                }

                executor.shutdown();

                return null;
            }
        };
    }
}