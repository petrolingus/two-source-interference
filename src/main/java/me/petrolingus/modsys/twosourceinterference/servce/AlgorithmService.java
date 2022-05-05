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
import me.petrolingus.modsys.twosourceinterference.core.Algorithm2;
import me.petrolingus.modsys.twosourceinterference.core.Constants;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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
            protected Void call() throws Exception {

                int width = (int) canvas.getWidth();
                int height = (int) canvas.getHeight();

                System.out.println("Width: " + width);

                int[] pixels = new int[width * height];

                WritableImage img = new WritableImage(width, height);
                PixelWriter pw = img.getPixelWriter();

                ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                executor.scheduleAtFixedRate(() -> Platform.runLater(() -> {
                    GraphicsContext g = canvas.getGraphicsContext2D();
                    g.fillRect(0, 0, canvas.getWidth(), canvas.getWidth());
                    g.setStroke(Color.GRAY);
                    g.drawImage(img, 0, 0);
                    g.strokeRect(0, 0, canvas.getWidth(), canvas.getWidth());
                }), 0, 16, TimeUnit.MILLISECONDS);

                int n = Constants.SIZE;
                double[][] data;

//                System.out.println("Algorithm was created");
//                Algorithm algorithm = new Algorithm();
                Algorithm2 algorithm2 = new Algorithm2(n, n, Constants.D);

                double maxEnergy = 0;

                while (!isCancelled()) {

                    algorithm2.GenNextStep(Constants.TAU);
                    data = algorithm2.getFieldValues();

                    double currentEnergy = Math.abs(algorithm2.getFillEnergy());

                    if (currentEnergy > maxEnergy) {
                        maxEnergy = currentEnergy;
                        System.out.println(currentEnergy);
                    }

                    try {
                        for (int i = 0; i < height; i++) {
                            for (int j = 0; j < width; j++) {

                                int row = (int) Math.floor((double) i / ((double) width / n));
                                int column = (int) Math.floor((double) j / ((double) height / n));

                                Color c;
                                if (row > Constants.D && row < Constants.SIZE - Constants.D && column > Constants.D && column < Constants.SIZE - Constants.D) {
                                    c = Color.hsb(data[row][column], 1.0, 1.0);
                                } else {
                                    c = Color.hsb(data[row][column], 1.0, 0.9);
                                }

                                int r = (int) Math.round(c.getRed() * 255.0);
                                int g = (int) Math.round(c.getGreen() * 255.0);
                                int b = (int) Math.round(c.getBlue() * 255.0);
                                int color = 0xFF << 24 | r << 16 | g << 8 | b;
                                pixels[i * width + j] = color;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    pw.setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixels, 0, width);

                }

                executor.shutdown();

                return null;
            }
        };
    }
}
