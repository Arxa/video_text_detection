package Controllers;

import Models.FrameCorners;
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

    public void paintVideoCorners(ActionEvent actionEvent)
    {
        Task<Void> task = new Task<Void>()
        {
            @Override protected Void call() throws Exception
            {
                System.out.println("Painting Started");
                Visualizer.paintCorners(PixelProcessor.getFrameCornersList());
                return null;
            }

            @Override protected void succeeded() {
                super.succeeded();
                System.out.println("Painting Done");
            }
        };
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    public void showVideoWithCorners(ActionEvent actionEvent)
    {
        PixelProcessor.findStableAndMovingCorners();
        PixelProcessor.SortCorners();
        PixelProcessor.findMovingCorners();
        for (FrameCorners f : PixelProcessor.getFrameCornersList())
        {
            //Visualizer.paintTextArea(f.getStableCorners(),f.getFrame());
            Visualizer.paintTextArea(f.getQualifiedMovingCorners(),f.getFrame());
        }
        Player.playFramesWithCorners(PixelProcessor.getFrameCornersList(), pane2);
    }

    public void showResults(ActionEvent actionEvent)
    {
//        PixelProcessor.findTextAreas();
//        PixelProcessor.printResults();
    }
}


