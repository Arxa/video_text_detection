package Controllers;

import Models.CacheID;
import Models.Color;;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;


/**
 * Created by arxa on 16/11/2016.
 */

public class VideoProcessor
{
    private static VideoCapture cap;

    // TODO Handle the Exceptions
    public static void processVideo(Pane pane2, Label label1)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // Required for OpenCV libraries usage
        cap = new VideoCapture();
        cap.open(Player.getFilename().toString());

        if (cap != null && cap.isOpened())
        {
            Task<Void> task = new Task<Void>()
            {
                @Override protected Void call() throws Exception
                {
                    Writer.initializeVideoWriter();
                    while (true)
                    {
                        Mat frame1 = new Mat();
                        Mat frame2 = new Mat();
                        if (cap.read(frame1) && cap.read(frame2)) // Start reading video frames by pairs
                        {
                            PixelProcessor.findHarrisCorners(frame1, CacheID.FIRST_FRAME);
                            PixelProcessor.findHarrisCorners(frame2, CacheID.SECOND_FRAME);
                            PixelProcessor.findStableAndMovingCorners();
                            PixelProcessor.sortCorners();
                            PixelProcessor.findMovingCorners();
                            for (Object key : SystemController.getCache().getKeys())
                            {
                                PixelProcessor.applyNormalDistribution(SystemController.getFromCache((int)key).getStableCorners());
                                PixelProcessor.cleanCorners(SystemController.getFromCache((int)key).getStableCorners());
                                //PixelProcessor.applyNormalDistribution(getFromCache((int)key).getQualifiedMovingCorners());
                                //PixelProcessor.cleanCorners(getFromCache((int)key).getQualifiedMovingCorners());
                            }
                            for (Object key : SystemController.getCache().getKeys())
                            {
                                Visualizer.paintCorners(SystemController.getFromCache((int)key).
                                        getStableCorners(),SystemController.getFromCache((int)key).getFrame(), Color.RED);
                                Visualizer.paintCorners(SystemController.getFromCache((int)key).
                                        getQualifiedMovingCorners(),SystemController.getFromCache((int)key).getFrame(),Color.RED);
                                Visualizer.paintTextArea(SystemController.getFromCache((int)key).
                                        getStableCorners(),SystemController.getFromCache((int)key).getFrame(),Color.GREEN);
                                Visualizer.paintTextArea(SystemController.getFromCache((int)key).
                                        getQualifiedMovingCorners(),SystemController.getFromCache((int)key).getFrame(),Color.GREEN);
                            }
                            Writer.writeFrames();
                            System.out.println("Cycle completed");
                        }
                        else break;
                    } return null;
                }
                @Override protected void succeeded() {
                    super.succeeded();
                    SystemController.closeVideoHandlers();
                    System.out.println("Everything is done!");
                    ViewController.processIsComplete(pane2,label1);
                }
                @Override protected void cancelled() {
                    super.cancelled();
                    updateMessage("Thread has been cancelled!");
                }
                @Override protected void failed() {
                    super.failed();
                    updateMessage("Thread has failed!");
                }
            };
            Thread th = new Thread(task);
            th.setDaemon(true);
            th.start();
        }
    }

    public static VideoCapture getCap() {
        return cap;
    }
}
