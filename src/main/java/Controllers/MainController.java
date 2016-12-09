package Controllers;

import Models.FrameCorners;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.opencv.core.CvType;

import java.util.List;

import static Controllers.PixelProcessor.getFromCache;

public class MainController
{
    // View Elements
    public Pane pane;
    public Pane pane2;
    public Pane pane3;
    public Pane pane4;
    public Button grab;
    public Label label1;

    private static Cache cache;
    private static CacheManager cm;

    public MainController() {
    }

    public void playLocalVideo(ActionEvent actionEvent)
    {
        cache = cm.getCache("cache1");
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
                for (Object key : MainController.getCache().getKeys())
                {
                    Visualizer.paintCorners(getFromCache((int)key).getStableCorners(),getFromCache((int)key).getFrame(),"stable");
                    Visualizer.paintCorners(getFromCache((int)key).getQualifiedMovingCorners(),getFromCache((int)key).getFrame(),"moving");
                    Visualizer.paintTextArea(getFromCache((int)key).getStableCorners(),getFromCache((int)key).getFrame());
                    Visualizer.paintTextArea(getFromCache((int)key).getQualifiedMovingCorners(),getFromCache((int)key).getFrame());
                }
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
        Player.playFramesWithCorners(pane2);
    }


    public void findTextAreas(ActionEvent actionEvent)
    {
        Task<Void> task = new Task<Void>()
        {
            @Override protected Void call() throws Exception
            {
                System.out.println("Finding Text Areas...");
                PixelProcessor.findStableAndMovingCorners();
                PixelProcessor.SortCorners();
                PixelProcessor.findMovingCorners();
                return null;
            }

            @Override protected void succeeded() {
                super.succeeded();
                System.out.println("Done!");
                for (Object key : MainController.getCache().getKeys())
                {
                    PixelProcessor.applyNormalDistribution(getFromCache((int)key).getStableCorners());
                    PixelProcessor.applyNormalDistribution(getFromCache((int)key).getQualifiedMovingCorners());
                }
                /*for (FrameCorners f : PixelProcessor.getFrameCornersList()) {
                    PixelProcessor.cleanCorners(f.getQualifiedMovingCorners());
                }*/

                for (Object key : MainController.getCache().getKeys()){
                    PixelProcessor.cleanCorners(getFromCache((int)key).getStableCorners());
                }
            }
        };
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    public static Cache getCache() {
        return cache;
    }

    public static CacheManager getCm() {
        return cm;
    }

    public static void initCache() {
        cm  = CacheManager.newInstance();
    }
}


