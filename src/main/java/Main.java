import Entities.ApplicationPaths;
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
import org.apache.commons.lang3.SystemUtils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.lang.reflect.Field;

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
        stage.setOnCloseRequest(e -> Platform.exit());

        stage.getIcons().add(new Image(Main.class.getResourceAsStream("Icons/app.png")));
        Controllers.setMainController(loader);
        MainController.setMainStage(stage);

        FileProcessor.loadLibraries();

        stage.setOnCloseRequest(e -> {
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }


}
