package Controllers;

import javafx.concurrent.Task;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

/**
 * Created by arxa on 16/11/2016.
 */

public class FrameProcessor
{
    private static VideoCapture cap;

    // TODO Handle the Exceptions
    public static void grabVideoFileFrames()
    {
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        cap = new VideoCapture();
        cap.open(Player.getFilename().toString());

        if (cap != null && cap.isOpened())
        {
            Task<Void> task = new Task<Void>()
            {
                @Override protected Void call() throws Exception
                {
                    while (true)
                    {
                        Mat frame1 = new Mat();
                        Mat frame2 = new Mat();
                        if (cap.read(frame1) && cap.read(frame2))
                        {
                            PixelProcessor.findMatchedCorners(frame1, frame2);
                            System.out.println("finding corners...");
                        }
                        else break;
                    }
                    return null;
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
