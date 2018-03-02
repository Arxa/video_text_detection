import controllers.MainController;
import entities.ApplicationPaths;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import processors.FileProcessor;
import java.io.IOException;
import java.nio.file.Paths;

public class Main extends Application
{
    @Override
    public void start(Stage stage) throws Exception {
        try {
            ApplicationPaths.setApplicationPaths();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR,"Failed to set Application Paths").showAndWait();
            return;
        }

        FXMLLoader loader = new FXMLLoader(Paths.get(ApplicationPaths.RESOURCES_VIEWS,"main.fxml").toFile().toURI().toURL());
        Parent root = loader.load();
        stage.setTitle("VideoText Extractor");
        Scene scene = new Scene(root, 680, 390);
        scene.getStylesheets().add(Paths.get(ApplicationPaths.RESOURCES_CSS,"main.css").toFile().toURI().toURL().toExternalForm());
        stage.setScene(scene);
        stage.getIcons().add(new Image(Paths.get(ApplicationPaths.RESOURCES_ICONS,"app.png").toFile().toURI().toURL().toString()));
        stage.setResizable(false);
        stage.show();
        stage.setOnCloseRequest(e -> System.exit(0));

        MainController.setMainStage(stage);

        try {
            FileProcessor.loadLibraries();
        } catch (Throwable e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load libraries!"+e.getMessage(), ButtonType.OK).showAndWait();
            return;
        }

        stage.setOnCloseRequest(e -> {
            System.exit(0);
        });
    }

    public static void main(String[] args) {

        launch(args);
    }
}