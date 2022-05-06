package me.petrolingus.modsys.twosourceinterference;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("TSI");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        stage.showingProperty().addListener((observable, oldValue, newValue) -> System.exit(0));
    }

    public static void main(String[] args) {
        launch();
    }
}