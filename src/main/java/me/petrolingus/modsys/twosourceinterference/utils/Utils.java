package me.petrolingus.modsys.twosourceinterference.utils;

import me.petrolingus.modsys.twosourceinterference.LwjglApplication;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class Utils {

    public static String loadShader(String shaderName) {
        try {
            URI resource = Objects.requireNonNull(LwjglApplication.class.getResource("shaders/" + shaderName)).toURI();
            return String.join("\n", Files.readAllLines(Paths.get(Objects.requireNonNull(resource))));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
