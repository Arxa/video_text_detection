package Processors;

import Entities.ApplicationPaths;
import Entities.Controllers;
import ViewControllers.MainController;
import ViewControllers.SettingsController;
import org.bytedeco.javacpp.BytePointer;
import org.jetbrains.annotations.NotNull;
import org.languagetool.JLanguageTool;
import org.languagetool.language.BritishEnglish;
import org.languagetool.rules.RuleMatch;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import static org.bytedeco.javacpp.lept.*;
import static org.bytedeco.javacpp.tesseract.TessBaseAPI;

/**
 * Created by arxa on 27/4/2017.
 */

public class OcrProcessor
{
    private static boolean includeSpecialCharacters;
    private static boolean extractUniqueWords;
    private static Set<String> uniqueWords;

    /**
     * Extracts the text representation in the given image, using OCR.
     * Depending of the user's selections, certain preprocessing and filtering of the ocr result is applied first
     * @param imagePath Path to the input image
     * @return The extracted String text
     */
    @NotNull
    public static String getOcrText(String imagePath) throws IOException, URISyntaxException
    {
        String selectedLanguage = Controllers.getSettingsController().ocrLanguage_combobox.getSelectionModel().getSelectedItem().toString();
        includeSpecialCharacters = Controllers.getSettingsController().includeSpecialCharacters_checkbox.isSelected();
        extractUniqueWords = Controllers.getSettingsController().extractUniqueWords_checkbox.isSelected();
        BytePointer outText;
        TessBaseAPI api = new TessBaseAPI();

        // Setting OCR language
        if (api.Init(ApplicationPaths.RESOURCES_OCR, SettingsController.getLanguageMap().get(selectedLanguage)) != 0) {
            Controllers.getLogController().logTextArea.appendText("Could not initialize tesseract - ocr!\n");
            MainController.getLogStage().show();
        }

        if (!includeSpecialCharacters){
            api.SetVariable("tessedit_char_blacklist", "`,#[];()!£\"$%^&\\²³²£§¶¤°¦<>|€");
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

        // Check for spelling errors and apply suggestion
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

        // Check if this word has appeared in the past
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

    public static void setIncludeSpecialCharacters(boolean includeSpecialCharacters) {
        OcrProcessor.includeSpecialCharacters = includeSpecialCharacters;
    }

    public static void setExtractUniqueWords(boolean extractUniqueWords) {
        OcrProcessor.extractUniqueWords = extractUniqueWords;
    }
}
