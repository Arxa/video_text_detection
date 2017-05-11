import Controllers.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.sf.ehcache.Status;

public class Main extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        SystemController.initSystem();
        //Parent root = FXMLLoader.load(getClass().getResource("Views/RootLayout.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Views/RootLayout.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("VideoText Extractor");
        primaryStage.setScene(new Scene(root, 1500, 750));
        primaryStage.show();

        ViewController.initViewController(loader);

        // Terminating all Threads when the application is closed.
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>()
        {
            @Override
            public void handle(WindowEvent e)
            {
                SystemController.closeSystem();
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

}
