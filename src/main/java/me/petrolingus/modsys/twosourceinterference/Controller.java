package me.petrolingus.modsys.twosourceinterference;

import javafx.collections.FXCollections;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import me.petrolingus.modsys.twosourceinterference.core.Constants;
import me.petrolingus.modsys.twosourceinterference.core.SourceType;
import me.petrolingus.modsys.twosourceinterference.servce.CanvasUpdateService;
import me.petrolingus.modsys.twosourceinterference.utils.MouseInput;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class Controller {

    public ChoiceBox<SourceType> choiceBox;

    // Source A
    public TextField amplitudeText;
    public TextField cyclicFrequencyText;

    // Options
    public TextField timeMulText;
    public ColorPicker maxColorPicker;
    public ColorPicker minColorPicker;
    public Slider colorSlider;
    public ToggleGroup viewType;
    public RadioButton view3dRadioButton;
    public RadioButton view2dRadioButton;

    // View
    public Canvas canvas;

    private LwjglApplication lwjglApplication;

    public void initialize() {

        lwjglApplication = new LwjglApplication();
        LwjglApplication.canvas = canvas;

        choiceBox.setItems(FXCollections.observableArrayList(SourceType.values()));
        choiceBox.setValue(SourceType.ONE);
        Constants.setSourceType(SourceType.ONE);
        choiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            Constants.setSourceType(newValue);
            onClearButton();
        });

        // Text fields setup
        setup(amplitudeText, Constants::setAmplitude);
        setup(cyclicFrequencyText, Constants::setOmega);
        setup(timeMulText, Constants::setTimeMul);

        // Sliders setup
        setup(colorSlider, lwjglApplication::setColorDelimiter);

        // Color pickers setup
        setupColorPicker(minColorPicker, lwjglApplication::setMinColor);
        setupColorPicker(maxColorPicker, lwjglApplication::setMaxColor);

        GraphicsContext g = canvas.getGraphicsContext2D();
        g.fillRect(0, 0, canvas.getWidth(), canvas.getWidth());

        lwjglApplication.setIs3DActive(false);
        viewType.selectedToggleProperty().addListener((observable, oldValue, newValue) -> lwjglApplication.setIs3DActive(!observable.getValue().equals(view3dRadioButton)));

        MouseInput mouseInput = new MouseInput();
        lwjglApplication.setMouseInput(mouseInput);
        canvas.addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
            mouseInput.setMousePos(event.getX(), event.getY());
            event.consume();
        });
        canvas.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            mouseInput.setMousePos(event.getX(), event.getY());
            event.consume();
        });
        canvas.addEventFilter(MouseEvent.DRAG_DETECTED, event -> {
            System.out.println("DRAG_DETECTED");
            mouseInput.setDragged(true);
            event.consume();
        });
        canvas.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            System.out.println("MOUSE_RELEASED");
            mouseInput.setDragged(false);
            event.consume();
        });

        new Thread(() -> {
            try {
                lwjglApplication.run();
            } catch (Exception e) {
                throw new RuntimeException("Lwjgl application interrupted", e);
            }
        }).start();
        CanvasUpdateService service3d = new CanvasUpdateService(canvas);
        service3d.start();
    }

    public void onClearButton() {
        Constants.clearRequest = true;
    }

    private void setupColorPicker(ColorPicker colorPicker, Consumer<Vector3f> function) {
        Color color = colorPicker.getValue();
        function.accept(new Vector3f((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue()));
        colorPicker.valueProperty().addListener((observable, oldValue, newValue) -> function.accept(new Vector3f((float) newValue.getRed(), (float) newValue.getGreen(), (float) newValue.getBlue())));
    }

    private void setup(Control control, Consumer<Double> function) {
        if (control instanceof TextField textField) {
            function.accept(Double.parseDouble(textField.getText()));
            bind(textField, function);
        } else if (control instanceof Slider slider) {
            function.accept(slider.getValue());
            slider.valueProperty().addListener((observable, oldValue, newValue) -> {
                double value = 0;
                try {
                    value = newValue.doubleValue();
                } finally {
                    function.accept(value);
                }
            });
        }
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