import entities.Controllers;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import processors.FileProcessor;
import controllers.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application
{
    @Override
    public void start(Stage stage)
    {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Views/main.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK).showAndWait();
            return;
        }
        stage.setTitle("VideoText Extractor");
        stage.setScene(new Scene(root, 680, 390));
        stage.setResizable(false);
        stage.show();
        stage.setOnCloseRequest(e -> Platform.exit());

        stage.getIcons().add(new Image(Main.class.getResourceAsStream("Icons/app.png")));
        Controllers.setMainController(loader);
        MainController.setMainStage(stage);

        try {
            FileProcessor.loadLibraries();
        } catch (Throwable e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK).showAndWait();
            return;
        }

        stage.setOnCloseRequest(e -> {
            System.exit(0);
        });
    }

    public static void main(String[] args) throws Exception {
        launch(args);
    }
}