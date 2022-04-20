package me.petrolingus.modsys.twosourceinterference;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("TSI");
        stage.setScene(scene);
        stage.show();
        stage.onCloseRequestProperty().addListener((observable, oldValue, newValue) -> {
            System.exit(0);
        });
    }

    public static void main(String[] args) throws Exception {
//        new Thread(() -> {
//            try {
//                new LwjglApplication().run();
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }).start();
        launch();
    }
}