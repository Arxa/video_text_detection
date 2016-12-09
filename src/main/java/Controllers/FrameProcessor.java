package Controllers;

import javafx.concurrent.Task;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;

/**
 * Created by arxa on 16/11/2016.
 */

public class FrameProcessor
{
    private static VideoCapture cap;
    private static Mat frame;

    // TODO Handle the Exceptions
    public static void grabVideoFileFrames()
    {
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        PixelProcessor.initializeList();
        cap = new VideoCapture();
        cap.open(Player.getFilename().toString());

        if (cap != null && cap.isOpened())
        {
            Task<Void> task = new Task<Void>()
            {
                @Override protected Void call() throws Exception
                {
                    while (true) // Grabbing and passing the video frames by one
                    {
                        frame = new Mat();
                        if (cap.read(frame))
                        {
                            PixelProcessor.findHarrisCorners(frame);
                            System.out.println("finding corners. . .");
                        }
                        else return null;
                    }
                }

                @Override protected void succeeded() {
                    super.succeeded();
                    System.out.println("Done");
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
