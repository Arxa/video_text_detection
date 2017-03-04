import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import org.junit.Test;

/**
 * Created by arxa on 9/12/2016.
 */


public class MainTest
{
    @Test
    public void startTest() throws InterruptedException {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                new JFXPanel(); // Initializes the JavaFx Platform
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            new Main().start(new Stage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        /* Creating and initializing
                         your app.*/
                    }
                });
            }
        });
        thread.start();// Initialize the thread
        Thread.sleep(3000); // Time to use the app, with out this, the thread
        // will be killed before you can tell.
    }


}
