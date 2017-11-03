package Processors;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.text.extraction.swt.LineCandidate;
import org.openimaj.image.text.extraction.swt.SWTTextDetector;
import org.openimaj.image.text.extraction.swt.WordCandidate;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Stroke Width Transform
 */
public class SWT {

    public static boolean containsText(Mat mat)
    {
        final SWTTextDetector detector = new SWTTextDetector();
        detector.getOptions().direction = SWTTextDetector.Direction.LightOnDark;
        detector.getOptions().minHeight = 0;
        detector.getOptions().minArea = 0;
        detector.getOptions().minLettersPerLine = 1;

        final MBFImage image;
        try {
            image = ImageUtilities.readMBF(matToInputStream(mat));
            //image = ImageUtilities.readMBF(new File("C:\\Users\\310297685\\IdeaProjects\\Thesis\\VideoText_Extractor\\src\\main\\resources\\Outputs\\MegaMan Fri Nov 03 13-25-26 GMT 2017\\Temp\\25.png"));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        detector.analyseImage(image.flatten());
        if (detector.getLines().size() > 0){
            for (LineCandidate line : detector.getLines()){
                for (WordCandidate word : line.getWords()){
                    if (word.getRegularBoundingBox().height * word.getRegularBoundingBox().width > mat.height() * mat.width() * 0.05){
                        return true;
                    }
                }
            }
        }
        else {
            detector.getOptions().direction = SWTTextDetector.Direction.DarkOnLight;
            detector.analyseImage(image.flatten());
            if (detector.getLines().size() > 0){
                for (LineCandidate line : detector.getLines()){
                    for (WordCandidate word : line.getWords()){
                        if (/*word.getRegularBoundingBox().height * word.getRegularBoundingBox().width > mat.height() * mat.width() * 0.05*/true){
                            return true;
                        }
                    }
                }
            }
            else return false;
        }
        return false;
    }

    private static InputStream matToInputStream(Mat mat){
        MatOfByte bytemat = new MatOfByte();
        Imgcodecs.imencode(".png", mat, bytemat);
        byte[] bytes = bytemat.toArray();
        return new ByteArrayInputStream(bytes);
    }

}
