package Controllers;

import Models.CacheID;
;
import Models.Color;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import net.sf.ehcache.Element;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
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
                    Mat frame1 = new Mat();
                    Mat frame2 = new Mat();
                    boolean open1 = cap.read(frame1);
                    boolean open2 = cap.read(frame2);
                    if (open1){
                        PixelProcessor.findHarrisCorners(frame1,CacheID.FIRST_FRAME);
                    }
                    while (true)
                    {
                        if (open1 && open2) // Start reading video frames by pairs
                        {
                            PixelProcessor.findHarrisCorners(frame2,CacheID.SECOND_FRAME);

                            // Creating binary images with zeros (black)
                            Mat binaryFrame1 = new Mat(frame1.size(),CvType.CV_8UC1,new Scalar(0.0));
                            //Mat binaryFrame2 = new Mat(frame2.size(),CvType.CV_8UC1,new Scalar(0.0));

                            PixelProcessor.findStableAndMovingCorners();
                            PixelProcessor.findMovingCorners();

                            // Mark Stable Corners with White
                            PixelProcessor.paintCornersToBinaryImage(binaryFrame1);
                            Mat dilated1 = new Mat(binaryFrame1.rows(),binaryFrame1.cols(),binaryFrame1.type());
                            //Mat dilated2 = new Mat(binaryFrame2.rows(),binaryFrame2.cols(),binaryFrame2.type());

                            Imgproc.dilate(binaryFrame1,dilated1,Imgproc.getStructuringElement
                                    (Imgproc.MORPH_RECT, new Size(12.0,12.0))); // Defaults: 3x3 - Anchor: center
                            //Imgproc.dilate(binaryFrame2,dilated2,new Mat());
                            Writer.writeFrameAsImage(dilated1);
                            //Writer.writeFrameAsImage(dilated2);

                            PixelProcessor.sortCorners();
                            /*
                            for (Object key : SystemController.getCache().getKeys())
                            {
                                PixelProcessor.applyNormalDistribution(SystemController.getFromCache((int)key).getStableCorners());
                                PixelProcessor.cleanCorners(SystemController.getFromCache((int)key).getStableCorners());
                                //PixelProcessor.applyNormalDistribution(getFromCache((int)key).getQualifiedMovingCorners());
                                //PixelProcessor.cleanCorners(getFromCache((int)key).getQualifiedMovingCorners());
                            }*/
                            //for (Object key : SystemController.getCache().getKeys())
                            //{
                                Visualizer.paintCorners(SystemController.getFromCache(CacheID.FIRST_FRAME).
                                        getStableCorners(),SystemController.getFromCache(CacheID.FIRST_FRAME).getFrame(), Color.RED);
                                Visualizer.paintCorners(SystemController.getFromCache(CacheID.FIRST_FRAME).
                                        getQualifiedMovingCorners(),SystemController.getFromCache(CacheID.FIRST_FRAME).getFrame(),Color.BLUE);

                                /*Visualizer.paintTextArea(SystemController.getFromCache((int)key).
                                        getStableCorners(),SystemController.getFromCache((int)key).getFrame(),Color.GREEN);
                                Visualizer.paintTextArea(SystemController.getFromCache((int)key).
                                        getQualifiedMovingCorners(),SystemController.getFromCache((int)key).getFrame(),Color.GREEN);*/
                            //}
                            Writer.writeFramesToVideo();
                            System.out.println("Cycle completed");

                            // Switching frames and their caches
                            frame2.copyTo(frame1);
                            SystemController.getCache().put(new Element(CacheID.FIRST_FRAME,
                                    SystemController.getFromCache(CacheID.SECOND_FRAME)));
                            open2 = cap.read(frame2);
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
