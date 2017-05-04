package Controllers;

import org.bytedeco.javacpp.*;
import static org.bytedeco.javacpp.lept.*;
import static org.bytedeco.javacpp.tesseract.*;
import static org.junit.Assert.assertTrue;

/**
 * Created by arxa on 27/4/2017.
 */

public class OCR
{
    public static String applyOCR(String matPath)
    {
        BytePointer outText;

        TessBaseAPI api = new TessBaseAPI();

        // Initialize tesseract-applyOCR with English, without specifying tessdata path
        if (api.Init("src/main/resources/", "ENG") != 0) {
            System.err.println("Could not initialize tesseract.");
            System.exit(1);
        }

        // Open input image with leptonica library
        PIX image = pixRead(matPath);
        api.SetImage(image);

        // Get OCR result
        outText = api.GetUTF8Text();
        String ocr_text = outText.getString();
        System.out.println("OCR output:\n" + ocr_text);

        // Destroy used object and release memory
        api.End();
        outText.deallocate();
        pixDestroy(image);

        return ocr_text;
    }
}
