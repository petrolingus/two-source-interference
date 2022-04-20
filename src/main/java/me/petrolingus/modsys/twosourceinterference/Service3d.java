package me.petrolingus.modsys.twosourceinterference;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

public class Service3d extends Service<Void> {

    private final Canvas canvas;

    public Service3d(Canvas canvas) {
        this.canvas = canvas;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                int width = 800;
                int height = 800;
                int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
                int whb = width * height * bpp;
                int[] pixels = new int[width * height];
                final WritableImage img = new WritableImage(width, height);
                PixelWriter pw = img.getPixelWriter();

                ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                executor.scheduleAtFixedRate(() -> {
                    Platform.runLater(() -> {
                        GraphicsContext g = canvas.getGraphicsContext2D();
//                        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                        g.drawImage(img, 0, 0);
                    });
                }, 0, 32, TimeUnit.MILLISECONDS);

                while (!isCancelled()) {

                    ByteBuffer buffer = LwjglApplication.buffer;

                    for (int y = 0; y < width; y++) {
                        for (int x = 0; x < height; x++) {
                            int i = (x + (width * y)) * bpp;
                            int r = buffer.get(i) & 0xFF;
                            int g = buffer.get(i + 1) & 0xFF;
                            int b = buffer.get(i + 2) & 0xFF;
                            pixels[(height - (y + 1)) * 800 + x] = (0xFF << 24) | (r << 16) | (g << 8) | b;
//                            pixels[800 * x + y] = ThreadLocalRandom.current().nextInt();
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
