package Processors;

import Entities.Controllers;
import ViewControllers.PreferencesController;
import com.ibm.icu.util.LocaleData;
import org.bytedeco.javacpp.BytePointer;


import java.io.IOException;
import java.util.*;

import static org.bytedeco.javacpp.lept.*;
import static org.bytedeco.javacpp.tesseract.TessBaseAPI;

/**
 * Created by arxa on 27/4/2017.
 */

public class OcrProcessor
{
    private static boolean includeSymbols = Controllers.getPreferencesController().includeSymbols_checkbox.isSelected();
    private static boolean extractUniqueWords = Controllers.getPreferencesController().extractUniqueWords_checkbox.isSelected();
    private static Set<String> uniqueWords;

    public static String getOcrText(String imagePath)
    {

        BytePointer outText;
        TessBaseAPI api = new TessBaseAPI();

        // Initialize tesseract-getOcrText with English, without specifying tessdata path
        if (api.Init("src/main/resources/OCR", PreferencesController.getLanguageMap().
                get(Controllers.getPreferencesController().ocrLanguage_combobox.getSelectionModel().getSelectedItem().toString())) != 0) {
            System.err.println("Could not initialize tesseract.");
            System.exit(1);
        }
        if (!includeSymbols){
            api.SetVariable("tessedit_char_whitelist", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.?!");
            //api.SetVariable("tessedit_char_blacklist", "\\/¬£$%^&*()[]|€€²³§£®£§§£¶¤¶¤°/-+");
            //api.SetVariable("tessedit_char_whitelist", "ΑΒΓΔΕΖΗΘΙΚΛΜΝΞΟΠΡΣΤΥΦΧΨΩ0123456789.?!");

        }

        // Open input image with leptonica library
        PIX image = pixRead(imagePath);
        api.SetImage(image);

        // Get OcrProcessor result
        outText = api.GetUTF8Text();
        if (outText == null) {
            System.out.println("OcrProcessor TEXT IS NULL");
            api.End();
            pixDestroy(image);
            return "";
        }
        String ocr_text = outText.getString();
        System.out.println("OcrProcessor output:\n" + ocr_text);

        // Destroy used object and release memory
        api.End();
        outText.deallocate();
        pixDestroy(image);

        // Unique words
        if (extractUniqueWords){
            if (!uniqueWords.contains(ocr_text)){
                uniqueWords.add(ocr_text);
                return ocr_text;
            }
            return "";
        }
        return ocr_text;
    }


    public static void initUniqueWords(){
        uniqueWords = new TreeSet<>();
    }

    public static boolean isIncludeSymbols() {
        return includeSymbols;
    }

    public static void setIncludeSymbols(boolean includeSymbols) {
        OcrProcessor.includeSymbols = includeSymbols;
    }

    public static Set<String> getUniqueWords() {
        return uniqueWords;
    }

    public static boolean isExtractUniqueWords() {
        return extractUniqueWords;
    }

    public static void setExtractUniqueWords(boolean extractUniqueWords) {
        OcrProcessor.extractUniqueWords = extractUniqueWords;
    }
}
