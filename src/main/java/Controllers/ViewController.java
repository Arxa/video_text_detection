package Controllers;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

/**
 * Created by arxa on 26/2/2017.
 */

public class ViewController
{
    public static void processIsComplete(Pane pane2, Label label1)
    {
        label1.setText("Processing Completed: 100%");
        label1.setStyle("-fx-text-fill: green");
        Player.playProcessedVideo(pane2,Writer.getProcessedVideo().getPath());
    }
}
