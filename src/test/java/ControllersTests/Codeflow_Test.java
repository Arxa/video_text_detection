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
//import org.opencv.videoio.VideoCapture;
//
//import static Controllers.PixelProcessor.getFromCache;
//
///**
// * Created by arxa on 13/12/2016.
// */
//
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//@RunWith(JUnitParamsRunner.class)
//public class Codeflow_Test
//{
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
////    @Before
////    public void beforeTest() {
////        PixelProcessor.setCacheId(0);
////    }
//
//    @After
//    public void afterTest() {
//        MainController.getCm().clearAll();
//    }
//
//    @Test
//    public void testA_findHarrisCorners_videoWithNoCorners()
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
//        Assert.assertTrue("cornersList shouldn't be empty", !getFromCache(0).getCornersList().isEmpty());
//    }
//
//    @Test
//    public void testB_findStableAndMovingCorners()
//    {
//        PixelProcessor.findStableAndMovingCorners();
//        for (Object key : MainController.getCache().getKeys())
//        {
//            Assert.assertTrue("moving corners shouldn't exist",getFromCache((int)key).getMovingCorners().isEmpty());
//            Assert.assertTrue("stable corners shouldn't be empty",!getFromCache((int)key).getStableCorners().isEmpty());
//        }
//    }
//
////    @Test
////    public void testC_sortCorners()
////    {
////        PixelProcessor.sortCorners();
////        for (Object key : MainController.getCache().getKeys())
////        {
////            Assert.assertTrue("",getFromCache((int)key).getMovingCorners());
////            Assert.assertTrue("stable corners shouldn't be empty",!getFromCache((int)key).getStableCorners().isEmpty());
////        }
////        Assert.assertTrue("",)
////    }
//
//
//}
