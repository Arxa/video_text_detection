//package ControllersTests;
//
//import Controllers.MainController;
//import Controllers.PixelProcessor;
//import junitparams.JUnitParamsRunner;
//import org.junit.*;
//import org.junit.runner.RunWith;
//import org.junit.runners.MethodSorters;
//import org.opencv.core.Core;
//import org.opencv.core.Mat;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.videoio.VideoCapture;
//
//import static Controllers.PixelProcessor.getFromCache;
//
///**
// * Created by arxa on 9/12/2016.
// */
//
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//@RunWith(JUnitParamsRunner.class)
//public class PixelProcessor_findHarrisCorners_Test
//{
//    private Mat testSource;
//
//    @BeforeClass
//    public static void beforeTests() {
//        MainController.initCache();
//    }
//
//    @AfterClass
//    public static void afterTests() {
//        MainController.getCm().shutdown();
//    }
//
//    @Before
//    public void beforeTest() {
//        PixelProcessor.setCacheId(0);
//    }
//
//    @After
//    public void afterTest() {
//        MainController.getCm().clearAll();
//    }
//
//    @Test
//    public void testAa_findHarrisCorners_videoWithNoCorners()
//    {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        VideoCapture cap = new VideoCapture("C:\\Users\\arxa\\Desktop\\InteliJ IDEA\\Thesis\\src\\test\\resources\\videoWthNoCorners.mp4");
//        while (true) {
//            Mat frame = new Mat();
//            if (cap.read(frame)) {
//                PixelProcessor.findHarrisCorners(frame);
//            }
//            else break;
//        }
//        Assert.assertTrue("cornersList should be empty", getFromCache(0).getCornersList().isEmpty());
//    }
//
//    @Test
//    public void testAb_findHarrisCorners_videoWithCorners()
//    {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        VideoCapture cap = new VideoCapture("C:\\Users\\arxa\\Desktop\\InteliJ IDEA\\Thesis\\src\\test\\resources\\videoWithStableCorners.mp4");
//        while (true) {
//            Mat frame = new Mat();
//            if (cap.read(frame)) {
//                PixelProcessor.findHarrisCorners(frame);
//            }
//            else break;
//        }
//        Assert.assertTrue("cornersList shouldn't be empty",!getFromCache(0).getCornersList().isEmpty());
//    }
//
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
////    public Object framesWithMovingCorners() {
////
////        return new Object[]{testSource1};
////    }
//
//    public Object framesWithStableCorners() {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        String path = "src\\test\\resources\\frameWithStableCorners1.png";
//        Mat testSource1 = Imgcodecs.imread(path);
//        return new Object[]{testSource1};
//    }
//
//
////    @Test
////    public void testAb_findHarrisCorners_frameWithCorners()
////    {
////        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
////        String path = "src\\test\\resources\\frameWithCorners.png";
////        testSource = Imgcodecs.imread(path);
////        if (testSource.empty()){
////            fail("Couldn't load test frame");
////        }
////        PixelProcessor.findHarrisCorners(testSource);
////        Assert.assertTrue("cornersList should not be empty",!PixelProcessor.getFromCache(0).getCornersList().isEmpty());
////    }
//
//}
