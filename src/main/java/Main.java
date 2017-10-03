import Entities.Controllers;
import Processors.FileProcessor;
import ViewControllers.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import sun.misc.Launcher;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Main extends Application
{
    @Override
    public void start(Stage stage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Views/main.fxml"));
        Parent root = loader.load();
        stage.setTitle("VideoText Extractor");
        stage.setScene(new Scene(root, 680, 450));
        stage.show();

        stage.getIcons().add(new Image(Main.class.getResourceAsStream("Icons/app.png")));
        Controllers.setMainController(loader);
        MainController.setMainStage(stage);

        FileProcessor.loadLibraries();

        // Terminating all Threads when the application is closed. ?
        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }


}
