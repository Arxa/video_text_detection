package Processors;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.processing.edges.CannyEdgeDetector;
import org.openimaj.image.processing.edges.StrokeWidthTransform;
import org.openimaj.image.text.extraction.swt.LetterCandidate;
import org.openimaj.image.text.extraction.swt.LineCandidate;
import org.openimaj.image.text.extraction.swt.SWTTextDetector;
import org.openimaj.image.text.extraction.swt.WordCandidate;

import java.io.File;
import java.io.IOException;

public class Test {


    // TODO New Filter Method: if the SWT returning > 0 bounding boxes, then the image contains, otherwise not. Parameters are important. Would be smart to use Unit Tests for large images to validate

    public static void test(){

//        final FImage image;
//        try {
//            image = ImageUtilities.readF(new File(
//                    "C:\\Users\\310297685\\IdeaProjects\\Thesis\\VideoText_Extractor\\src\\main\\resources\\Outputs\\MegaMan Thu Nov 02 11-02-18 GMT 2017\\OCR Images\\21.png"));
//        } catch (IOException e) {
//            e.printStackTrace();
//            return;
//        }
//
//        StrokeWidthTransform swt = new StrokeWidthTransform(false, new CannyEdgeDetector());  //canny sigma=1; thresholds automatically selected using same algorithm as Matlab implementation
//        swt.processImage(image);
//        DisplayUtilities.display(swt.normaliseImage(image));



        final SWTTextDetector detector = new SWTTextDetector();
        detector.getOptions().direction = SWTTextDetector.Direction.LightOnDark;
        detector.getOptions().minHeight = 0;
        detector.getOptions().minArea = 0;
        detector.getOptions().minLettersPerLine = 1;

        final MBFImage image;
        try {
            image = ImageUtilities.readMBF(new File(
                    "C:\\Users\\310297685\\IdeaProjects\\Thesis\\VideoText_Extractor\\src\\main\\resources\\Outputs\\MegaMan Fri Nov 03 12-22-43 GMT 2017\\OCR Images\\186.png"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        detector.analyseImage(image.flatten());



        for (final LineCandidate line : detector.getLines()) {
            image.drawShape(line.getRegularBoundingBox(), 3, RGBColour.RED);
        }

        DisplayUtilities.display(image, "Filtered candidate letters, lines and words.");

    }

}
