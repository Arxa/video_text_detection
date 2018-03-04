package processors;

import controllers.MainController;
import entities.ApplicationPaths;
import entities.Controllers;
import controllers.SettingsController;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.bytedeco.javacpp.tesseract;
import org.languagetool.JLanguageTool;
import org.languagetool.language.BritishEnglish;
import org.languagetool.rules.RuleMatch;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by arxa on 27/4/2017.
 */

public class OcrProcessor
{
    private static JLanguageTool languageTool = new JLanguageTool(new BritishEnglish());
    private static Pattern pattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);

    public static void initializeOcr(tesseract.TessBaseAPI ocrApi) throws Exception {
        if (ocrApi.Init(ApplicationPaths.RESOURCES_OCR, SettingsController.getLanguageMap().
                get(Controllers.getSettingsController().ocrLanguage_combobox.getSelectionModel().getSelectedItem().toString())) != 0) {
            throw new Exception("Failed to set ocr language!");
        }
        /*if (!Controllers.getSettingsController().includeSpecialCharacters_checkbox.isSelected()){
            ocrApi.SetVariable("tessedit_char_blacklist", "`,#[];()!£\"$%^&\\²³²£§¶¤°¦<>|€");
        }*/
    }

    public static String removeSpecialCharacters(String ocrText){
        StringBuffer buffer = new StringBuffer(ocrText);
        Matcher matcher = pattern.matcher(buffer);
        while (matcher.find()){
            try {
                buffer.replace(matcher.start(), matcher.end(),"");
            } catch (StringIndexOutOfBoundsException ignored) {}
            matcher = pattern.matcher(buffer);
        }
        return buffer.toString();
    }

    public static String checkForSpelling(String ocrText) {
        List<RuleMatch> matches;
        StringBuffer buffer = new StringBuffer(ocrText);
        int iterations = 0;
        while (true) {
            if (++iterations > 3) return buffer.toString();
            try { matches = languageTool.check(buffer.toString()); }
            catch (IOException e) { return ocrText; }
            if (!matches.isEmpty()){
                if (!matches.get(0).getSuggestedReplacements().isEmpty()){
                    try {
                        buffer.replace(matches.get(0).getFromPos(), matches.get(0).getToPos(), matches.get(0).getSuggestedReplacements().get(0));
                    } catch (StringIndexOutOfBoundsException ignored) {}
                } else {
                    return " ";
                }
            } else {
                return buffer.toString();
            }
        }
    }
}