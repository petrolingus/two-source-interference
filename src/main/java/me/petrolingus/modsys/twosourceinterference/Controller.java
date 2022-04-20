package me.petrolingus.modsys.twosourceinterference;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

import java.util.function.Consumer;

public class Controller {

    public TextField aAmplitudeText;
    public TextField aCyclicFrequencyText;
    public TextField aWavelengthText;
    public TextField aInitialPhaseText;

    public TextField bAmplitudeText;
    public TextField bCyclicFrequencyText;
    public TextField bWavelengthText;
    public TextField bInitialPhaseText;

    public Slider distanceSlider;

    public Button button;


    public Canvas canvas;

    private Service service;

    private Service3d service3d;

    private LwjglApplication lwjglApplication;

    public void initialize() throws Exception {

        button.requestFocus();

        lwjglApplication = new LwjglApplication();
        LwjglApplication.canvas = canvas;

        new Thread(() -> {
            try {
                lwjglApplication.run();
            } catch (Exception e) {
                throw new RuntimeException("Lwjgl application interrupted", e);
            }
        }).start();

        service = new Service(canvas);
        service3d = new Service3d(canvas);

        service.setDistance(distanceSlider.getValue());
        distanceSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            service.setDistance(newValue.doubleValue());
            lwjglApplication.setBetween(newValue.floatValue());
        });

        setup(aAmplitudeText, service::setAmplitudeA);
        setup(aCyclicFrequencyText, service::setCyclicFrequencyA);
        setup(aWavelengthText, service::setWavelengthA);
        setup(aInitialPhaseText, service::setInitialPhaseA);

        setup(bAmplitudeText, service::setAmplitudeB);
        setup(bCyclicFrequencyText, service::setCyclicFrequencyB);
        setup(bWavelengthText, service::setWavelengthB);
        setup(bInitialPhaseText, service::setInitialPhaseB);

        GraphicsContext g = canvas.getGraphicsContext2D();
        g.fillRect(0, 0, canvas.getWidth(), canvas.getWidth());
    }

    public void onButtonClick() {
//        try {
//            lwjglApplication.run();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        service.start();
        service3d.start();
    }

    private void setup(TextField textField, Consumer<Double> function) {
        function.accept(Double.parseDouble(textField.getText()));
        bind(textField, function);
    }

    private void bind(TextField textField, Consumer<Double> function) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            double value = 0;
            try {
                value = Double.parseDouble(newValue);
            } finally {
                function.accept(value);
            }
        });
    }
}