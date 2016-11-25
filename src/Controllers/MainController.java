package Controllers;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class MainController
{
    // View Elements
    public Pane pane;
    public Pane pane2;
    public Pane pane3;
    public Pane pane4;
    public Button grab;
    public Label label1;


    public MainController() {
    }

    public void playLocalVideo(ActionEvent actionEvent)
    {
        Player.playLocalVideo(label1, pane);
    }

    public void grabVideoFileFrames(ActionEvent actionEvent)
    {
        FrameProcessor.grabVideoFileFrames();
    }

    public void playVideoWithCorners(ActionEvent actionEvent)
    {
        Task<Void> task = new Task<Void>()
        {
            @Override protected Void call() throws Exception
            {
                Visualizer.paintCorners(PixelProcessor.getFrameCornersList());
                System.out.println("Painting done.");
                return null;
            }
        };
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    public void showVideoWithCorners(ActionEvent actionEvent)
    {
        Player.playFramesWithCorners(PixelProcessor.getFrameCornersList(), pane2);
    }
}


