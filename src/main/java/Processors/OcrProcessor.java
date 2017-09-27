package Processors;

import Entities.Controllers;
import ViewControllers.MainController;
import ViewControllers.PreferencesController;
import org.bytedeco.javacpp.BytePointer;
import org.languagetool.JLanguageTool;
import org.languagetool.language.BritishEnglish;
import org.languagetool.rules.RuleMatch;
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

    public static String getOcrText(String imagePath) throws IOException {
        BytePointer outText;
        TessBaseAPI api = new TessBaseAPI();

        // Setting ocr language
        if (api.Init("src/main/resources/OCR", PreferencesController.getLanguageMap().
                get(Controllers.getPreferencesController().ocrLanguage_combobox.getSelectionModel().getSelectedItem().toString())) != 0) {
            Controllers.getLogController().logTextArea.appendText("Could not initialize tesseract - ocr!\n");
            MainController.getLogStage().show();
        }
        if (!includeSymbols){
            api.SetVariable("tessedit_char_whitelist", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.?! ,");
        }

        // Open input image with leptonica library
        PIX image = pixRead(imagePath);
        api.SetImage(image);

        // Get OcrProcessor result
        outText = api.GetUTF8Text();
        if (outText == null) {
            Controllers.getLogController().logTextArea.appendText("OcrProcessor Text is NULL - Continuing forward\n");
            MainController.getLogStage().show();
            api.End();
            pixDestroy(image);
            return "";
        }
        String ocr_text = outText.getString().trim();

        // Destroy used object and release memory
        api.End();
        outText.deallocate();
        pixDestroy(image);

        StringBuffer buffer = new StringBuffer(ocr_text);
        JLanguageTool languageTool = new JLanguageTool(new BritishEnglish());
        List<RuleMatch> matches;

        int iterations = 0;
        while (true) {
            if (++iterations > 10) break;
            matches = languageTool.check(buffer.toString());
            if (!matches.isEmpty()){
                if (!matches.get(0).getSuggestedReplacements().isEmpty()){
                    try {
                        buffer.replace(matches.get(0).getFromPos(),matches.get(0).getToPos(),matches.get(0).getSuggestedReplacements().get(0));
                    } catch (StringIndexOutOfBoundsException ignored) {}
                } else {
                    buffer.replace(matches.get(0).getFromPos(),matches.get(0).getToPos(),"");
                }
            } else {
                ocr_text = buffer.toString();
                break;
            }
        }

        // Unique words
        if (extractUniqueWords){
            if (!uniqueWords.contains(ocr_text)){
                uniqueWords.add(ocr_text);
                return ocr_text + " ";
            }
            return "";
        }
        return ocr_text + " ";
    }

    public static void initUniqueWords(){
        uniqueWords = new TreeSet<>();
    }

    public static void setIncludeSymbols(boolean includeSymbols) {
        OcrProcessor.includeSymbols = includeSymbols;
    }

    public static void setExtractUniqueWords(boolean extractUniqueWords) {
        OcrProcessor.extractUniqueWords = extractUniqueWords;
    }
}
