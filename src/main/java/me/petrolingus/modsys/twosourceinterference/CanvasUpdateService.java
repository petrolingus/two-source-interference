package me.petrolingus.modsys.twosourceinterference;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CanvasUpdateService extends Service<Void> {

    private final Canvas canvas;

    public CanvasUpdateService(Canvas canvas) {
        this.canvas = canvas;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() {

                int width = (int) canvas.getWidth();
                int height = (int) canvas.getHeight();;
                int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.

                // Update canvas every 16 ms
                final WritableImage img = new WritableImage(width, height);
                ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                executor.scheduleAtFixedRate(() -> Platform.runLater(() -> {
                    GraphicsContext g = canvas.getGraphicsContext2D();
                    g.drawImage(img, 0, 0);
                }), 0, 16, TimeUnit.MILLISECONDS);

                // Prepare canvas data
                int[] pixels = new int[width * height];
                PixelWriter pw = img.getPixelWriter();
                while (!isCancelled()) {
                    ByteBuffer buffer = LwjglApplication.buffer;
                    for (int y = 0; y < width; y++) {
                        for (int x = 0; x < height; x++) {
                            int i = (x + (width * y)) * bpp;
                            int r = buffer.get(i) & 0xFF;
                            int g = buffer.get(i + 1) & 0xFF;
                            int b = buffer.get(i + 2) & 0xFF;
                            pixels[(height - (y + 1)) * 800 + x] = (0xFF << 24) | (r << 16) | (g << 8) | b;
                        }
                    }
                    pw.setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixels, 0, width);
                }

                // When service done we need to shut down the scheduler executor
                executor.shutdown();

                return null;
            }
        };
    }
}
