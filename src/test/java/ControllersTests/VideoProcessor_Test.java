//package ControllersTests;
//
//import Controllers.*;
//import Models.CacheID;
//import Models.Color;
//import junitparams.JUnitParamsRunner;
//import net.sf.ehcache.Element;
//import org.junit.*;
//import org.junit.runner.RunWith;
//import org.junit.runners.MethodSorters;
//import org.opencv.core.*;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.imgproc.Imgproc;
//import org.opencv.videoio.VideoCapture;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.Date;
//
//
///**
// * Created by arxa on 9/12/2016.
// */
//
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//@RunWith(JUnitParamsRunner.class)
//public class VideoProcessor_Test
//{
//    private static VideoCapture cap;
//    private Mat testFrame1;
//    private Mat testFrame2;
//    private static final String videoWithNoCorners = "C:\\Users\\arxa\\Desktop\\InteliJ IDEA\\Thesis\\src\\test\\resources\\videoWithNoCorners.mp4";
//
//    @BeforeClass
//    public static void beforeTests() {
//        SystemController.initSystem();
//    }
//
//    @AfterClass
//    public static void afterTests() {
//        SystemController.closeSystem();
//    }
//
//    @Before
//    public void beforeTest() {
//        Writer.setCurrentVideoFolderName("videoWithNoCorners".replace(".mp4","")+" "+
//                new Date().toString().replace(":","-"));
//        try {
//            Files.createDirectories(Paths.get(Writer.getFixedOutputPath()+Writer.getCurrentVideoFolderName()));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Writer.setBinaryFrameCounter(0);
//        Writer.initializeVideoWriter();
//    }
//
//    @After
//    public void afterTest() {
//        SystemController.closeVideoHandlers();
//    }
//
//    @Test
//    public void processVideo_VideoWithNoCorners_Test()
//    {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // Required for OpenCV libraries usage
//        cap = new VideoCapture(videoWithNoCorners);
//        if (cap.isOpened())
//        {
//            testFrame1 = new Mat();
//            testFrame2 = new Mat();
//            boolean open1 = cap.read(testFrame1);
//            boolean open2 = cap.read(testFrame2);
//            if (open1){
//                PixelProcessor.findHarrisCorners(testFrame1,CacheID.FIRST_FRAME);
//                Assert.assertTrue("Corner list should be empty",SystemController.
//                        getFromCache(CacheID.FIRST_FRAME).getCornersList().isEmpty());
//            }
//            while (true) {
//                if (open1 && open2) {
//                    PixelProcessor.findHarrisCorners(testFrame2, CacheID.SECOND_FRAME);
//                    Assert.assertTrue("Corner list should be empty", SystemController.
//                            getFromCache(CacheID.SECOND_FRAME).getCornersList().isEmpty());
//
//                    PixelProcessor.findStableAndMovingCorners();
//                    PixelProcessor.findMovingCorners();
//                    for (Object key : SystemController.getCache().getKeys()) {
//                        Assert.assertTrue("StableCorners list should be empty", SystemController.
//                                getFromCache((int) key).getStableCorners().isEmpty());
//                        Assert.assertTrue("MovingCorners list should be empty", SystemController.
//                                getFromCache((int) key).getMovingCorners().isEmpty());
//                        Assert.assertTrue("QualifiedCorners list should be empty", SystemController.
//                                getFromCache((int) key).getQualifiedMovingCorners().isEmpty());
//                    }
//
//                    Mat binaryFrame = new Mat(testFrame1.size(), CvType.CV_8UC1, new Scalar(0.0));
//                    Assert.assertTrue("No pixels should be painted",
//                            Visualizer.paintCornersToBinaryImage(binaryFrame) == 0);
//
//                    Mat dilated = new Mat(binaryFrame.rows(), binaryFrame.cols(), binaryFrame.type());
//                    Imgproc.dilate(binaryFrame, dilated, Imgproc.getStructuringElement
//                            (Imgproc.MORPH_RECT, new Size(12.0, 12.0))); // Defaults: 3x3 - Anchor: center
//                    Assert.assertTrue("Dilated image should be empty", dilated.empty());
//
//                    Assert.assertTrue("Image file should exist(?)",
//                            Writer.writeFrameAsImage(dilated).exists());
//
//                    PixelProcessor.sortCorners();
//
//                    int stablePaintedCorners = Visualizer.paintCorners(SystemController.getFromCache(CacheID.FIRST_FRAME).
//                            getStableCorners(), SystemController.getFromCache(CacheID.FIRST_FRAME).getFrame(), Color.RED);
//                    Assert.assertTrue("stablePaintedCorners should be 0", stablePaintedCorners == 0);
//
//                    int qualifiedPaintedCorners = Visualizer.paintCorners(SystemController.getFromCache(CacheID.FIRST_FRAME).
//                            getQualifiedMovingCorners(), SystemController.getFromCache(CacheID.FIRST_FRAME).getFrame(), Color.BLUE);
//                    Assert.assertTrue("qualifiedPaintedCorners should be 0", qualifiedPaintedCorners == 0);
//
//                    Writer.writeFramesToVideo();
//                    File video = new File(Writer.getFullVideoPath());
//                    Assert.assertTrue("Video File should exist", video.exists());
//
//                    // Switching frames and their caches
//                    testFrame2.copyTo(testFrame1);
//                    SystemController.getCache().put(new Element(CacheID.FIRST_FRAME,
//                            SystemController.getFromCache(CacheID.SECOND_FRAME)));
//                    open2 = cap.read(testFrame2);
//                } else break;
//            }
//        }
//    }
//
//
//    public Object framesWithNoCorners() {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        String path = "src\\test\\resources\\frameWithNoCorners1.png";
//        Mat testSource1 = Imgcodecs.imread(path);
//        return new Object[]{testSource1};
//    }
//
//    public Object framesWithCorners() {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        String path = "src\\test\\resources\\frameWithCorners1.png";
//        Mat testSource1 = Imgcodecs.imread(path);
//        return new Object[]{testSource1};
//    }
//
//
//    public Object framesWithStableCorners() {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        String path = "src\\test\\resources\\frameWithStableCorners1.png";
//        Mat testSource1 = Imgcodecs.imread(path);
//        return new Object[]{testSource1};
//    }
//
//
//}
